package com.pisanadam.ekmek.compat;

import com.mojang.serialization.MapCodec;
import java.util.function.Function;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;

public abstract class CompatEntityBlock extends BaseEntityBlock {
	private final MapCodec<? extends BaseEntityBlock> codec;

	protected <B extends CompatEntityBlock> CompatEntityBlock(BlockBehaviour.Properties properties, Function<BlockBehaviour.Properties, B> factory) {
		super(properties);
		this.codec = simpleCodec(factory);
	}

	@Override
	protected MapCodec<? extends BaseEntityBlock> codec() {
		return this.codec;
	}
}
