import com.atsuishio.superbwarfare.network.security.DroneControlPolicy

fun main() {
    var checks = 0
    fun verify(value: Boolean, label: String) { checks++; check(value) { label } }
    val base = DroneControlPolicy.State("operator", "operator", "drone-a", "drone-a",
        true, true, true, true, true, 100.0, 150.0)
    fun denied(state: DroneControlPolicy.State, label: String) = verify(!DroneControlPolicy.allows(state), label)
    verify(DroneControlPolicy.allows(base), "valid owner")
    denied(base.copy(owner = null), "absent owner")
    denied(base.copy(owner = ""), "blank owner")
    denied(base.copy(requester = "other"), "another operator")
    denied(base.copy(monitorTarget = "drone-b"), "wrong monitor target")
    denied(base.copy(monitorTarget = null), "missing monitor target")
    denied(base.copy(drone = "", monitorTarget = ""), "blank drone id")
    denied(base.copy(droneLinked = false), "unlinked drone")
    denied(base.copy(monitorActive = false), "inactive/replaced monitor")
    denied(base.copy(playerAvailable = false), "dead/removed/spectator operator")
    denied(base.copy(sameWorld = false), "dimension mismatch")
    denied(base.copy(droneAvailable = false), "destroyed drone")
    denied(base.copy(distanceSquared = -1.0), "invalid negative distance")
    for (n in listOf(Double.NaN, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY)) {
        denied(base.copy(distanceSquared = n), "nonfinite distance $n")
        denied(base.copy(maximumDistance = n), "nonfinite limit $n")
        verify(!DroneControlPolicy.validMouse(n, 0.0), "nonfinite mouse X $n")
        verify(!DroneControlPolicy.validMouse(0.0, n), "nonfinite mouse Y $n")
    }
    denied(base.copy(maximumDistance = 0.0), "zero range")
    denied(base.copy(maximumDistance = -1.0), "negative range")
    denied(base.copy(maximumDistance = Double.MAX_VALUE), "limit overflow")
    verify(DroneControlPolicy.allows(base.copy(distanceSquared = 150.0 * 150.0)), "range boundary")
    denied(base.copy(distanceSquared = 150.0 * 150.0 + 1.0), "beyond range")
    verify(DroneControlPolicy.allows(base.copy(maximumDistance = 200.0, distanceSquared = 40_000.0)), "scout range preserved")
    verify(DroneControlPolicy.validMouse(0.0, 0.0), "neutral mouse")
    verify(DroneControlPolicy.validMouse(-50.25, 75.0), "ordinary mouse")
    verify(DroneControlPolicy.validMouse(-1_000_000.0, 1_000_000.0), "transport boundary")
    verify(!DroneControlPolicy.validMouse(Double.MAX_VALUE, 0.0), "oversized mouse X")
    verify(!DroneControlPolicy.validMouse(0.0, -Double.MAX_VALUE), "oversized mouse Y")
    println("Drone control policy: $checks checks passed")
}
