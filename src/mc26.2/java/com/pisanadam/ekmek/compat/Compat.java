package com.pisanadam.ekmek.compat;

import net.fabricmc.fabric.api.registry.CompostableRegistry;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.PushReaction;

/** Minecraft 26.2 için sürüme özel çağrılar. */
public final class Compat {
	private Compat() {
	}

	public static void giveItem(Player player, ItemStack stack) {
		player.getInventory().placeItemBackInInventory(stack);
	}

	public static void spawnVisualLightning(ServerLevel level, double x, double y, double z) {
		LightningBolt bolt = new LightningBolt(EntityTypes.LIGHTNING_BOLT, level);
		bolt.setVisualOnly(true);
		bolt.setPos(x, y, z);
		level.addFreshEntity(bolt);
	}

	public static Item.Properties compostable(Item.Properties properties, float chance) {
		return properties;
	}

	public static void registerCompostable(Item item, float chance) {
		CompostableRegistry.INSTANCE.add(item, chance);
	}

	public static PushReaction cropPushReaction() {
		return PushReaction.DESTROY;
	}
}
