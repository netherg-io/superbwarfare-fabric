package com.atsuishio.superbwarfare.fabric;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

/** Замена net.neoforged.neoforge.items.wrapper.RecipeWrapper: IItemHandler как вход рецепта. */
public class RecipeWrapper implements RecipeInput {
    protected final IItemHandler inv;

    public RecipeWrapper(IItemHandler inv) {
        this.inv = inv;
    }

    @Override
    public int size() {
        return inv.getSlots();
    }

    @Override
    public ItemStack getItem(int slot) {
        return inv.getStackInSlot(slot);
    }
}
