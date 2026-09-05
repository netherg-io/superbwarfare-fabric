package com.atsuishio.superbwarfare.client

import com.atsuishio.superbwarfare.Mod
import com.atsuishio.superbwarfare.init.ModKeyMappings
import com.atsuishio.superbwarfare.init.ModSounds
import com.atsuishio.superbwarfare.init.ModTags
import com.atsuishio.superbwarfare.mixins.GameRendererInvoker
import com.atsuishio.superbwarfare.network.message.send.NightVisionMessage
import com.atsuishio.superbwarfare.tools.sendPacketToServer
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.minecraft.client.Minecraft
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.player.Player

/**
 * ПНВ шлема, как в старом fracturepoint: клавиша включает зелёный пост-шейдер на клиенте, сервер
 * по тому же переключателю вешает night_vision. Анимации откидывания монокуляра нет — на моделях
 * шлемов SBW монокуляра тоже нет.
 */
@Environment(EnvType.CLIENT)
object NvgHandler {
    private val mc get() = Minecraft.getInstance()

    private var active = false

    /** После входа в мир гасим возможный «залипший» бесконечный эффект от прошлой сессии. */
    private var needsSync = true

    fun init() {
        ClientTickEvents.END_CLIENT_TICK.register { _ -> tick() }
    }

    private fun hasNvgHelmet(player: Player) =
        player.getItemBySlot(EquipmentSlot.HEAD).`is`(ModTags.Items.HAS_NVG)

    private fun tick() {
        val player = mc.player
        if (player == null) {
            active = false
            needsSync = true
            return
        }

        if (needsSync) {
            needsSync = false
            sendPacketToServer(NightVisionMessage(false))
        }

        var toggled = false
        while (ModKeyMappings.TOGGLE_NVG.consumeClick()) {
            toggled = true
        }

        if (toggled && (active || hasNvgHelmet(player))) {
            setActive(!active)
        } else if (active && !hasNvgHelmet(player)) {
            setActive(false)
        }
    }

    private fun setActive(value: Boolean) {
        active = value
        sendPacketToServer(NightVisionMessage(value))

        if (value) {
            (mc.gameRenderer as GameRendererInvoker).callLoadEffect(Mod.loc("shaders/post/nvg.json"))
            mc.player?.playSound(ModSounds.NIGHT_VISION_ACTIVATE.get(), 1f, 1f)
        } else if (mc.gameRenderer.currentEffect() != null) {
            // ponytail: post-эффект в ваниле один на всех, поэтому гасим текущий целиком.
            // Если тепловизор и ПНВ начнут включать одновременно — понадобится общий стек эффектов.
            mc.gameRenderer.shutdownEffect()
        }
    }
}
