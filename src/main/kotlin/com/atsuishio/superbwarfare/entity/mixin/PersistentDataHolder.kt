package com.atsuishio.superbwarfare.entity.mixin

import net.minecraft.nbt.CompoundTag
import net.minecraft.world.entity.Entity

/**
 * Замена NeoForge-расширения `Entity#getPersistentData()`.
 * Реализуется миксином [com.atsuishio.superbwarfare.mixins.EntityMixin] на [Entity].
 */
@Suppress("FunctionName")
interface PersistentDataHolder {
    fun `sbw$getPersistentData`(): CompoundTag
}

/**
 * Произвольный NBT, живущий на сущности между сохранениями.
 * Как и в NeoForge, на клиент не синхронизируется.
 */
val Entity.persistentData: CompoundTag
    get() = (this as PersistentDataHolder).`sbw$getPersistentData`()
