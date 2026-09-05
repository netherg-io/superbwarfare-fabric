package com.atsuishio.superbwarfare.network.message.send

import com.atsuishio.superbwarfare.init.ModTags
import com.atsuishio.superbwarfare.network.PayloadContext
import com.atsuishio.superbwarfare.network.ServerPacketPayload
import kotlinx.serialization.Serializable
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.EquipmentSlot

/**
 * ПНВ шлема: клиент шлёт состояние переключателя, сервер вешает бесконечный night_vision.
 * Снятие шлема ловит клиент (он же и присылает выключение) — серверного тика ради одного эффекта
 * не заводим.
 */
@Serializable
data class NightVisionMessage(val active: Boolean) : ServerPacketPayload() {
    override fun PayloadContext.handler() {
        val player = sender()

        if (active && player.getItemBySlot(EquipmentSlot.HEAD).`is`(ModTags.Items.HAS_NVG)) {
            player.addEffect(
                MobEffectInstance(
                    MobEffects.NIGHT_VISION,
                    MobEffectInstance.INFINITE_DURATION,
                    0,
                    false,
                    false,
                    false
                )
            )
        } else if (player.getEffect(MobEffects.NIGHT_VISION)?.isInfiniteDuration == true) {
            // Зелье ночного видения снимать нельзя — гасим только бесконечный эффект от ПНВ.
            player.removeEffect(MobEffects.NIGHT_VISION)
        }
    }
}
