import com.atsuishio.superbwarfare.tools.DroneControlRules;
import java.util.UUID;

/** Runs the actual production rules without a Minecraft classpath. */
public final class DroneControlChecks {
    private static final UUID OWNER = UUID.fromString("11111111-1111-4111-8111-111111111111");
    private static final String OTHER = "22222222-2222-4222-8222-222222222222";
    private static int checks;

    private static void check(boolean value, String name) {
        checks++;
        if (!value) throw new AssertionError(name);
    }
    private static boolean allowed(String owner, boolean[] state, double distance, double limit) {
        return DroneControlRules.permits(OWNER, owner,
                state[0], state[1], state[2], state[3], state[4], state[5], state[6], distance, limit);
    }
    public static void main(String[] args) {
        boolean[] active = {true, false, true, true, true, true, true};
        String owner = OWNER.toString();
        check(allowed(owner, active, 0, 150), "owner can control at origin");
        check(allowed(owner, active, 150, 150), "FPV inclusive range boundary");
        check(!allowed(owner, active, Math.nextUp(150.0), 150), "FPV outside range");
        check(allowed(owner, active, 200, 200), "scout retains its longer range");
        check(!allowed(owner, active, Math.nextUp(200.0), 200), "scout outside range");
        for (String id : new String[]{OTHER, null, "", "undefined", "none"}) {
            check(!allowed(id, active, 1, 150), "reject foreign or absent controller");
        }
        check(!DroneControlRules.permits(null, owner, true, false, true, true, true, true, true, 1, 150),
                "absent operator denied");
        for (int i = 0; i < active.length; i++) {
            boolean[] invalid = active.clone();
            invalid[i] = !invalid[i];
            check(!allowed(owner, invalid, 1, 150), "independent state prerequisite " + i);
        }
        for (double invalid : new double[]{Double.NaN, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY, -1}) {
            check(!allowed(owner, active, invalid, 150), "invalid distance");
            check(!allowed(owner, active, 1, invalid), "invalid configured range");
        }
        check(!allowed(owner, active, 0, 0), "zero configured range denied");
        check(!allowed(owner, active, Double.MAX_VALUE, 200), "huge finite distance denied");
        for (double invalid : new double[]{Double.NaN, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY}) {
            check(!DroneControlRules.finiteMouseInput(invalid, 0), "invalid mouse x");
            check(!DroneControlRules.finiteMouseInput(0, invalid), "invalid mouse y");
        }
        check(DroneControlRules.finiteMouseInput(-1.25, 3.5), "normal mouse input");
        check(DroneControlRules.finiteMouseInput(0, 0), "neutral mouse input");
        for (float invalid : new float[]{Float.NaN, Float.POSITIVE_INFINITY, Float.NEGATIVE_INFINITY}) {
            check(!DroneControlRules.finiteTarget(invalid, 0, 0), "invalid target x");
            check(!DroneControlRules.finiteTarget(0, invalid, 0), "invalid target y");
            check(!DroneControlRules.finiteTarget(0, 0, invalid), "invalid target z");
        }
        check(DroneControlRules.finiteTarget(-50, 64, 100), "normal target");
        // Exhaust every boolean combination; only the fully live/owned/linked context can pass.
        for (int mask = 0; mask < 128; mask++) {
            boolean[] state = new boolean[7];
            for (int i = 0; i < state.length; i++) state[i] = (mask & (1 << i)) != 0;
            check(allowed(owner, state, 10, 150) == (mask == 125), "state truth table " + mask);
        }
        System.out.println("DroneControlChecks: " + checks + " checks passed");
    }
}
