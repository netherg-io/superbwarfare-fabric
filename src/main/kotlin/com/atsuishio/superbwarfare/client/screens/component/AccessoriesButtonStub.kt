package com.atsuishio.superbwarfare.client.screens.component

import io.wispforest.accessories.client.gui.AbstractButtonExtension
import io.wispforest.accessories.client.gui.ButtonEvents
import net.fabricmc.fabric.api.event.Event
import net.fabricmc.fabric.api.event.EventFactory

/**
 * Accessories вкручивает AbstractButtonExtension в ванильный AbstractButton через loom interface
 * injection, но реализацию getRenderingEvent даёт только миксин, в рантайме. На этапе компиляции
 * метод остаётся абстрактным, и каждый Kotlin-наследник кнопки обязан его закрыть сам.
 *
 * ponytail: одно событие на все кнопки мода вместо поля на экземпляр -- рендер кнопок мода
 * переопределён целиком, миксин Accessories до него не доходит и события никто не шлёт.
 * Разводить по экземплярам, если кнопки мода начнут участвовать в ButtonEvents.
 */
interface AccessoriesButtonStub : AbstractButtonExtension {
    override fun getRenderingEvent(): Event<ButtonEvents.AdjustRendering> = RENDERING_EVENT

    companion object {
        private val RENDERING_EVENT: Event<ButtonEvents.AdjustRendering> =
            EventFactory.createArrayBacked(ButtonEvents.AdjustRendering::class.java) { callbacks ->
                ButtonEvents.AdjustRendering { button, graphics, sprite, x, y, width, height ->
                    callbacks.any { it.render(button, graphics, sprite, x, y, width, height) }
                }
            }
    }
}
