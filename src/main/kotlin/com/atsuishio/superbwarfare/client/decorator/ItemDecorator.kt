package com.atsuishio.superbwarfare.client.decorator

import net.minecraft.client.gui.Font
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.world.item.ItemStack

/**
 * Замена NeoForge-овскому IItemDecorator: сигнатура та же, но интерфейс свой, потому что в
 * Fabric API нет ни аналога RegisterItemDecorationsEvent, ни точки, куда декоратор отдать.
 *
 * ponytail: сейчас никто не вызывает -- ClientRenderHandler.registerItemDecorations() держит пары
 * «предмет -> декоратор» до появления миксина на GuiGraphics.renderItemDecorations.
 */
interface ItemDecorator {
    fun render(guiGraphics: GuiGraphics, font: Font, stack: ItemStack, xOffset: Int, yOffset: Int): Boolean
}
