package com.atsuishio.superbwarfare.fabric

import com.atsuishio.superbwarfare.Mod
import com.atsuishio.superbwarfare.network.ClientPacketPayload
import com.atsuishio.superbwarfare.network.PayloadContext
import com.atsuishio.superbwarfare.network.payloadTypeMap
import com.atsuishio.superbwarfare.tools.createStreamCodec
import io.netty.buffer.Unpooled
import kotlinx.serialization.Serializable
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientEntityEvents
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.fabricmc.fabric.api.networking.v1.EntityTrackingEvents
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.server.MinecraftServer

/**
 * Замена Entity#onAddedToLevel/onRemovedFromLevel из NeoForge.
 *
 * Реализации ничего не вызывают через super: на Fabric колбэки раздаёт [EntityHooks.init],
 * поэтому пустые тела здесь — единственная база.
 */
interface LevelLifecycleListener {
    fun onAddedToLevel() {}

    fun onRemovedFromLevel() {}
}

/**
 * Замена IEntityWithComplexSpawn из NeoForge: доп. данные, которые сущность досылает клиенту
 * в момент, когда тот начинает её отслеживать.
 */
interface IEntityWithComplexSpawn {
    fun writeSpawnData(buffer: RegistryFriendlyByteBuf)

    fun readSpawnData(additionalData: RegistryFriendlyByteBuf)
}

/**
 * Пакет с данными спавна. Отдельный тип, а не поле в ClientboundAddEntityPacket:
 * ванильный пакет несёт только один int, а технике нужны углы башни и состояние шасси.
 */
@Serializable
data class EntitySpawnDataMessage(val entityId: Int, val data: ByteArray) : ClientPacketPayload() {
    override fun PayloadContext.handler() {
        val level = player().level()
        // ponytail: сущность приходит первой (START_TRACKING шлётся после пакета спавна).
        // Если её всё же нет — данные теряются; тогда слать по запросу клиента.
        val entity = level.getEntity(entityId) as? IEntityWithComplexSpawn ?: return
        entity.readSpawnData(RegistryFriendlyByteBuf(Unpooled.wrappedBuffer(data), level.registryAccess()))
    }

    override fun equals(other: Any?) = this === other ||
            (other is EntitySpawnDataMessage && entityId == other.entityId && data.contentEquals(other.data))

    override fun hashCode() = 31 * entityId + data.contentHashCode()
}

object EntityHooks {
    /** Активный сервер: на Fabric статического доступа к нему нет, а рассылка «всем» его требует. */
    @JvmStatic
    var server: MinecraftServer? = null
        private set

    val SPAWN_DATA_TYPE = CustomPacketPayload.Type<EntitySpawnDataMessage>(Mod.loc("entity_spawn_data"))

    /** Вызывается из Mod.onInitialize. */
    fun init() {
        val codec: StreamCodec<in RegistryFriendlyByteBuf, EntitySpawnDataMessage> = createStreamCodec<EntitySpawnDataMessage>()
        payloadTypeMap[EntitySpawnDataMessage::class.java] = SPAWN_DATA_TYPE
        PayloadTypeRegistry.playS2C().register(SPAWN_DATA_TYPE, codec)

        ServerLifecycleEvents.SERVER_STARTED.register { server = it }
        ServerLifecycleEvents.SERVER_STOPPED.register { server = null }

        ServerEntityEvents.ENTITY_LOAD.register { entity, _ -> (entity as? LevelLifecycleListener)?.onAddedToLevel() }
        ServerEntityEvents.ENTITY_UNLOAD.register { entity, _ ->
            (entity as? LevelLifecycleListener)?.onRemovedFromLevel()
        }

        EntityTrackingEvents.START_TRACKING.register { entity, player ->
            if (entity is IEntityWithComplexSpawn) {
                val buffer = RegistryFriendlyByteBuf(Unpooled.buffer(), player.registryAccess())
                entity.writeSpawnData(buffer)
                val data = ByteArray(buffer.readableBytes())
                buffer.readBytes(data)
                ServerPlayNetworking.send(player, EntitySpawnDataMessage(entity.id, data))
            }
        }
    }

    /** Вызывается из ModClient.onInitializeClient после init. */
    fun initClient() {
        ClientPlayNetworking.registerGlobalReceiver(SPAWN_DATA_TYPE) { payload, context ->
            payload.handle { context.player() }
        }

        ClientEntityEvents.ENTITY_LOAD.register { entity, _ -> (entity as? LevelLifecycleListener)?.onAddedToLevel() }
        ClientEntityEvents.ENTITY_UNLOAD.register { entity, _ ->
            (entity as? LevelLifecycleListener)?.onRemovedFromLevel()
        }
    }
}
