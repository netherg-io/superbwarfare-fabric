package com.atsuishio.superbwarfare.tools;

import java.util.UUID;

/** Server snapshot checks shared by all remote-drone input handlers. */
public final class DroneControlRules {
    private DroneControlRules() {}

    /** Range is in blocks, not squared distance. Equality preserves the existing range limit. */
    public static boolean permits(
            UUID operatorId, String controllerId,
            boolean operatorAlive, boolean operatorSpectator, boolean droneAlive,
            boolean sameWorld, boolean monitorUsing, boolean monitorLinked, boolean droneLinked,
            double distance, double maxDistance) {
        return operatorId != null && operatorId.toString().equals(controllerId)
                && operatorAlive && !operatorSpectator && droneAlive && sameWorld
                && monitorUsing && monitorLinked && droneLinked
                && Double.isFinite(distance) && distance >= 0.0
                && Double.isFinite(maxDistance) && maxDistance > 0.0
                && distance <= maxDistance;
    }

    public static boolean finiteMouseInput(double x, double y) {
        return Double.isFinite(x) && Double.isFinite(y);
    }

    public static boolean finiteTarget(float x, float y, float z) {
        return Float.isFinite(x) && Float.isFinite(y) && Float.isFinite(z);
    }
}
