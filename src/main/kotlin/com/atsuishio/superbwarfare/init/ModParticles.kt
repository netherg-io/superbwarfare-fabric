package com.atsuishio.superbwarfare.init

import com.atsuishio.superbwarfare.client.particle.*
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry

/** Клиентская сторона: вызывать из ClientModInitializer. */
object ModParticles {
    fun init() {
        val registry = ParticleFactoryRegistry.getInstance()

        registry.register(ModParticleTypes.FIRE_STAR.get()) { FireStarParticle.provider(it) }
        registry.register(ModParticleTypes.EXPLOSION_DEBRIS.get()) { ExplosionDebrisParticle.Provider(it) }
        registry.register(ModParticleTypes.WHITE_STAR.get()) { WhiteStarParticle.provider(it) }
        registry.register(ModParticleTypes.RISING_SMOKE.get()) { RisingSmokeParticle.provider(it) }
        registry.register(ModParticleTypes.BULLET_DECAL.get(), BulletDecalParticle.Provider())
        registry.register(ModParticleTypes.CUSTOM_CLOUD.get()) { CustomCloudParticle.Provider(it) }
        registry.register(ModParticleTypes.CUSTOM_SMOKE.get()) { CustomSmokeParticle.Provider(it) }
        registry.register(ModParticleTypes.CANNON_MUZZLE_FLARE.get()) { CannonMuzzleFlareParticle.Provider(it) }
        registry.register(ModParticleTypes.CUSTOM_FLARE.get()) { CustomFlareParticle.Provider(it) }
    }
}
