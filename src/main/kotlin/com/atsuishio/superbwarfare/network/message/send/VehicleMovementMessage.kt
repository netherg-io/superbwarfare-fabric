package com.atsuishio.superbwarfare.network.message.send

import com.atsuishio.superbwarfare.control.DroneControlAccess
import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity
import com.atsuishio.superbwarfare.network.PayloadContext
import com.atsuishio.superbwarfare.network.ServerPacketPayload
import kotlinx.serialization.Serializable

@Serializable
data class VehicleMovementMessage(
    val keys: Short,
    // Unused on the plain-vehicle path; only the drone/monitor path is replay-sensitive.
    val session: String = "none",
    val sequence: Long = 0,
) : ServerPacketPayload() {
    override fun PayloadContext.handler() {
        val player = sender()
        if (!player.isAlive || player.isRemoved || player.isSpectator) return
        val entity = player.vehicle
        if (entity is VehicleEntity && entity.getFirstPassenger() === player) {
            entity.processInput(keys)
            return
        }
        val drone = DroneControlAccess.resolve(player) ?: return
        if (!DroneControlAccess.acceptsSequence(drone, session, sequence)) return
        drone.processInput(keys)
    }
}
