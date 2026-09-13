package com.atsuishio.superbwarfare.entity.vehicle

import com.atsuishio.superbwarfare.init.ModItems
import com.atsuishio.superbwarfare.item.misc.MonitorItem
import com.atsuishio.superbwarfare.network.security.DroneControlPolicy
import com.atsuishio.superbwarfare.tools.NBTTool
import net.minecraft.world.entity.player.Player

/** Called for every control packet and again before server-side drone ticking. */
fun DroneEntity.canAcceptControl(player: Player?): Boolean {
    if (player == null) return false
    val stack = player.mainHandItem
    if (!stack.`is`(ModItems.MONITOR.get())) return false
    val tag = NBTTool.getTag(stack)
    return DroneControlPolicy.allows(DroneControlPolicy.State(
        owner = entityData.get(DroneEntity.CONTROLLER),
        requester = player.getStringUUID(),
        drone = getStringUUID(),
        monitorTarget = tag.getString(MonitorItem.LINKED_DRONE),
        droneLinked = entityData.get(DroneEntity.LINKED),
        monitorActive = tag.getBoolean("Using") && tag.getBoolean("Linked"),
        playerAvailable = player.isAlive && !player.isRemoved && !player.isSpectator,
        sameWorld = player.level() === level(),
        droneAvailable = !isRemoved && health > 0,
        distanceSquared = position().distanceToSqr(player.position()),
        maximumDistance = maxControlDistance,
    ))
}

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
