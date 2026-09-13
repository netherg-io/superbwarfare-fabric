"""Structural wiring regression only; API doubles and source checks are not Minecraft E2E."""
from pathlib import Path
root = Path(__file__).resolve().parents[1]
entity = (root / 'src/main/java/com/atsuishio/superbwarfare/entity/vehicle/DroneEntity.kt').read_text()
body = entity[entity.index('override fun baseTick()'):]
assert body.index('!this.level().isClientSide() && !canAcceptControl(getController())') < body.index('super.baseTick()')
assert body.index('clearOperatorInput()') < body.index('super.baseTick()')
helper = (root / 'src/main/kotlin/com/atsuishio/superbwarfare/entity/vehicle/DroneControl.kt').read_text()
assert 'player != null && DroneControlAccess.activeDrone(player) === this' in helper
for axis in ('left', 'right', 'forward', 'back', 'up', 'down'):
    assert f'{axis}InputDown = false' in helper
assert 'mouseInput(0.0, 0.0)' in helper and 'fire = false' in helper
packets = root / 'src/main/kotlin/com/atsuishio/superbwarfare/network/message/send'
for name in ('DroneFireMessage', 'MouseMoveMessage', 'VehicleMovementMessage'):
    assert 'DroneControlAccess.activeDrone(player)' in (packets / f'{name}.kt').read_text()
print('Pre-tick reset wiring guard passed; not a live gameplay test')
