#!/usr/bin/env bash
# Isolated tests of the production extension against narrow API doubles, NOT Minecraft integration.
set -euo pipefail
cd "$(dirname "$0")/.."
work=$(mktemp -d)
trap 'rm -rf "$work"' EXIT
cat > "$work/Player.kt" <<'KOTLIN'
package net.minecraft.world.entity.player
class Player
KOTLIN
cat > "$work/DroneEntity.kt" <<'KOTLIN'
package com.atsuishio.superbwarfare.entity.vehicle
class DroneEntity {
    var leftInputDown = true; var rightInputDown = true
    var forwardInputDown = true; var backInputDown = true
    var upInputDown = true; var downInputDown = true
    var fire = true
    var holdTickX = 8; var holdTickY = 9; var holdTickZ = 10
    var mouseX = 12.0; var mouseY = -4.0
    var velocity = 17.0; var ammunition = 3
    fun mouseInput(x: Double, y: Double) { mouseX = x; mouseY = y }
}
KOTLIN
cat > "$work/DroneControlAccess.kt" <<'KOTLIN'
package com.atsuishio.superbwarfare.tools
import com.atsuishio.superbwarfare.entity.vehicle.DroneEntity
import net.minecraft.world.entity.player.Player
object DroneControlAccess {
    var target: DroneEntity? = null
    var resolvedPlayer: Player? = null
    fun activeDrone(player: Player): DroneEntity? { resolvedPlayer = player; return target }
}
KOTLIN
cat > "$work/Checks.kt" <<'KOTLIN'
import com.atsuishio.superbwarfare.entity.vehicle.*
import com.atsuishio.superbwarfare.tools.DroneControlAccess
import net.minecraft.world.entity.player.Player
fun main() {
    var count = 0
    fun verify(ok: Boolean, name: String) { count++; check(ok) { name } }
    val drone = DroneEntity(); val player = Player()
    verify(!drone.canAcceptControl(null), "absent controller")
    verify(DroneControlAccess.resolvedPlayer == null, "null does not invoke resolver")
    verify(!drone.canAcceptControl(player), "resolver denial")
    DroneControlAccess.target = DroneEntity()
    verify(!drone.canAcceptControl(player), "another drone is not this drone")
    DroneControlAccess.target = drone
    verify(drone.canAcceptControl(player), "exact resolved identity")
    verify(DroneControlAccess.resolvedPlayer === player, "delegates actual player")
    verify(drone.fire && drone.leftInputDown, "authorization does not reset active input")
    drone.clearOperatorInput()
    verify(!drone.leftInputDown && !drone.rightInputDown, "horizontal inputs cleared")
    verify(!drone.forwardInputDown && !drone.backInputDown, "forward inputs cleared")
    verify(!drone.upInputDown && !drone.downInputDown, "vertical inputs cleared")
    verify(drone.mouseX == 0.0 && drone.mouseY == 0.0, "mouse command cleared")
    verify(!drone.fire, "fire request cleared")
    verify(drone.holdTickX == 0 && drone.holdTickY == 0 && drone.holdTickZ == 0, "hold counters cleared")
    verify(drone.velocity == 17.0 && drone.ammunition == 3, "does not zero motion or replenish ammunition")
    drone.clearOperatorInput()
    verify(!drone.fire && drone.mouseX == 0.0 && drone.holdTickX == 0, "idempotent reset")
    println("Drone input reset: $count isolated checks passed; API doubles, not Minecraft")
}
KOTLIN
if [[ "${DRONE_TEST_COMPILER:-kotlinc}" == gradle ]]; then
  cp src/main/kotlin/com/atsuishio/superbwarfare/entity/vehicle/DroneControl.kt "$work/DroneControl.kt"
  cat > "$work/settings.gradle.kts" <<'GRADLE'
pluginManagement { repositories { gradlePluginPortal(); mavenCentral() } }
rootProject.name = "drone-input-reset-checks"
GRADLE
  cat > "$work/build.gradle.kts" <<'GRADLE'
plugins { kotlin("jvm") version "2.1.20"; application }
repositories { mavenCentral() }
kotlin {
    jvmToolchain(21)
    sourceSets.main { kotlin.srcDir("."); kotlin.include("*.kt") }
    compilerOptions { allWarningsAsErrors.set(true) }
}
application { mainClass.set("ChecksKt") }
GRADLE
  ./gradlew -p "$work" --no-daemon run
else
  kotlinc -Werror -jvm-target "${KOTLIN_JVM_TARGET:-17}" \
    src/main/kotlin/com/atsuishio/superbwarfare/entity/vehicle/DroneControl.kt \
    "$work/Player.kt" "$work/DroneEntity.kt" "$work/DroneControlAccess.kt" "$work/Checks.kt" \
    -include-runtime -d "$work/checks.jar"
  java -jar "$work/checks.jar"
fi
