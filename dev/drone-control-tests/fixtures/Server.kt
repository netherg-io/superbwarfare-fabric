package net.minecraft.server.level

import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level

class TestServer
class ServerLevel(val server: TestServer) : Level()
class ServerPlayer(world: Level, id: String) : Player(world, id) {
    var resetPackets = 0
}
