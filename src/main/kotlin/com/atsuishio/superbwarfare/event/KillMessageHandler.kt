package com.atsuishio.superbwarfare.event

import com.atsuishio.superbwarfare.tools.LivingKillRecord
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import java.util.*

object KillMessageHandler {
    val QUEUE: Queue<LivingKillRecord> = ArrayDeque()

    fun init() {
        ClientTickEvents.END_CLIENT_TICK.register { onClientTick() }
    }

    private fun onClientTick() {
        for (record in QUEUE) {
            if (record.freeze && record.tick >= 3) {
                continue
            }
            record.tick++
            if (record.fastRemove && record.tick >= 82 || record.tick >= 100) {
                QUEUE.poll()
            }
        }
    }
}
