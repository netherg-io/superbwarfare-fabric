package com.atsuishio.superbwarfare.init

import net.minecraft.client.model.HumanoidModel.ArmPose
import net.minecraft.world.item.Rarity
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment

/**
 * Расширения ванильных енумов: у NeoForge это RegisterEnumExtensionsEvent + EnumProxy,
 * у Fabric штатного механизма нет.
 *
 * ponytail: все расширения сведены к ближайшим ванильным константам. Что этим потеряно:
 *
 * 1. Rarity. Три собственные редкости (legendary/GOLD, superb/RED, virtual/розовый градиент)
 *    схлопнуты в Rarity.EPIC, то есть имена предметов этих трёх редкостей красятся
 *    в светло-фиолетовый вместо золотого/красного/розового. Поведение (сортировка, коды
 *    в компонентах предмета, теги чертежей) не зависит от редкости и не меняется.
 *    Дополнительная причина не тянуть сюда Fabric-ASM: `virtual` строится через конструктор
 *    Rarity(int, String, UnaryOperator<Style>), которого в ванили 1.21.1 нет вообще -- его
 *    добавляет патч NeoForge. Даже с расширением енума пришлось бы ещё и миксить
 *    Rarity.color()/ItemStack.getStyledHoverName().
 *
 * 2. ArmPose. Четыре собственные позы рук (LUNGE_MINE, MINIGUN, M2, SUPER_STAR_SHOOTER)
 *    сведены к ArmPose.CROSSBOW_HOLD -- ближайшей ванильной двуручной позе «держит оружие
 *    перед собой». Точные углы плеч из апстрима потеряны.
 *
 * Как вернуть по-настоящему (обе части, ~день работы, требует правок вне пакета init):
 *   - Fabric-ASM (com.github.Chocohead:Fabric-ASM v2.3) уже на classpath транзитивно
 *     от Porting Lib. Расширение енума делается так:
 *       ClassTinkerers.enumBuilder(clazz, paramTypes...).addEnum(name, args...).build()
 *     в entrypoint'е `mm:early_risers`, а обратно значение достаётся
 *     ClassTinkerers.getEnum(clazz, name).
 *   - Это требует правки ОБЩЕГО src/main/resources/fabric.mod.json (entrypoint early_risers)
 *     и зависимости на Fabric-ASM явной строкой в build.gradle.kts, а не транзитивной.
 *   - Для ArmPose одного добавления константы мало: HumanoidModel.poseRightArm/poseLeftArm --
 *     это switch по енуму, и новая константа попадёт в default. Нужен ещё миксин
 *     в HumanoidModel, который для наших констант вызывает свой трансформер.
 *   - Для Rarity дополнительно нужен миксин в Rarity.color() (или в место,
 *     где строится Style имени предмета), потому что ванильный конструктор
 *     принимает только ChatFormatting.
 */
object ModEnumExtensions {
    @JvmField
    val legendary: Rarity = Rarity.EPIC

    @JvmField
    val superb: Rarity = Rarity.EPIC

    @JvmField
    val virtual: Rarity = Rarity.EPIC

    @Environment(EnvType.CLIENT)
    object Client {
        @JvmStatic
        val lungeMinePose: ArmPose = ArmPose.CROSSBOW_HOLD

        @JvmStatic
        val minigunPose: ArmPose = ArmPose.CROSSBOW_HOLD

        @JvmStatic
        val m2Pose: ArmPose = ArmPose.CROSSBOW_HOLD

        @JvmStatic
        val superStarShooterPose: ArmPose = ArmPose.CROSSBOW_HOLD
    }
}
