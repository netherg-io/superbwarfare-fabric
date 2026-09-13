package net.minecraft.world.level

import com.atsuishio.superbwarfare.entity.vehicle.DroneEntity
import net.minecraft.world.entity.player.Player

open class Level {
    val drones = mutableMapOf<String, DroneEntity>()
    val players = mutableMapOf<String, Player>()
}
