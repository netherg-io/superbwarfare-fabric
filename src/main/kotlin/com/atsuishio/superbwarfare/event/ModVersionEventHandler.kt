package com.atsuishio.superbwarfare.event

import org.apache.maven.artifact.versioning.ArtifactVersion

/**
 * ponytail: у NeoForge ModMismatchEvent аналога нет ни в Fabric API, ни в Porting Lib --
 * Fabric вообще не хранит версии модов предыдущего запуска. Поля остались, их читает
 * ClientEventHandler.onPlayerLoggedIn; заполнять их будет нечему, пока версия прошлого
 * запуска не будет сохраняться самим модом. Регистрировать здесь нечего, init() нет.
 */
object ModVersionEventHandler {
    @JvmField
    var previousVersion: ArtifactVersion? = null

    @JvmField
    var currentVersion: ArtifactVersion? = null
}
