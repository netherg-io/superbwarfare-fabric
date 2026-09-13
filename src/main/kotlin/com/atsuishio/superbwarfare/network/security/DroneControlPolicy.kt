package com.atsuishio.superbwarfare.network.security

/** Checks server-observed facts, not client declarations. Does not replace a session nonce protocol. */
object DroneControlPolicy {
    data class State(
        val owner: String?,
        val requester: String,
        val drone: String,
        val monitorTarget: String?,
        val droneLinked: Boolean,
        val monitorActive: Boolean,
        val playerAvailable: Boolean,
        val sameWorld: Boolean,
        val droneAvailable: Boolean,
        val distanceSquared: Double,
        val maximumDistance: Double,
    )

    fun allows(state: State): Boolean = with(state) {
        owner != null && owner.isNotBlank() && owner == requester && drone.isNotBlank() &&
            monitorTarget == drone && droneLinked && monitorActive && playerAvailable &&
            sameWorld && droneAvailable && distanceSquared.isFinite() && distanceSquared >= 0.0 &&
            maximumDistance.isFinite() && maximumDistance > 0.0 &&
            maximumDistance <= 30_000_000.0 && distanceSquared <= maximumDistance * maximumDistance
    }

    /** Generous transport bound, not a flight/sensitivity tuning parameter. Reject before float math. */
    fun validMouse(x: Double, y: Double): Boolean =
        x.isFinite() && y.isFinite() && kotlin.math.abs(x) <= 1_000_000.0 && kotlin.math.abs(y) <= 1_000_000.0
}
