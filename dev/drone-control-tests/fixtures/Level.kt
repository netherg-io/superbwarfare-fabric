package net.minecraft.world.level

import com.atsuishio.superbwarfare.entity.vehicle.DroneEntity

open class Level {
    val drones = mutableMapOf<String, DroneEntity>()
}
