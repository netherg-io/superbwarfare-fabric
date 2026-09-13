package com.atsuishio.superbwarfare.network.message.send

import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity
import com.atsuishio.superbwarfare.network.PayloadContext
import com.atsuishio.superbwarfare.network.ServerPacketPayload
import com.atsuishio.superbwarfare.tools.DroneControlAccess
import com.atsuishio.superbwarfare.tools.DroneControlRules
import kotlinx.serialization.Serializable

@Serializable
data class MouseMoveMessage(val speedX: Double, val speedY: Double) : ServerPacketPayload() {
    override fun PayloadContext.handler() {
        val player = sender()
        if (!player.isAlive || player.isSpectator || !DroneControlRules.finiteMouseInput(speedX, speedY)) return
        val entity = player.vehicle

        // Preserve seat-specific mouse handling (including gunners) in VehicleEntity.
        if (entity is VehicleEntity) {
            entity.mouseInput(speedX, speedY)
        }
        DroneControlAccess.activeDrone(player)?.mouseInput(speedX, speedY)
    }
}
