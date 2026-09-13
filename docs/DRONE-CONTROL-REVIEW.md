# Drone input authority: local review

Refs netherg-io/blockfield-releases#4, stage 1 (SBW compatibility prerequisite).
Baseline: superbwarfare-fabric 8c6a00547a6812703ca632915b149e31e185a014.

## Implemented in the real packet handlers

Movement, mouse and drone-action messages resolve the same server-owned active
monitor/drone pair. Control requires a live non-spectator ServerPlayer, the
entity's current controller UUID, live/loaded entity in the same world, both
link flags and the held monitor's USING state, and finite distance within the
entity's own finite positive range. This does not force-load entities or chunks.
FPV and Scout keep their existing 150/200 block limits. The Scout virtual fire
setter remains in use, so this change does not arm reconnaissance drones.
NaN/infinite mouse values and artillery-marker coordinates are rejected before
mutation; finite marker coordinates still follow the existing marker feature.
Invalid senders do not unlink or mutate another operator's drone.

No wire format, camera, drone physics, inventory stock, class restriction,
world data, dependency pin, release or repository visibility is changed.
Existing occupied-vehicle controls remain delegated to VehicleEntity, including
gunner mouse input; the remote-drone path no longer trusts monitor NBT alone.

## Verification

Run `bash dev/test-drone-control.sh` with JDK 21. This compiles the production
Java rules and their dependency-free checks with warnings as errors. These are
policy unit tests, not tests of the Minecraft adapter, packet serialization,
drone entities, or the full mod. The existing Build workflow is the separate
full Kotlin/Java compilation path; a successful rules job is not a full build.
Local full builds need the dependency jars already documented in build.gradle.kts
(SimpleBedrockModel bf3 and the shaded Rhino jar). Never silently replace those
jars to make CI green. No live Minecraft or two-client test is claimed here.

## Two-client checks before merge

- Owner can move/look/act on FPV, at and just outside its range boundary; repeat
  with Scout and confirm its longer range and no payload/fire behavior.
- Another player with a copied linked monitor cannot move/look/act on the
  owner's drone. Re-link ownership and repeat with the previous operator.
- Reject input while USING or either Linked flag is false, after operator death,
  in spectator mode, after world change and when the drone is removed/unloaded.
- Inspect normal vehicle driver/gunner controls for regressions.
- Inject NaN/infinite mouse/marker inputs on a local test server and confirm no
  transform or marker mutation. Normal finite marker placement still works.

## Still unimplemented in issue #4

This is NOT the Drone Warfare addon port or a new public fork. No upstream addon
code, textures, models or sounds were copied. Public fork creation and the
upstream provenance/resource-license audit remain outstanding.

It also does not implement session generations in the packet format. In
particular, an old in-flight input arriving after the SAME owner resumes the
SAME drone cannot yet be distinguished from new input. Implement a versioned
session protocol together with the client; do not claim replay protection from
UUID/range checks. Room/round admission and class/stock rules still require the
private Blockfield adapter. Full idempotent camera/input/audio cleanup on all
exit paths, bounded chunk tickets, Angle/Acro flight, battery, payload mass,
signal/EW/fiber behavior and multiplayer validation remain separate work.

Keep the tracking issue open. This PR is safe to review independently; merging
it does not publish the private SBW repository or deliver the standalone addon.
