package com.atsuishio.superbwarfare.init

import com.atsuishio.superbwarfare.Mod
import com.atsuishio.superbwarfare.perk.js.PerkDescriptor
import net.minecraft.core.Registry
import net.minecraft.resources.ResourceKey
import net.fabricmc.fabric.api.event.registry.DynamicRegistries

object ModDatapackRegistries {

    val PERKS_KEY: ResourceKey<Registry<PerkDescriptor>> =
        ResourceKey.createRegistryKey(Mod.loc("sbw/perks"))

    fun register() {
        DynamicRegistries.register(PERKS_KEY, PerkDescriptor.CODEC)
    }
}