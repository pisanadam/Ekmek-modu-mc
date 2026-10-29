package com.pisanadam.ekmek.compat;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Prediction;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.storage.loot.providers.number.ints.ContextIntProviders;

/** Minecraft 26.3 için sürüme özel çağrılar. */
public final class Compat {
	private Compat() {
	}

	public static void giveItem(Player player, ItemStack stack) {
		player.getInventory().placeItemBackInInventory(stack, Prediction.SERVER_ONLY);
	}

	public static void spawnVisualLightning(ServerLevel level, double x, double y, double z) {
		LightningBolt bolt = new LightningBolt(EntityTypes.LIGHTNING_BOLT, level);
		bolt.setVisualOnly(true);
		bolt.setPos(x, y, z);
		level.addFreshEntity(bolt);
	}

	public static Item.Properties compostable(Item.Properties properties, float chance) {
		if (chance >= 0.85F) {
			return properties.compostable(ContextIntProviders.COMPOSTABLE_MEDIUM_HIGH);
		} else if (chance >= 0.65F) {
			return properties.compostable(ContextIntProviders.COMPOSTABLE_MEDIUM);
		} else if (chance >= 0.5F) {
			return properties.compostable(ContextIntProviders.COMPOSTABLE_LOW_MEDIUM);
		}
		return properties.compostable(ContextIntProviders.COMPOSTABLE_LOW);
	}

	public static void registerCompostable(Item item, float chance) {
		// 26.3'te kompost bir eşya bileşenidir; compostable(...) ile ayarlanır
	}

	public static PushReaction cropPushReaction() {
		return PushReaction.POPPED;
	}
}
