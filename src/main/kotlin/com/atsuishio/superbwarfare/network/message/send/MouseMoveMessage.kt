package com.atsuishio.superbwarfare.network.message.send

import com.atsuishio.superbwarfare.control.DroneControlAccess
import com.atsuishio.superbwarfare.control.DroneControlPolicy
import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity
import com.atsuishio.superbwarfare.network.PayloadContext
import com.atsuishio.superbwarfare.network.ServerPacketPayload
import kotlinx.serialization.Serializable

@Serializable
data class MouseMoveMessage(val speedX: Double, val speedY: Double) : ServerPacketPayload() {
    override fun PayloadContext.handler() {
        if (!DroneControlPolicy.validMouseInput(speedX, speedY)) return
        val player = sender()
        if (!player.isAlive || player.isRemoved || player.isSpectator) return
        val entity = player.vehicle
        if (entity is VehicleEntity) {
            // Preserve the existing passenger/turret path; do not restrict gunners to the driver's seat.
            entity.mouseInput(speedX, speedY)
            return
        }
        DroneControlAccess.resolve(player)?.mouseInput(speedX, speedY)
    }
}
