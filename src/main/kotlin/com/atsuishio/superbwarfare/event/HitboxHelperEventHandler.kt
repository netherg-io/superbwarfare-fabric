package com.atsuishio.superbwarfare.event

import com.atsuishio.superbwarfare.tools.HitboxHelper
import io.github.fabricators_of_create.porting_lib.entity.events.tick.PlayerTickEvent
import net.fabricmc.api.EnvType
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.server.level.ServerPlayer

object HitboxHelperEventHandler {
    fun init() {
        PlayerTickEvent.Post.EVENT.register { onPlayerTick(it) }
        ServerPlayConnectionEvents.DISCONNECT.register { handler, _ -> onPlayerLoggedOut(handler.player) }
    }

    private fun onPlayerTick(event: PlayerTickEvent.Post) {
        if (FabricLoader.getInstance().environmentType == EnvType.SERVER) {
            HitboxHelper.onPlayerTick(event.entity)
        }
    }

    private fun onPlayerLoggedOut(player: ServerPlayer) {
        HitboxHelper.onPlayerLoggedOut(player)
    }
}
