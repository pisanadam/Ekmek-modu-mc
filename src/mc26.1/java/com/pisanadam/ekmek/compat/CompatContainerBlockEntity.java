package com.pisanadam.ekmek.compat;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class CompatContainerBlockEntity extends BaseContainerBlockEntity {
	protected CompatContainerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	protected int burnDuration(ServerLevel level, ItemStack fuel) {
		return level.fuelValues().burnDuration(fuel);
	}

	protected static boolean isFuel(Level level, ItemStack stack) {
		return level.fuelValues().isFuel(stack);
	}
}
