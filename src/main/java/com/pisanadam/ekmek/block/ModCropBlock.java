package com.pisanadam.ekmek.block;

import com.pisanadam.ekmek.compat.CompatCropBlock;
import java.util.function.Supplier;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.state.BlockBehaviour;

/** Çavdar, mısır ve susam için 8 evreli (0-7) ekin bloğu. */
public class ModCropBlock extends CompatCropBlock {
	private final Supplier<ItemLike> seed;

	public ModCropBlock(BlockBehaviour.Properties properties, Supplier<ItemLike> seed) {
		super(properties, props -> new ModCropBlock(props, seed));
		this.seed = seed;
	}

	@Override
	protected ItemLike getBaseSeedId() {
		return this.seed.get();
	}
}
