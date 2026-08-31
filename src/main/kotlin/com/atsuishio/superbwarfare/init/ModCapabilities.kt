package com.atsuishio.superbwarfare.init

/**
 * Общая сторона: вызывается из Mod.init().
 *
 * Регистрировать нечего. На NeoForge поставщики capability прописывались здесь через
 * RegisterCapabilitiesEvent; на Fabric их роль взял com.atsuishio.superbwarfare.fabric.Capabilities,
 * где маркер сам содержит функцию поиска и разбирает владельца по типу в момент вызова. Ни
 * события, ни реестра, ни порядка инициализации это не требует.
 *
 * Объект оставлен, потому что Mod.kt его зовёт, а точка входа под будущие capability-подобные
 * вещи (например мост энергии наружу) пригодится.
 */
object ModCapabilities {
    fun init() = Unit
}
