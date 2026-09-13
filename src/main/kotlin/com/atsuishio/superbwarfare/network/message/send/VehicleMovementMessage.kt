package com.atsuishio.superbwarfare.network.message.send

import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity
import com.atsuishio.superbwarfare.network.PayloadContext
import com.atsuishio.superbwarfare.network.ServerPacketPayload
import com.atsuishio.superbwarfare.tools.DroneControlAccess
import kotlinx.serialization.Serializable

@Serializable
data class VehicleMovementMessage(val keys: Short) : ServerPacketPayload() {
    override fun PayloadContext.handler() {
        val player = sender()
        if (!player.isAlive || player.isSpectator) return
        val entity = player.vehicle
        val vehicle = if (entity is VehicleEntity && entity.getFirstPassenger() === player) {
            entity
        } else {
            DroneControlAccess.activeDrone(player) ?: return
        }
        vehicle.processInput(keys)
    }
}
