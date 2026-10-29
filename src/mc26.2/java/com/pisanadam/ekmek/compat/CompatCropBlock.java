package com.pisanadam.ekmek.compat;

import com.mojang.serialization.MapCodec;
import java.util.function.Function;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;

public abstract class CompatCropBlock extends CropBlock {
	private final MapCodec<? extends CropBlock> codec;

	protected <B extends CompatCropBlock> CompatCropBlock(BlockBehaviour.Properties properties, Function<BlockBehaviour.Properties, B> factory) {
		super(properties);
		this.codec = simpleCodec(factory);
	}

	@Override
	public MapCodec<? extends CropBlock> codec() {
		return this.codec;
	}
}
