package com.pisanadam.ekmek.registry;

import com.pisanadam.ekmek.EkmekModu;
import net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTab;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public final class ModCreativeTab {
	public static final CreativeModeTab TAB = Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, EkmekModu.id("ekmek"),
		FabricCreativeModeTab.builder()
			.title(Component.translatable("itemGroup.ekmekmodu.ekmek"))
			.icon(() -> new ItemStack(ModItems.SOURDOUGH.bread()))
			.displayItems((params, output) -> {
				for (Item item : ModItems.ORDERED) {
					output.accept(item);
				}
			})
			.build());

	private ModCreativeTab() {
	}

	public static void init() {
	}
}
