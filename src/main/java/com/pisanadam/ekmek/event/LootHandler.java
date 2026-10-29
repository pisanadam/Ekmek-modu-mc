package com.pisanadam.ekmek.event;

import com.pisanadam.ekmek.registry.ModItems;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;

/** Yeni tohumları ve zeytini dünyaya ekler: çimenlerden tohum, meşe yapraklarından zeytin düşer. */
public final class LootHandler {
	private LootHandler() {
	}

	public static void init() {
		LootTableEvents.MODIFY.register((key, tableBuilder, source, registries) -> {
			if (!source.isBuiltin()) {
				return;
			}
			if (is(key, Blocks.SHORT_GRASS) || is(key, Blocks.TALL_GRASS) || is(key, Blocks.FERN)) {
				addChance(tableBuilder, ModItems.RYE_SEEDS, 0.04F);
				addChance(tableBuilder, ModItems.CORN_SEEDS, 0.03F);
				addChance(tableBuilder, ModItems.SESAME_SEEDS, 0.03F);
			} else if (is(key, Blocks.OAK_LEAVES) || is(key, Blocks.DARK_OAK_LEAVES)) {
				addChance(tableBuilder, ModItems.OLIVES, 0.04F);
			}
		});
	}

	private static boolean is(net.minecraft.resources.ResourceKey<LootTable> key, Block block) {
		return block.getLootTable().map(key::equals).orElse(false);
	}

	private static void addChance(LootTable.Builder table, Item item, float chance) {
		table.withPool(LootPool.lootPool()
			.add(LootItem.lootTableItem(item))
			.when(LootItemRandomChanceCondition.randomChance(chance)));
	}
}
