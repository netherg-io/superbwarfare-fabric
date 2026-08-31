package com.atsuishio.superbwarfare.init

/**
 * Общая сторона: вызывать из ModInitializer.
 *
 * Апстрим регистрировал здесь capability-провайдеры NeoForge:
 *   - Capabilities.EnergyStorage.BLOCK  -- зарядная станция, креативная зарядная станция, FuMO25;
 *   - Capabilities.ItemHandler.BLOCK    -- зарядная станция, стол исследования чертежей,
 *                                         卓越物品接口 (SuperbItemInterface);
 *   - Capabilities.EnergyStorage.ITEM   -- креативная зарядная станция и все EnergyStorageItem;
 *   - Capabilities.EnergyStorage.ENTITY -- любая VehicleEntity с батареей и DPS_GENERATOR;
 *   - Capabilities.ItemHandler.ENTITY   -- любая VehicleEntity с инвентарём.
 *
 * Прямого аналога RegisterCapabilitiesEvent на Fabric нет, и текущий набор зависимостей
 * закрывает только половину задачи:
 *   - предметы/инвентари: fabric-transfer-api-v1 (ItemStorage.SIDED) плюс обёртки
 *     io.github.fabricators_of_create.porting_lib.transfer.item.wrapper.*, которыми можно
 *     заменить SidedInvWrapper/InvWrapper;
 *   - энергия: аналога нет вообще. В Porting Lib модуля energy нет (проверено по содержимому
 *     jar-ов core/entity/level_events/client_events/transfer/items), в Fabric API энергии нет,
 *     team-reborn Energy в зависимостях не подключён.
 *
 * Переносить провайдеры в одиночку смысла нет: 33 файла мода вызывают getCapability на
 * стороне потребителя, и все они должны переехать на ту же замену одним куском.
 * Поэтому здесь пока пусто.
 */
object ModCapabilities {
    fun init() = Unit
}
