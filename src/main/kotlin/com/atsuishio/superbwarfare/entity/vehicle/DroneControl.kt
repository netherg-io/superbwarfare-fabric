package com.atsuishio.superbwarfare.entity.vehicle

import com.atsuishio.superbwarfare.tools.DroneControlAccess
import net.minecraft.world.entity.player.Player

/** Reuse the packet resolver; never maintain a second authorization policy here. */
fun DroneEntity.canAcceptControl(player: Player?): Boolean =
    player != null && DroneControlAccess.activeDrone(player) === this

/** Clear commands, not velocity, ownership or ammunition; normal physical coasting remains. */
fun DroneEntity.clearOperatorInput() {
    leftInputDown = false
    rightInputDown = false
    forwardInputDown = false
    backInputDown = false
    upInputDown = false
    downInputDown = false
    mouseInput(0.0, 0.0)
    fire = false
    holdTickX = 0
    holdTickY = 0
    holdTickZ = 0
}
