package com.pisanadam.ekmek.block.entity;

import com.pisanadam.ekmek.recipe.BakingRecipes;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.FurnaceMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

/**
 * Taş fırın menüsü. İstemcide vanilla fırın ekranı kullanılır (ek istemci kodu gerekmez).
 * Sunucu tarafında giriş yuvası yalnızca pişirilebilir hamurları kabul eder ve
 * shift+tık ile hamurlar doğru yuvaya gider.
 */
public class StoneOvenMenu extends FurnaceMenu {
	public StoneOvenMenu(int containerId, Inventory inventory, Container container, ContainerData data) {
		super(containerId, inventory, container, data);
		Slot vanillaInput = this.slots.get(0);
		Slot input = new Slot(container, 0, vanillaInput.x, vanillaInput.y) {
			@Override
			public boolean mayPlace(ItemStack itemStack) {
				return BakingRecipes.canBake(itemStack);
			}
		};
		input.index = 0;
		this.slots.set(0, input);
	}

	@Override
	protected boolean canSmelt(ItemStack itemStack) {
		return BakingRecipes.canBake(itemStack);
	}
}
