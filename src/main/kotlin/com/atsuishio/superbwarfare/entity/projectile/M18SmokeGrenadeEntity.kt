package com.atsuishio.superbwarfare.entity.projectile

import com.atsuishio.superbwarfare.client.particle.CustomSmokeOption
import com.atsuishio.superbwarfare.init.ModEntities
import com.atsuishio.superbwarfare.init.ModItems
import com.atsuishio.superbwarfare.init.ModSounds
import com.atsuishio.superbwarfare.network.message.receive.ClientIndicatorMessage
import com.atsuishio.superbwarfare.tools.ParticleTool
import com.atsuishio.superbwarfare.tools.sendPacketTo
import com.atsuishio.superbwarfare.world.phys.ExtendedEntityRayTraceResult
import net.minecraft.core.Direction
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.sounds.SoundSource
import net.minecraft.util.Mth
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.Item
import net.minecraft.world.level.Level
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.EntityHitResult
import net.minecraft.world.phys.Vec3
import kotlin.math.min

open class M18SmokeGrenadeEntity : BounceProjectile, BasicGeoProjectileEntity {
    private var count = 8
    private var fuse = 100
    var red: Float = 1.0f
        private set
    var green: Float = 1.0f
        private set
    var blue: Float = 1.0f
        private set

    constructor(type: EntityType<out M18SmokeGrenadeEntity>, level: Level) : super(type, level)

    constructor(type: EntityType<out M18SmokeGrenadeEntity>, x: Double, y: Double, z: Double, world: Level) :
            super(type, x, y, z, world)

    constructor(entity: LivingEntity?, level: Level, fuse: Int) :
            super(ModEntities.M18_SMOKE_GRENADE.get(), entity, level) {
        this.fuse = fuse
    }

    init {
        this.damageValue = 1f
        this.headShotValue = 5f
    }

    override fun addAdditionalSaveData(compound: CompoundTag) {
        super.addAdditionalSaveData(compound)
        compound.putFloat("Fuse", this.fuse.toFloat())
        compound.putInt("Count", this.count)
        compound.putFloat("RColor", this.red)
        compound.putFloat("GColor", this.green)
        compound.putFloat("BColor", this.blue)
    }

    override fun readAdditionalSaveData(compound: CompoundTag) {
        super.readAdditionalSaveData(compound)
        if (compound.contains("Fuse")) {
            this.fuse = compound.getInt("Fuse")
        }
        if (compound.contains("Count")) {
            this.count = Mth.clamp(compound.getInt("Count"), 1, 64)
        }
        if (compound.contains("RColor")) {
            this.red = compound.getFloat("RColor")
        }
        if (compound.contains("GColor")) {
            this.green = compound.getFloat("GColor")
        }
        if (compound.contains("BColor")) {
            this.blue = compound.getFloat("BColor")
        }
    }

    override fun canPassThroughFluid() = true

    override fun getDefaultItem(): Item {
        return ModItems.M18_SMOKE_GRENADE.get()
    }

    override fun afterHitEntity(result: EntityHitResult) {
        if (result !is ExtendedEntityRayTraceResult) return
        val entity = result.entity
        val owner = this.owner
        if (entity == owner || entity == this.vehicle) return
        val speedE = this.deltaMovement.length()
        if (speedE > 0.1) {
            if (owner is ServerPlayer) {
                owner.level().playSound(
                    null,
                    owner.blockPosition(),
                    ModSounds.INDICATION.get(),
                    SoundSource.VOICE,
                    1f,
                    1f
                )

                sendPacketTo(owner, ClientIndicatorMessage(0, 5))
            }
        }
        this.bounce(
            Direction.getNearest(
                this.deltaMovement.x(),
                this.deltaMovement.y(),
                this.deltaMovement.z()
            ).opposite
        )
        this.deltaMovement = this.deltaMovement.multiply(0.25, 1.0, 0.25)
    }

    override fun afterHitBlock(result: BlockHitResult) {
        val resultPos = result.blockPos
        val state = this.level().getBlockState(resultPos)
        val block = state.block
        val event = state.soundType.breakSound
        val speed = this.deltaMovement.length()
        if (speed > 0.5) {
            val volume = min(4f, speed.toFloat() / 4f + 0.5f)
            this.level().playSound(
                null,
                result.getLocation().x,
                result.getLocation().y,
                result.getLocation().z,
                event,
                SoundSource.AMBIENT,
                volume,
                1f
            )
        }
        this.bounce(result.direction)
    }

    override fun tick() {
        super.tick()
        --this.fuse

        if (tickCount > CLOUD_TICKS) {
            this.discard()
        }

        if (fuse == -20) {
            releaseSmoke()
        }

        val level = this.level()
        if (fuse == 0) {
            level.playSound(null, this, ModSounds.SM0KE_GRENADE_RELEASE.get(), this.soundSource, 2f, 1f)
        }

        if (level is ServerLevel && fuse <= 0 && tickCount % 10 == 0) replayCloudToNewcomers(level)

        if (fuse <= 0 && tickCount <= EMIT_TICKS && tickCount % 2 == 0) {
            if (level is ServerLevel) {
                ParticleTool.sendParticle(
                    level,
                    CustomSmokeOption(this.red, this.green, this.blue),
                    this.x,
                    this.y + bbHeight,
                    this.z,
                    8,
                    0.075,
                    0.01,
                    0.075,
                    0.08,
                    true
                )
            }
        }

        if (level is ServerLevel) {
            ParticleTool.sendParticle(
                level, ParticleTypes.SMOKE, this.xo, this.yo, this.zo,
                1, 0.0, 0.0, 0.0, 0.01, true
            )
        }
    }

    /**
     * Blockfield: the cloud is nothing but long-lived client particles (600-800 ticks) emitted during the first
     * [EMIT_TICKS]. A player whose client world was rebuilt meanwhile (death -> respawn room in another dimension ->
     * back) lost them and saw no smoke at all while everyone else still did. The grenade now outlives its cloud and
     * hands every newly seen player one burst shaped like the already spread cloud.
     */
    private val served = HashSet<Int>()

    private fun replayCloudToNewcomers(level: ServerLevel) {
        for (player in level.players()) {
            // A respawned player is a new entity with a new id, which is exactly who needs the replay.
            if (player.distanceToSqr(this) > 256.0 * 256.0 || !served.add(player.id)) continue
            if (tickCount <= EMIT_TICKS) continue
            // ponytail: replayed particles start a fresh lifetime, so this player keeps the smoke up to
            // (tickCount - EMIT_TICKS) ticks longer than the others; an age field in CustomSmokeOption fixes that.
            level.sendParticles(
                player, CustomSmokeOption(this.red, this.green, this.blue), true,
                this.x, this.y + bbHeight + 1.5, this.z, 250, 2.5, 1.2, 2.5, 0.01
            )
        }
    }

    open fun releaseSmoke() {
        val vec3 = Vec3(1.0, 0.05, 0.0)

        for (i in 0..<this.count) {
            val decoy = SmokeDecoyEntity(ModEntities.SMOKE_DECOY.get(), this.level(), false)
            decoy.setPos(this.x, this.y + bbHeight, this.z)
            decoy.decoyShoot(this, vec3.yRot(i * (360f / this.count) * Mth.DEG_TO_RAD), 1.5f, 5f)
            this.level().addFreshEntity(decoy)
        }
    }

    override fun getCustomGravity(): Float {
        return 0.07f
    }

    fun setColor(r: Float, g: Float, b: Float): M18SmokeGrenadeEntity {
        this.red = r
        this.green = g
        this.blue = b
        return this
    }

    override fun isFastMoving(): Boolean {
        return false
    }

    companion object {
        /** Upstream lifetime: smoke is emitted until this tick. */
        private const val EMIT_TICKS = 200
        /** Emission plus the longest CustomSmokeParticle lifetime. */
        private const val CLOUD_TICKS = EMIT_TICKS + 800
    }
}
