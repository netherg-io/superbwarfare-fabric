# Drone control hardening — local review

Refs netherg-io/blockfield-releases#4. Baseline: `8c6a00547a6812703ca632915b149e31e185a014`.

This changes the **existing Superb Warfare Fabric runtime**, not a second drone engine. It is a prerequisite slice of the Drone Warfare epic, **not the port of the separate addon**. Keep the epic open and this PR draft until local review.

## Changed runtime

`DroneControlAccess` supplies the same exact-owner/entity binding, linked/active monitor, life/spectator, world and existing per-drone range checks to movement, mouse, fire and interaction packets. Finite mouse input is checked after Float narrowing; block-target coordinates cannot silently saturate to Int limits. The artillery target still comes from the existing client protocol: these numeric checks do not prove a server raycast or LOS. Mounted-vehicle passenger/turret handling is retained; packets no longer update both a mounted vehicle and a remote drone in one mouse operation.

The monitor only activates an eligible main-hand binding. Leaving a missing drone is permitted, link/unlink clears `Using`, and dormant copies do not repeatedly reset another active drone. Client camera access is confined to client-annotated methods called on the client; the old unconditional client-field read in server inventory ticks is removed.

Input cleanup calls the existing `processInput(0)` and `mouseInput(0, 0)` plus clears `fire`, instead of writing unrelated persistent-data keys. A separate Fabric Kotlin entrypoint tracks only loaded drones through entity load/unload callbacks, checks control at the start of world ticks, invalidates the active monitor on disconnect, and drops references on unload/server stop. It does not scan the entire world each tick, load chunks, create a second backend, delete drones or change the existing signal-loss explosion/balance. Tick checks are a fallback, not proof that all same-tick death/damage ordering races are solved.

ScoutDroneEntity and its non-combat overrides, models/resources, class stocks, recipes and registry IDs are not changed.

## Tests actually run

```sh
bash dev/test-drone-control.sh
```

Kotlin 1.9.0 on OpenJDK 21, `-Werror`; the standalone harness targets JVM 17 for compiler compatibility (the production project remains Java 21).

- **198** policy checks: independent-gate matrix, activation/active use, owner and entity mismatch, world/life/range, NaN/infinity, Float overflow and Int target conversion.
- **35** adapter/lifecycle checks execute the actual `DroneControlAccess` and `DroneControlEvents` sources with explicitly isolated Minecraft/Fabric test doubles, including copy-on-read tags, disconnection, unload, duplicate cleanup and stopped-server references.
- Shell syntax and Fabric metadata JSON parse checked.

Fixtures under `dev/drone-control-tests/fixtures` are not production classes or proof of real API/linkage compatibility. The harness does **not** compile MonitorItem or the payload classes against Minecraft. Full Gradle/remap build, dedicated-server class loading, camera rendering, real callbacks and two-client tests have **not** been run in the authoring container. Existing GitHub build CI may provide additional results; do not treat a queued run as passing.

## Required local acceptance

Use the existing project toolchain/cache for the full build; `PORT-BRIEF.md` reserves heavy Gradle work for the coordinator. Review the existing CI result and then run `./gradlew build` on the local coordinator as appropriate. Do not publish a release merely to validate this branch.

1. Start dedicated Fabric 1.21.1 without loading client-only classes on the server. Check both normal and scout drone control.
2. With two clients test foreign/copied monitors, wrong linked IDs, dead/spectator operators, missing drones, another world and each drone's configured control distance.
3. Start movement/mouse/fire, then deselect, switch between two monitors, stop control, die, disconnect, unload a chunk and reconnect. Inspect real synced input, inventory tags, camera type, sound and entity tracking. Test a grounded drone as well as a flying one.
4. Ensure an unused inventory monitor cannot cancel active control. Test off-hand interactions without toggling the main-hand monitor.
5. Verify turret/passenger mouse input, ordinary vehicle driving, artillery marking, drone interaction and the scout's existing prohibition on armed attachments/fire.
6. Check actual range loss, world unload, server stop/restart and gameplay-room teardown. Confirm no retained entity/world references or forced chunks.

## Epic work not implemented by this PR

- Separate public upstream fork and Fabric Drone Warfare addon/build/release. Available connector actions did not provide repository/fork creation; no new public repository or upstream resource copy was made. The owner's approval of the public-fork approach remains valid. Existing private repository visibility and licenses are unchanged.
- Source/release and resource-license audit of `SmartStreamLabs/-SBW-Drone-Warfare`, public dependency supply, clean addon build and release provenance.
- Protocol session IDs/generations and sequence rejection. Current wire payloads cannot distinguish delayed input from a previous control generation after reactivation.
- Complete camera/input/audio session teardown, physical binding/unlink/dismantling authorization, same-tick ordering, game-room/context and consent-PvP integration. The four input checks are not a claim that every interaction/damage path is secured.
- Angle/Acro physics, inertia/payload/battery model, EW/fiber/signal rendering, collision zones, advanced sound, chunk-ticket budgets and addon stock integration.

No main-branch change, merge, tag, jar release, modpack pin, deployment or production restart belongs to this PR.
