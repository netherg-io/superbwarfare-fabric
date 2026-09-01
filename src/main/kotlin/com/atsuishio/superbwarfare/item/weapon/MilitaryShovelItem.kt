package com.atsuishio.superbwarfare.item.weapon

import com.atsuishio.superbwarfare.client.renderer.item.MilitaryShovelRenderer
import com.atsuishio.superbwarfare.init.ModItems
import com.atsuishio.superbwarfare.init.ModTags
import com.atsuishio.superbwarfare.item.CustomDamageProperty
import com.atsuishio.superbwarfare.tiers.ModItemTier
import com.atsuishio.superbwarfare.tools.mc
import net.minecraft.ChatFormatting
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer
import net.minecraft.core.component.DataComponents
import net.minecraft.network.chat.Component
import net.minecraft.world.InteractionResult
import net.minecraft.world.item.*
import net.minecraft.world.item.component.Tool
import net.minecraft.world.item.context.UseOnContext
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry

open class MilitaryShovelItem :
    AxeItem(
        ModItemTier.CEMENTED_CARBIDE,
        CustomDamageProperty(810).rarity(Rarity.RARE)
            .component(
                DataComponents.TOOL, Tool(
                    listOf(
                        Tool.Rule.deniesDrops(ModItemTier.CEMENTED_CARBIDE.incorrectBlocksForDrops),
                        Tool.Rule.minesAndDrops(
                            ModTags.Blocks.MINEABLE_WITH_MILITARY_SHOVEL,
                            ModItemTier.CEMENTED_CARBIDE.speed
                        )
                    ),
                    1f, 1
                )
            )
            .attributes(createAttributes(ModItemTier.CEMENTED_CARBIDE, 2f, -2.6f))
    ) {

    override fun appendHoverText(
        stack: ItemStack,
        context: TooltipContext,
        tooltipComponents: MutableList<Component>,
        tooltipFlag: TooltipFlag
    ) {
        tooltipComponents.add(
            Component.translatable("des.superbwarfare.military_shovel").withStyle(ChatFormatting.GRAY)
        )
    }

    /**
     * Апстрим повторял здесь логику Mekanism-Tools поверх NeoForge-овских ItemAbilities: лопата
     * умеет всё, что умеют топор, лопата и мотыга. На Fabric ItemAbility нет, зато ванильные
     * useOn у AxeItem/ShovelItem/HoeItem делают ровно эти три набора действий и сами бьют по
     * прочности того стака, что лежит в UseOnContext, -- то есть по нашей лопате.
     *
     * ponytail: набор действий теперь ванильный. Если понадобится расширить его тегами блоков
     * (снять кору с модового бревна и т.п.), придётся вернуться к своим таблицам превращений.
     */
    override fun useOn(context: UseOnContext): InteractionResult {
        val axe = super.useOn(context)
        if (axe != InteractionResult.PASS) return axe

        val vanilla = if (context.player?.isShiftKeyDown == true) Items.IRON_HOE else Items.IRON_SHOVEL
        return vanilla.useOn(context)
    }

    override fun getEnchantmentValue(): Int {
        return ModItemTier.CEMENTED_CARBIDE.enchantmentValue
    }

    companion object {
        /** Клиент: BEWLR из IClientItemExtensions#getCustomRenderer заменён на DynamicItemRenderer из Fabric API. */
        @Environment(EnvType.CLIENT)
        fun init() {
            var renderer: BlockEntityWithoutLevelRenderer? = null

            BuiltinItemRendererRegistry.INSTANCE.register(
                ModItems.MILITARY_SHOVEL.get(),
                BuiltinItemRendererRegistry.DynamicItemRenderer { stack, mode, poseStack, buffer, light, overlay ->
                    if (renderer == null) {
                        renderer = MilitaryShovelRenderer(mc.blockEntityRenderDispatcher, mc.entityModels)
                    }
                    renderer!!.renderByItem(stack, mode, poseStack, buffer, light, overlay)
                }
            )
        }

    }
}
