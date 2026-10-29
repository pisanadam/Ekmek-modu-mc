package com.pisanadam.ekmek.registry;

import com.pisanadam.ekmek.EkmekModu;
import com.pisanadam.ekmek.compat.Compat;
import com.pisanadam.ekmek.block.ModCropBlock;
import com.pisanadam.ekmek.block.ProofingTroughBlock;
import com.pisanadam.ekmek.block.StoneOvenBlock;
import java.util.function.Function;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;

public final class ModBlocks {
	public static final Block STONE_OVEN = register("stone_oven", StoneOvenBlock::new,
		BlockBehaviour.Properties.of()
			.mapColor(MapColor.STONE)
			.instrument(NoteBlockInstrument.BASEDRUM)
			.requiresCorrectToolForDrops()
			.strength(3.5F)
			.lightLevel(state -> state.getValue(StoneOvenBlock.LIT) ? 13 : 0));

	public static final Block PROOFING_TROUGH = register("proofing_trough", ProofingTroughBlock::new,
		BlockBehaviour.Properties.of()
			.mapColor(MapColor.WOOD)
			.instrument(NoteBlockInstrument.BASS)
			.strength(2.0F)
			.sound(SoundType.WOOD)
			.noOcclusion()
			.ignitedByLava());

	public static final Block RYE_CROP = registerCrop("rye_crop", () -> ModItems.RYE_SEEDS);
	public static final Block CORN_CROP = registerCrop("corn_crop", () -> ModItems.CORN_SEEDS);
	public static final Block SESAME_CROP = registerCrop("sesame_crop", () -> ModItems.SESAME_SEEDS);

	private ModBlocks() {
	}

	private static Block registerCrop(String name, java.util.function.Supplier<net.minecraft.world.level.ItemLike> seed) {
		return register(name, props -> new ModCropBlock(props, seed),
			BlockBehaviour.Properties.of()
				.mapColor(MapColor.PLANT)
				.noCollision()
				.randomTicks()
				.instabreak()
				.sound(SoundType.CROP)
				.pushReaction(Compat.cropPushReaction()));
	}

	private static Block register(String name, Function<BlockBehaviour.Properties, Block> factory, BlockBehaviour.Properties properties) {
		ResourceKey<Block> key = ResourceKey.create(Registries.BLOCK, EkmekModu.id(name));
		Block block = factory.apply(properties.setId(key));
		return Registry.register(BuiltInRegistries.BLOCK, key, block);
	}

	public static void init() {
	}
}
