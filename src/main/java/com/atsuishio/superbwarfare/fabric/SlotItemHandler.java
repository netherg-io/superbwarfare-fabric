package com.atsuishio.superbwarfare.fabric;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

/** Замена net.neoforged.neoforge.items.SlotItemHandler: слот меню поверх IItemHandler. */
public class SlotItemHandler extends Slot {
    private static final Container EMPTY_INVENTORY = new SimpleContainer(0);

    private final IItemHandler itemHandler;
    protected final int index;

    public SlotItemHandler(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
        super(EMPTY_INVENTORY, index, xPosition, yPosition);
        this.itemHandler = itemHandler;
        this.index = index;
    }

    public IItemHandler getItemHandler() {
        return itemHandler;
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return !stack.isEmpty() && itemHandler.isItemValid(index, stack);
    }

    @Override
    public ItemStack getItem() {
        return itemHandler.getStackInSlot(index);
    }

    @Override
    public void set(ItemStack stack) {
        itemHandler.setStackInSlot(index, stack);
        this.setChanged();
    }

    @Override
    public void onQuickCraft(ItemStack oldStack, ItemStack newStack) {}

    @Override
    public int getMaxStackSize() {
        return itemHandler.getSlotLimit(index);
    }

    @Override
    public int getMaxStackSize(ItemStack stack) {
        return Math.min(stack.getMaxStackSize(), itemHandler.getSlotLimit(index));
    }

    @Override
    public boolean mayPickup(Player player) {
        return !itemHandler.extractItem(index, 1, true).isEmpty();
    }

    @Override
    public ItemStack remove(int amount) {
        return itemHandler.extractItem(index, amount, false);
    }
}
