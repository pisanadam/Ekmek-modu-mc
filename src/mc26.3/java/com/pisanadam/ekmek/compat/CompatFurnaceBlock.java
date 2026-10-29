package com.pisanadam.ekmek.compat;

import java.util.function.Function;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;

public abstract class CompatFurnaceBlock extends AbstractFurnaceBlock {
	protected <B extends CompatFurnaceBlock> CompatFurnaceBlock(BlockBehaviour.Properties properties, Function<BlockBehaviour.Properties, B> factory) {
		super(properties);
	}
}
