package com.atsuishio.superbwarfare

import com.atsuishio.superbwarfare.client.MouseMovementHandler
import com.atsuishio.superbwarfare.client.renderer.ModParticleRenderTypes
import com.atsuishio.superbwarfare.client.renderer.molang.MolangVariable
import com.atsuishio.superbwarfare.init.ModSoundInstances
import com.atsuishio.superbwarfare.sound.SoundLimit
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents

object ModClient : ClientModInitializer {
    override fun onInitializeClient() {
        MouseMovementHandler.init()
        MolangVariable.register()
        ModSoundInstances.init()
        SoundLimit.init()
        ModParticleRenderTypes.registerShaders()

        ClientTickEvents.END_CLIENT_TICK.register { Mod.executeClientWork() }
    }
}
