plugins {
    idea
    id("java-library")
    id("fabric-loom") version "1.11-SNAPSHOT"
    id("org.jetbrains.kotlin.jvm") version "2.1.20"
    id("org.jetbrains.kotlin.plugin.serialization") version "2.1.20"
    id("com.google.devtools.ksp") version "2.1.20-2.0.1"
}

version = "${project.property("mod_version")}-mc${project.property("minecraft_version")}"
group = "com.atsuishio.superbwarfare"

base {
    archivesName.set(project.property("mod_id") as String)
}

java {
    toolchain { languageVersion.set(JavaLanguageVersion.of(21)) }
}

kotlin {
    jvmToolchain(21)
}

loom {
    mixin {
        defaultRefmapName.set("mixins.superbwarfare.refmap.json")
    }
}

repositories {
    mavenLocal()
    mavenCentral()
    maven { url = uri("https://maven.parchmentmc.org") }
    maven { url = uri("https://api.modrinth.com/maven") }
    maven { url = uri("https://maven.createmod.net") }
    maven { url = uri("https://raw.githubusercontent.com/Fuzss/modresources/main/maven/") }
    maven { url = uri("https://maven.shedaniel.me/") }
    maven { url = uri("https://mvn.devos.one/snapshots/") }
    maven { url = uri("https://jitpack.io") }   // Fabric-ASM, транзитивная у Porting Lib
    maven {
        name = "GeckoLib"
        url = uri("https://dl.cloudsmith.io/public/geckolib3/geckolib/maven/")
        content {
            includeGroupByRegex("software\\.bernie.*")
            includeGroup("com.eliotlash.mclib")
        }
    }
    flatDir { dir("libs") }
}

dependencies {
    minecraft("com.mojang:minecraft:${project.property("minecraft_version")}")
    // Те же маппинги, что у апстрима на NeoForge: имена классов и методов при переезде не меняются,
    // поэтому миксины и весь ванильный слой переносятся почти дословно.
    mappings(loom.layered {
        officialMojangMappings()
        parchment("org.parchmentmc.data:parchment-${project.property("parchment_minecraft_version")}:${project.property("parchment_mappings_version")}@zip")
    })
    modImplementation("net.fabricmc:fabric-loader:${project.property("loader_version")}")
    modImplementation("net.fabricmc.fabric-api:fabric-api:${project.property("fabric_version")}")
    modImplementation("net.fabricmc:fabric-language-kotlin:${project.property("flk_version")}")

    ksp(project(":ksp"))
    implementation(project(":ksp"))

    // Fabric-порт SimpleBedrockModel 2.5.1 (Sh1roCu), пакеты те же, что у neoforge-версии, включая v2.
    // Jar в git не лежит (*.gitignore на бинарники). Пересобрать:
    //   git clone -b 1.21.1 https://github.com/Sh1roCu/SimpleBedrockModel-Fabric
    //   cd SimpleBedrockModel-Fabric && ./gradlew build && cp build/libs/*[!s].jar ../superbwarfare-fabric/libs/
    modImplementation(files("libs/simplebedrockmodel-fabric-2.5.1+mc1.21.1.jar"))

    modImplementation("software.bernie.geckolib:geckolib-fabric-1.21.1:4.7.5")
    modImplementation("dev.engine-room.flywheel:flywheel-fabric-${project.property("minecraft_version")}:${project.property("flywheel_version")}")
    modImplementation("me.shedaniel.cloth:cloth-config-fabric:${project.property("cloth_config_version")}")

    // Отдаёт net.neoforged.neoforge.common.ModConfigSpec под Fabric с тем же именем пакета,
    // поэтому весь пакет config едет без правок: ModConfigBuilder там -- typealias на его Builder.
    modImplementation("fuzs.forgeconfigapiport:forgeconfigapiport-fabric:21.1.3")

    // Porting Lib даёт события в форме Forge/NeoForge поверх Fabric: LivingFallEvent,
    // LivingKnockBackEvent, LivingDropsEvent, MobEffectEvent и прочие, которых в Fabric API нет.
    // Иначе под каждое пришлось бы писать свой миксин -- три десятка штук.
    val portingLib = "3.1.0-beta.90+1.21.1"
    for (module in listOf("core", "entity", "level_events", "client_events", "transfer", "items")) {
        modImplementation("io.github.fabricators_of_create.Porting-Lib:$module:$portingLib")
    }

    compileOnly("com.maydaymemory:mae:1.1.2") {
        exclude("com.google.code.findbugs", "jsr305")
        exclude("it.unimi.dsi", "fastutil")
        exclude("org.joml", "joml")
    }
}

sourceSets.main.get().resources {
    srcDir("src/generated/resources")
}

// Датаген не портируем: его выход (src/generated/resources, 1694 файла) уже лежит в репозитории,
// а ссылок на пакет извне нет. Это 7581 строка и 41 % всех ошибок компиляции.
val portExcludes = listOf("**/datagen/**")

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    exclude(portExcludes)
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    exclude(portExcludes)
}

tasks.processResources {
    from("COPYING", "COPYING.LESSER")
}

// Быстрая проверка компиляции без упаковки jar.
tasks.register("devBuild") {
    description = "Fast dev build: compile + resources only, no JAR packaging"
    group = "build"
    dependsOn("classes", "processResources")
}

idea {
    module {
        isDownloadSources = true
        isDownloadJavadoc = true
    }
}
