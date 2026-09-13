package com.atsuishio.superbwarfare.tools

import com.atsuishio.superbwarfare.entity.vehicle.DroneEntity
import com.atsuishio.superbwarfare.init.ModItems
import com.atsuishio.superbwarfare.item.misc.MonitorItem
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.player.Player

/** Resolves only the sender's currently controlled, loaded drone; never loads chunks. */
object DroneControlAccess {
    fun activeDrone(player: Player): DroneEntity? {
        if (player !is ServerPlayer) return null
        val stack = player.mainHandItem
        if (!stack.`is`(ModItems.MONITOR.get())) return null
        val tag = NBTTool.getTag(stack)
        if (!tag.getBoolean(MonitorItem.USING) || !tag.getBoolean(MonitorItem.LINKED)) return null
        val drone = EntityFindUtil.findDrone(player.level(), tag.getString(MonitorItem.LINKED_DRONE))
            ?: return null

        if (!DroneControlRules.permits(
                player.getUUID(), drone.entityData.get(DroneEntity.CONTROLLER),
                player.isAlive, player.isSpectator, drone.isAlive,
                drone.level() === player.level(),
                tag.getBoolean(MonitorItem.USING), tag.getBoolean(MonitorItem.LINKED),
                drone.entityData.get(DroneEntity.LINKED),
                drone.position().distanceTo(player.position()), drone.maxControlDistance
            )) return null
        return drone
    }
}
