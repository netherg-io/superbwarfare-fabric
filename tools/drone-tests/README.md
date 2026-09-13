# Drone control hardening — partial issue #4 prerequisite

This is an engine-side fix in the existing private Fabric SBW fork, **not the public Drone Warfare addon port**. No upstream addon code/assets are copied; visibility and COPYING files stay unchanged. Do not close netherg-io/blockfield-releases#4.

## What is connected
`DroneFireMessage`, the drone branch of `VehicleMovementMessage`, and the drone branch of `MouseMoveMessage` now require the current sender to match the server's drone controller, current linked/using monitor and exact target. The player must be alive, not removed/spectating, in the same world; the drone must not be removed and must have health. Distance and configured range must be finite and within the current drone-specific limit. Scout-specific range and its non-firing override are unchanged.

`DroneEntity.baseTick` rechecks the same policy before its parent tick and clears six direction keys, mouse command, fire request and input-hold counters when no valid operator remains. This includes the former controller-null and on-ground gaps. It does not zero velocity, delete the entity, replenish inventory or introduce a different flight model. Existing signal-loss/explosion behavior is not redesigned here.

Mouse packets reject NaN/infinity/extreme values before shared vehicle float arithmetic. The generous 1,000,000 transport bound is not a flight/sensitivity balance value. Normal vehicle-seat routing is otherwise untouched.

## Checks
Independent contracts use the repository's Kotlin 2.1.20 / Java 21 versions, without Minecraft dependencies:
```
./gradlew -p tools/drone-tests --no-daemon run
python3 tools/drone-tests/check_wiring.py
```
With a standalone compiler: `bash tools/drone-tests/run.sh`. Local authoring used Kotlin 1.9.0, `KOTLIN_JVM_TARGET=17` and Java 21 because that compiler predates the 21 bytecode target: **36 checks passed**. This does not prove target-21 adapter compilation. Dedicated CI uses 2.1.20/21; the existing full Build workflow separately compiles the actual mod.

The wiring guard is only a source-level coupling check, not a simulated multiplayer test. Review current-head Actions separately. If full Build cannot resolve existing private/local dependencies, follow the project's normal dependency setup; do not claim the Kotlin adapter compiled from the policy test alone.

## Remaining work (not just unrun checks)
The separate public addon fork and rights/assets provenance review; versioned session IDs in both client and server messages; common idempotent camera/sound/input termination; disconnect/death/world-change recovery; server-owned physics/Angle/Acro, battery/link/fiber/occlusion; bounded chunk tickets; addon registry integration and releases. A delayed packet arriving after a new valid session can still pass these current-state checks until the nonce protocol is implemented. Blockfield's room, consent and reward policy requires a dedicated integration layer, not a dependency on private game code inside a public addon.

## Required local two-client scenarios
Exercise rightful and wrong-owner monitors, monitor targeting another drone, offhand/hand switches, ground and airborne control release, death/logout/dimension changes, range boundary and Scout behavior. Test NaN/oversized mouse input in a local test harness; ordinary vehicle controls must still work. Confirm camera/audio restoration separately — this PR does not claim to fix them. No production server or user world is used for validation.
