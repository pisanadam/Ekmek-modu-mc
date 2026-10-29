package com.pisanadam.ekmek.item;

import com.pisanadam.ekmek.event.EasterEggs;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;

/** Ekmek eşyası. Ateşe atılırsa... ekmek çarpar. */
public class BreadItem extends TooltipItem {
	public BreadItem(Item.Properties properties, String tooltipKey) {
		super(properties, tooltipKey);
	}

	@Override
	public void onDestroyed(ItemEntity itemEntity) {
		super.onDestroyed(itemEntity);
		EasterEggs.onBreadDestroyed(itemEntity);
	}
}
