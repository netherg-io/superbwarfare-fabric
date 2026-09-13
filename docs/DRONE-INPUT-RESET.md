# Pre-tick drone input reset

Stacked on superbwarfare-fabric PR #1 (`fix/drone-control-authority`, baseline f02392b47f7bc4bcc209460f7cf52851067bf786). This PR reuses its `DroneControlAccess` resolver. The earlier competing Kotlin policy and duplicate packet rewrites were removed from this branch's final tree, along with temporary review workflows. Do not merge two authority implementations.

## Runtime change
Before the server-side DroneEntity parent tick, resolve the current operator through the same checked monitor/controller/world/range path used by packets. Only the exact resolved drone retains its input. Null controller, inactive/wrong monitor and all other resolver denials clear six direction keys, mouse command, fire request and hold counters. The reset is not restricted to airborne drones and occurs before the parent can consume stale input.

This does not zero motion, change ownership, replenish ammunition, change Scout overrides or implement a new physics model. Signal-loss/explosion behavior is unchanged. It is not a replacement for versioned session nonces, camera/audio teardown, actual room lifecycle hooks or the public Drone Warfare addon port. Release issue #4 stays open.

## Reproduce checks
`DRONE_TEST_COMPILER=gradle bash dev/test-drone-input-reset.sh` uses the repository wrapper to compile the production extension in a temporary Kotlin 2.1.20/Java 21 project with small API doubles. No Minecraft classes are bootstrapped. `bash dev/test-drone-input-reset.sh` uses an installed standalone compiler, default target 17; local authoring on Kotlin 1.9.0/Java 21 passed **15 isolated checks** with warnings as errors.

`bash dev/test-drone-control.sh` runs the 174 production authorization-rule checks from prerequisite PR #1. `python3 dev/check-drone-input-reset.py` is a source-level guard for pre-tick placement and reuse of that resolver. These checks do not prove actual Fabric adapter loading, Minecraft physics or multiplayer behavior. Check current-head full Build separately; no passing full build is inferred from isolated tests.

## Local gameplay review
Checkout this stacked branch to test both #1 and #2 together. After approval, merge #1 first and retarget #2 to main; do not merge automatically. Start the isolated rig with the resulting jar, verify the running version, hold each movement input and release control on ground/in air. Repeat with monitor replacement, wrong-target monitor, operator death/logout/world change and range loss. Ensure commands clear, ordinary inertia remains, valid control is not cleared, Scout is still unarmed and normal vehicle controls work. Verify camera/audio independently: those features are not implemented here. Do not use production worlds or create a release to run these checks.
