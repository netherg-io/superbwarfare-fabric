package com.atsuishio.superbwarfare.init

import com.atsuishio.superbwarfare.Mod
import com.atsuishio.superbwarfare.fabric.DeferredHolder
import com.atsuishio.superbwarfare.fabric.DeferredRegister
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.entity.ai.attributes.Attribute
import net.minecraft.world.entity.ai.attributes.RangedAttribute
import java.util.function.Supplier

/** Общая сторона: вызывать из ModInitializer. */
object ModAttributes {
    val ATTRIBUTES: DeferredRegister<Attribute> =
        DeferredRegister.create(BuiltInRegistries.ATTRIBUTE, Mod.MODID)

    @JvmField
    val BULLET_RESISTANCE: DeferredHolder<Attribute, out Attribute> = ATTRIBUTES.register(
        "bullet_resistance",
        Supplier {
            (RangedAttribute(
                "attribute." + Mod.MODID + ".bullet_resistance",
                0.0,
                0.0,
                1.0
            )).setSyncable(true)
        })

    // Апстрим вешал BULLET_RESISTANCE на КАЖДЫЙ тип живой сущности через
    // EntityAttributeModificationEvent. У Fabric аналога нет: FabricDefaultAttributeRegistry задаёт
    // набор атрибутов только для новых типов и не умеет дополнять уже собранные ванильные.
    // Нужен миксин в DefaultAttributes.SUPPLIERS — делается вне этого файла.
    fun init() {
        ATTRIBUTES.register(null)
    }
}
