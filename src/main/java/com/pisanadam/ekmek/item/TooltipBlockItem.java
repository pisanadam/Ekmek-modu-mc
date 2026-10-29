package com.pisanadam.ekmek.item;

import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.block.Block;

/** Açıklama satırı gösteren blok eşyası (tohumlar, fırın, tekne). */
public class TooltipBlockItem extends BlockItem {
	private final String tooltipKey;

	public TooltipBlockItem(Block block, Item.Properties properties, String tooltipKey) {
		super(block, properties);
		this.tooltipKey = tooltipKey;
	}

	@Override
	@SuppressWarnings("deprecation")
	public void appendHoverText(ItemStack itemStack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> builder, TooltipFlag tooltipFlag) {
		builder.accept(Component.translatable(this.tooltipKey).withStyle(ChatFormatting.GRAY));
	}
}
