package com.pisanadam.ekmek.compat;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CookingFuel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.providers.number.ints.ResolvableInt;

public abstract class CompatContainerBlockEntity extends BaseContainerBlockEntity {
	protected CompatContainerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	protected int burnDuration(ServerLevel level, ItemStack fuel) {
		return ResolvableInt.getFromItem(fuel, DataComponents.COOKING_FUEL, CookingFuel::burnTime, this.getLootContext(level), 0);
	}

	protected static boolean isFuel(Level level, ItemStack stack) {
		return stack.has(DataComponents.COOKING_FUEL);
	}
}
