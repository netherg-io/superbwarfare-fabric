package com.atsuishio.superbwarfare.compat

import com.atsuishio.superbwarfare.Mod
import com.atsuishio.superbwarfare.compat.clothconfig.ClothConfigHelper
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.ModList
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.fml.event.lifecycle.InterModEnqueueEvent
import net.fabricmc.api.EnvType
import net.fabricmc.loader.api.FabricLoader

@EventBusSubscriber(modid = Mod.MODID)
object CompatHolder {
    const val DMV: String = "dreamaticvoyage"
    const val VRC: String = "virtuarealcraft"
    const val CLOTH_CONFIG: String = "cloth_config"

    @SubscribeEvent
    fun onInterModEnqueue(event: InterModEnqueueEvent) {
        event.enqueueWork {
            hasMod(CLOTH_CONFIG) {
                if (FabricLoader.getInstance().environmentType == EnvType.CLIENT) {
                    ClothConfigHelper.registerScreen()
                }
            }
        }
    }

    fun hasMod(modid: String, runnable: Runnable) {
        if (ModList.get().isLoaded(modid)) {
            runnable.run()
        }
    }
}
