package com.pisanadam.ekmek.block.entity;

import com.pisanadam.ekmek.recipe.BakingRecipes;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.FurnaceMenu;
import net.minecraft.world.item.ItemStack;

/**
 * Taş fırın menüsü. İstemcide vanilla fırın ekranı kullanılır (ek istemci kodu gerekmez);
 * sunucu tarafında shift+tık ile hamurların doğru yuvaya gitmesi için tarif kontrolü değiştirilir.
 */
public class StoneOvenMenu extends FurnaceMenu {
	public StoneOvenMenu(int containerId, Inventory inventory, Container container, ContainerData data) {
		super(containerId, inventory, container, data);
	}

	@Override
	protected boolean canSmelt(ItemStack itemStack) {
		return BakingRecipes.canBake(itemStack);
	}
}
