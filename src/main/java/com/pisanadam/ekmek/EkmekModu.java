package com.pisanadam.ekmek;

import com.pisanadam.ekmek.event.BoilingHandler;
import com.pisanadam.ekmek.event.EasterEggs;
import com.pisanadam.ekmek.event.LootHandler;
import com.pisanadam.ekmek.recipe.BakingRecipes;
import com.pisanadam.ekmek.recipe.ProofingRecipes;
import com.pisanadam.ekmek.registry.ModBlockEntities;
import com.pisanadam.ekmek.registry.ModBlocks;
import com.pisanadam.ekmek.registry.ModCreativeTab;
import com.pisanadam.ekmek.registry.ModItems;
import net.fabricmc.api.ModInitializer;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EkmekModu implements ModInitializer {
	public static final String MOD_ID = "ekmekmodu";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}

	@Override
	public void onInitialize() {
		ModBlocks.init();
		ModItems.init();
		ModBlockEntities.init();
		BakingRecipes.init();
		ProofingRecipes.init();
		ModCreativeTab.init();
		LootHandler.init();
		BoilingHandler.init();
		EasterEggs.init();
		LOGGER.info("Ekmek Modu yüklendi: {} çeşit ekmek fırına girmeye hazır!", ModItems.BREADS.size());
	}
}
