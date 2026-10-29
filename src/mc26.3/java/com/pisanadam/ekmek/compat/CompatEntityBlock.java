package com.pisanadam.ekmek.compat;

import java.util.function.Function;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;

public abstract class CompatEntityBlock extends BaseEntityBlock {
	protected <B extends CompatEntityBlock> CompatEntityBlock(BlockBehaviour.Properties properties, Function<BlockBehaviour.Properties, B> factory) {
		super(properties);
	}
}
