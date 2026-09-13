package com.atsuishio.superbwarfare.control

/** Server-derived facts only; never deserialize this object from a client payload. */
data class DroneControlFacts(
    val operatorId: String,
    val controllerId: String,
    val droneId: String,
    val monitorDroneId: String,
    val monitorPresent: Boolean,
    val monitorLinked: Boolean,
    val monitorUsing: Boolean,
    val droneLinked: Boolean,
    val operatorEligible: Boolean,
    val droneAlive: Boolean,
    val sameWorld: Boolean,
    val distance: Double,
    val maxDistance: Double
)

object DroneControlPolicy {
    fun allows(facts: DroneControlFacts, requireUsing: Boolean = true): Boolean = with(facts) {
        operatorId.isNotBlank() && droneId.isNotBlank() &&
            operatorId == controllerId && droneId == monitorDroneId &&
            monitorPresent && monitorLinked && (!requireUsing || monitorUsing) &&
            droneLinked && operatorEligible && droneAlive && sameWorld &&
            distance.isFinite() && distance >= 0.0 &&
            maxDistance.isFinite() && maxDistance > 0.0 && distance <= maxDistance
    }

    // VehicleEntity.mouseInput narrows doubles to floats; finite doubles alone are insufficient.
    fun validMouseInput(x: Double, y: Double): Boolean =
        x.isFinite() && y.isFinite() && x.toFloat().isFinite() && y.toFloat().isFinite()

    fun validBlockTarget(x: Float, y: Float, z: Float): Boolean =
        validBlockCoordinate(x) && validBlockCoordinate(y) && validBlockCoordinate(z)

    private fun validBlockCoordinate(value: Float): Boolean =
        value.isFinite() && value.toDouble() >= Int.MIN_VALUE.toDouble() &&
            value.toDouble() <= Int.MAX_VALUE.toDouble()
}
