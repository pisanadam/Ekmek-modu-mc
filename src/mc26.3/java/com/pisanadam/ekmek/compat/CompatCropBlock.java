package com.pisanadam.ekmek.compat;

import java.util.function.Function;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;

public abstract class CompatCropBlock extends CropBlock {
	protected <B extends CompatCropBlock> CompatCropBlock(BlockBehaviour.Properties properties, Function<BlockBehaviour.Properties, B> factory) {
		super(properties);
	}
}
