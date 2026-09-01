package com.atsuishio.superbwarfare.event

/**
 * ponytail: у NeoForge ModMismatchEvent аналога нет ни в Fabric API, ни в Porting Lib --
 * Fabric вообще не хранит версии модов предыдущего запуска. Поля остались, их читает
 * ClientEventHandler.onPlayerLoggedIn; заполнять их будет нечему, пока версия прошлого
 * запуска не будет сохраняться самим модом. Регистрировать здесь нечего, init() нет.
 * Тип полей сведён к String: maven ArtifactVersion в classpath Fabric нет, а оба
 * потребителя поля только сравнивают с null и подставляют в текст.
 */
object ModVersionEventHandler {
    @JvmField
    var previousVersion: String? = null

    @JvmField
    var currentVersion: String? = null
}
