package com.pisanadam.ekmek.registry;

import com.pisanadam.ekmek.EkmekModu;
import com.pisanadam.ekmek.block.entity.ProofingTroughBlockEntity;
import com.pisanadam.ekmek.block.entity.StoneOvenBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;

public final class ModBlockEntities {
	public static final BlockEntityType<StoneOvenBlockEntity> STONE_OVEN = Registry.register(
		BuiltInRegistries.BLOCK_ENTITY_TYPE, EkmekModu.id("stone_oven"),
		FabricBlockEntityTypeBuilder.create(StoneOvenBlockEntity::new, ModBlocks.STONE_OVEN).build());

	public static final BlockEntityType<ProofingTroughBlockEntity> PROOFING_TROUGH = Registry.register(
		BuiltInRegistries.BLOCK_ENTITY_TYPE, EkmekModu.id("proofing_trough"),
		FabricBlockEntityTypeBuilder.create(ProofingTroughBlockEntity::new, ModBlocks.PROOFING_TROUGH).build());

	private ModBlockEntities() {
	}

	public static void init() {
	}
}
