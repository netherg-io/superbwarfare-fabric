package com.atsuishio.superbwarfare.client

import com.atsuishio.superbwarfare.client.animation.AnimationCurves
import com.atsuishio.superbwarfare.client.decorator.ContainerItemDecorator
import com.atsuishio.superbwarfare.client.decorator.LuckyContainerItemDecorator
import com.atsuishio.superbwarfare.client.decorator.VehicleKeyItemDecorator
import com.atsuishio.superbwarfare.client.model.curio.ParachuteModel
import com.atsuishio.superbwarfare.client.model.curio.ThermalImagingGogglesModel
import com.atsuishio.superbwarfare.client.overlay.*
import com.atsuishio.superbwarfare.client.renderer.block.*
import com.atsuishio.superbwarfare.client.renderer.curio.ParachuteRenderer
import com.atsuishio.superbwarfare.client.renderer.curio.ThermalImagingGogglesRenderer
import com.atsuishio.superbwarfare.client.tooltip.*
import com.atsuishio.superbwarfare.client.tooltip.component.*
import com.atsuishio.superbwarfare.init.ModBlockEntities
import com.atsuishio.superbwarfare.init.ModItems
import com.atsuishio.superbwarfare.tools.localPlayer
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.world.entity.projectile.Projectile
import net.minecraft.world.phys.Vec3
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback
import net.fabricmc.fabric.api.client.rendering.v1.TooltipComponentCallback
import io.wispforest.accessories.api.client.AccessoriesRendererRegistry
import kotlin.math.min

@Environment(EnvType.CLIENT)
object ClientRenderHandler {
    fun init() {
        registerTooltip()
        registerRenderers()
        registerOverlays()
        registerLayer()
        registerAccessoryRenderers()
    }

    // TODO 正确赋值该变量
    @JvmStatic
    var bulletRenderOffset: Vec3? = null

    /**
     * 修改子弹类实体的虚拟渲染位置
     */
    @JvmStatic
    fun transformVirtualRenderPosition(stack: PoseStack, projectile: Projectile, partialTick: Float) {
        if (bulletRenderOffset == null) return

        val player = localPlayer
        if (player == null || projectile.owner == null || (player.getUUID() != projectile.owner!!.getUUID())) return

        val rate = 1 - AnimationCurves.EASE_OUT_CIRC.apply(min(1.0, (projectile.tickCount + partialTick) / 5.0))
        val offset = bulletRenderOffset!!.subtract(projectile.position()).multiply(rate, rate, rate)
        stack.translate(offset.x, offset.y, offset.z)
    }

    // TooltipComponentCallback -- один слушатель на все типы вместо реестра по классу,
    // поэтому подклассы GunImageComponent обязаны стоять выше самого GunImageComponent.
    private fun registerTooltip() {
        TooltipComponentCallback.EVENT.register { data ->
            when (data) {
                is BocekImageComponent -> ClientBocekImageTooltip(data)
                is SentinelImageComponent -> ClientSentinelImageTooltip(data)
                is ChargingStationImageComponent -> ClientChargingStationImageTooltip(data)
                is GunImageComponent -> ClientGunImageTooltip(data)
                is CellImageComponent -> ClientCellImageTooltip(data)
                is DogTagImageComponent -> ClientDogTagImageTooltip(data)
                else -> null
            }
        }
    }

    private fun registerRenderers() {
        BlockEntityRendererRegistry.register(
            ModBlockEntities.CONTAINER.get(),
            BlockEntityRendererProvider { ContainerBlockEntityRenderer() })
        BlockEntityRendererRegistry.register(
            ModBlockEntities.FUMO_25.get(),
            BlockEntityRendererProvider { FuMO25BlockEntityRenderer() })
        BlockEntityRendererRegistry.register(
            ModBlockEntities.CHARGING_STATION.get(),
            BlockEntityRendererProvider { ChargingStationBlockEntityRenderer() })
        BlockEntityRendererRegistry.register(
            ModBlockEntities.SMALL_CONTAINER.get(),
            BlockEntityRendererProvider { SmallContainerBlockEntityRenderer() })
        BlockEntityRendererRegistry.register(
            ModBlockEntities.LUCKY_CONTAINER.get(),
            BlockEntityRendererProvider { LuckyContainerBlockEntityRenderer() })
        BlockEntityRendererRegistry.register(
            ModBlockEntities.VEHICLE_ASSEMBLING_TABLE.get(),
            BlockEntityRendererProvider { VehicleAssemblingTableBlockEntityRenderer() })
        BlockEntityRendererRegistry.register(
            ModBlockEntities.BLUEPRINT_RESEARCH_TABLE.get(),
            BlockEntityRendererProvider { BlueprintResearchTableBlockEntityRenderer() })
    }

    // У Fabric API 1.21.1 нет аналога RegisterGuiLayersEvent: HudRenderCallback рисует
    // строго поверх ванильного HUD, а порядок слоёв задаётся только порядком регистрации.
    // Цепочка registerBelow/registerBelowAll в NeoForge давала порядок отрисовки, обратный
    // порядку вызовов, поэтому здесь слои перечислены в обратном порядке.
    private fun registerOverlays() {
        val layers = listOf(
            SodayoRocketInfoOverlay,
            Type63InfoOverlay,
            MortarInfoOverlay,
            TowOverlay,
            SpyglassRangeOverlay,
            HandsomeFrameOverlay,
            RedTriangleOverlay,
            DroneHudOverlay,
            HeatBarOverlay,
            CrossHairOverlay,
            ItemRendererFixOverlay,
            AmmoCountOverlay,
            StaminaOverlay,
            VehicleCrosshairOverlay,
            GPWSOverlay,
            VehicleMainWeaponHudOverlay,
            VehicleHudOverlay,
            IglaHudOverlay,
            JavelinHudOverlay,
            VehicleTeamOverlay,
            IFFOverlay,
            AmmoBarOverlay,
            ArmorPlateOverlay,
            KillMessageOverlay,
        )
        layers.forEach { layer ->
            HudRenderCallback.EVENT.register { guiGraphics, deltaTracker ->
                layer.render(guiGraphics, deltaTracker)
            }
        }
    }

    // ponytail: у Fabric API 1.21.1 нет аналога RegisterItemDecorationsEvent, регистрировать негде.
    // Пары «предмет -> декоратор» сохранены дословно; подключить миксином на
    // GuiGraphics.renderItemDecorations и удалить эту заглушку.
    @Suppress("unused")
    private fun registerItemDecorations() = listOf(
        ModItems.CONTAINER.get() to ContainerItemDecorator(),
        ModItems.LUCKY_CONTAINER.get() to LuckyContainerItemDecorator(),
        ModItems.VEHICLE_KEY.get() to VehicleKeyItemDecorator(),
    )

    private fun registerAccessoryRenderers() {
        AccessoriesRendererRegistry.registerRenderer(ModItems.PARACHUTE.get()) { ParachuteRenderer() }
        AccessoriesRendererRegistry.registerRenderer(ModItems.THERMAL_IMAGING_GOGGLES.get()) { ThermalImagingGogglesRenderer() }
    }

    private fun registerLayer() {
        EntityModelLayerRegistry.registerModelLayer(ParachuteModel.LAYER_LOCATION) { ParachuteModel.createBodyLayer() }
        EntityModelLayerRegistry.registerModelLayer(ThermalImagingGogglesModel.LAYER_LOCATION) { ThermalImagingGogglesModel.createBodyLayer() }
    }
}