package com.atsuishio.superbwarfare.item.armor

import com.atsuishio.superbwarfare.Mod
import com.atsuishio.superbwarfare.Mod.loc
import com.atsuishio.superbwarfare.init.ModAttributes
import com.atsuishio.superbwarfare.init.ModItems
import com.atsuishio.superbwarfare.resource.model.ArmorModelReloadListener
import com.atsuishio.superbwarfare.tiers.ModArmorMaterial
import com.github.mcmodderanchor.simplebedrockmodel.v1.client.handler.FirstPersonArmorHandler
import com.github.mcmodderanchor.simplebedrockmodel.v2.client.renderer.GeoArmorRendererV2
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.client.rendering.v1.ArmorRenderer
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.EquipmentSlotGroup
import net.minecraft.world.entity.ai.attributes.AttributeModifier
import net.minecraft.world.item.ArmorItem
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.component.ItemAttributeModifiers
import kotlin.math.max
import com.atsuishio.superbwarfare.item.StackAttributeItem

class UsChestIotvItem : ArmorItem(
    ModArmorMaterial.CEMENTED_CARBIDE,
    Type.CHESTPLATE,
    Properties().durability(Type.CHESTPLATE.getDurability(50))
), StackAttributeItem {
    companion object {
        val TEXTURE = loc("textures/bedrock/armor/us_chest_iotv.png")
        val MODEL = loc("models/bedrock/armor/us_chest_iotv.geo.json")

        /** Клиент: аналога IClientItemExtensions#getHumanoidArmorModel на Fabric нет, модель брони отдаётся через ArmorRenderer. */
        @Environment(EnvType.CLIENT)
        fun init() {
            var renderer: GeoArmorRendererV2? = null
            fun getRenderer(slot: EquipmentSlot): GeoArmorRendererV2 {
                if (renderer == null) {
                    renderer = GeoArmorRendererV2(
                        ArmorModelReloadListener.getModel(MODEL),
                        slot,
                        TEXTURE
                    )
                }
                return renderer!!
            }

            ArmorRenderer.register({ poseStack, buffer, stack, entity, slot, light, contextModel ->
                val armorRenderer = getRenderer(slot)
                armorRenderer.preparePose(entity, stack, slot, contextModel)
                armorRenderer.renderArmorToBuffer(poseStack, buffer, light, OverlayTexture.NO_OVERLAY, 1f, 1f, 1f, 1f)
            }, ModItems.US_CHEST_IOTV.get())

            FirstPersonArmorHandler.register(ModItems.US_CHEST_IOTV.get()) { getRenderer(EquipmentSlot.CHEST) }
        }
    }

    override fun getDefaultAttributeModifiers(stack: ItemStack): ItemAttributeModifiers {
        val modifiers = baseAttributeModifiers(stack)
        val list = ArrayList<ItemAttributeModifiers.Entry>(modifiers.modifiers())
        list.add(
            ItemAttributeModifiers.Entry(
                ModAttributes.BULLET_RESISTANCE, AttributeModifier(
                    Mod.ATTRIBUTE_MODIFIER,
                    0.5 * max(0.0, 1 - stack.damageValue.toDouble() / stack.maxDamage),
                    AttributeModifier.Operation.ADD_VALUE
                ),
                EquipmentSlotGroup.bySlot(this.type.slot)
            )
        )
        return ItemAttributeModifiers(list, true)
    }
}
