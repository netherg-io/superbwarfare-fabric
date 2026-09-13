plugins {
    kotlin("jvm") version "2.1.20"
    application
}
repositories { mavenCentral() }
kotlin {
    jvmToolchain(21)
    sourceSets.main {
        kotlin.srcDir("../../src/main/kotlin/com/atsuishio/superbwarfare/network/security")
        kotlin.srcDir(".")
        kotlin.include("DroneControlPolicy.kt", "DroneControlPolicyTest.kt")
    }
    compilerOptions { allWarningsAsErrors.set(true) }
}
application { mainClass.set("DroneControlPolicyTestKt") }
