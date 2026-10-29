package com.pisanadam.ekmek.compat;

import com.mojang.serialization.MapCodec;
import java.util.function.Function;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;

public abstract class CompatFurnaceBlock extends AbstractFurnaceBlock {
	private final MapCodec<? extends AbstractFurnaceBlock> codec;

	protected <B extends CompatFurnaceBlock> CompatFurnaceBlock(BlockBehaviour.Properties properties, Function<BlockBehaviour.Properties, B> factory) {
		super(properties);
		this.codec = simpleCodec(factory);
	}

	@Override
	protected MapCodec<? extends AbstractFurnaceBlock> codec() {
		return this.codec;
	}
}
