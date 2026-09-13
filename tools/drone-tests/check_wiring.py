"""Source-level guard only. Not a Kotlin adapter compilation or a Minecraft integration test."""
from pathlib import Path
root = Path(__file__).resolve().parents[2]
packets = root / 'src/main/kotlin/com/atsuishio/superbwarfare/network/message/send'
for name in ('DroneFireMessage', 'MouseMoveMessage', 'VehicleMovementMessage'):
    source = (packets / f'{name}.kt').read_text()
    assert 'import com.atsuishio.superbwarfare.entity.vehicle.canAcceptControl' in source, name
    assert '.canAcceptControl(player)' in source, name
mouse = (packets / 'MouseMoveMessage.kt').read_text()
assert mouse.index('validMouse(speedX, speedY)') < mouse.index('entity.mouseInput(')
entity = (root / 'src/main/java/com/atsuishio/superbwarfare/entity/vehicle/DroneEntity.kt').read_text()
body = entity[entity.index('override fun baseTick()'):]
assert body.index('clearOperatorInput()') < body.index('super.baseTick()')
control = (root / 'src/main/kotlin/com/atsuishio/superbwarfare/entity/vehicle/DroneControl.kt').read_text()
for key in ('left', 'right', 'forward', 'back', 'up', 'down'):
    assert f'{key}InputDown = false' in control
assert 'fire = false' in control and 'mouseInput(0.0, 0.0)' in control
print('Source wiring guard passed (not a game/runtime test)')
