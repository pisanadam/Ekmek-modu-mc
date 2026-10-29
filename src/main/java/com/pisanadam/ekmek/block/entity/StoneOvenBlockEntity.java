package com.pisanadam.ekmek.block.entity;

import com.pisanadam.ekmek.compat.CompatContainerBlockEntity;
import com.pisanadam.ekmek.recipe.BakingRecipes;
import com.pisanadam.ekmek.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.Nullable;

public class StoneOvenBlockEntity extends CompatContainerBlockEntity implements WorldlyContainer {
	public static final int SLOT_INPUT = 0;
	public static final int SLOT_FUEL = 1;
	public static final int SLOT_RESULT = 2;
	private static final int[] SLOTS_FOR_UP = {SLOT_INPUT};
	private static final int[] SLOTS_FOR_DOWN = {SLOT_RESULT, SLOT_FUEL};
	private static final int[] SLOTS_FOR_SIDES = {SLOT_FUEL};

	private NonNullList<ItemStack> items = NonNullList.withSize(3, ItemStack.EMPTY);
	private int litTime;
	private int litDuration;
	private int cookingProgress;
	private int cookingTotalTime;

	private final ContainerData dataAccess = new ContainerData() {
		@Override
		public int get(int index) {
			return switch (index) {
				case 0 -> StoneOvenBlockEntity.this.litTime;
				case 1 -> StoneOvenBlockEntity.this.litDuration;
				case 2 -> StoneOvenBlockEntity.this.cookingProgress;
				case 3 -> StoneOvenBlockEntity.this.cookingTotalTime;
				default -> 0;
			};
		}

		@Override
		public void set(int index, int value) {
			switch (index) {
				case 0 -> StoneOvenBlockEntity.this.litTime = value;
				case 1 -> StoneOvenBlockEntity.this.litDuration = value;
				case 2 -> StoneOvenBlockEntity.this.cookingProgress = value;
				case 3 -> StoneOvenBlockEntity.this.cookingTotalTime = value;
				default -> {
				}
			}
		}

		@Override
		public int getCount() {
			return 4;
		}
	};

	public StoneOvenBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlockEntities.STONE_OVEN, pos, state);
	}

	public static void serverTick(ServerLevel level, BlockPos pos, BlockState state, StoneOvenBlockEntity oven) {
		boolean changed = false;
		boolean wasLit = oven.litTime > 0;
		if (wasLit) {
			oven.litTime--;
		}

		ItemStack input = oven.items.get(SLOT_INPUT);
		ItemStack fuel = oven.items.get(SLOT_FUEL);
		BakingRecipes.Baking recipe = BakingRecipes.get(input);
		boolean canBake = recipe != null && canOutput(oven, recipe.resultStack());

		if (oven.litTime <= 0 && canBake && !fuel.isEmpty()) {
			int burn = oven.burnDuration(level, fuel);
			if (burn > 0) {
				oven.litTime = burn;
				oven.litDuration = burn;
				if (fuel.is(Items.LAVA_BUCKET)) {
					oven.items.set(SLOT_FUEL, new ItemStack(Items.BUCKET));
				} else {
					fuel.shrink(1);
				}
				changed = true;
			}
		}

		boolean isLit = oven.litTime > 0;
		if (isLit && canBake) {
			oven.cookingTotalTime = recipe.ticks();
			oven.cookingProgress++;
			if (oven.cookingProgress >= oven.cookingTotalTime) {
				oven.cookingProgress = 0;
				bake(oven, input, recipe);
				changed = true;
			}
		} else if (!canBake) {
			oven.cookingProgress = 0;
		} else if (oven.cookingProgress > 0) {
			// fırın soğurken pişirme geriler
			oven.cookingProgress = Math.max(0, oven.cookingProgress - 2);
		}

		if (wasLit != isLit) {
			state = state.setValue(AbstractFurnaceBlock.LIT, isLit);
			level.setBlock(pos, state, 3);
			changed = true;
		}

		if (changed) {
			setChanged(level, pos, state);
		}
	}

	private static boolean canOutput(StoneOvenBlockEntity oven, ItemStack result) {
		ItemStack current = oven.items.get(SLOT_RESULT);
		if (current.isEmpty()) {
			return true;
		}
		if (!ItemStack.isSameItemSameComponents(current, result)) {
			return false;
		}
		return current.getCount() + result.getCount() <= Math.min(oven.getMaxStackSize(), current.getMaxStackSize());
	}

	private static void bake(StoneOvenBlockEntity oven, ItemStack input, BakingRecipes.Baking recipe) {
		ItemStack current = oven.items.get(SLOT_RESULT);
		if (current.isEmpty()) {
			oven.items.set(SLOT_RESULT, recipe.resultStack());
		} else {
			current.grow(recipe.count());
		}
		input.shrink(1);
	}

	@Override
	protected Component getDefaultName() {
		return Component.translatable("container.ekmekmodu.stone_oven");
	}

	@Override
	protected AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
		return new StoneOvenMenu(containerId, inventory, this, this.dataAccess);
	}

	@Override
	protected NonNullList<ItemStack> getItems() {
		return this.items;
	}

	@Override
	protected void setItems(NonNullList<ItemStack> items) {
		this.items = items;
	}

	@Override
	public int getContainerSize() {
		return this.items.size();
	}

	@Override
	public void setItem(int slot, ItemStack itemStack) {
		ItemStack old = this.items.get(slot);
		boolean same = !itemStack.isEmpty() && ItemStack.isSameItemSameComponents(old, itemStack);
		super.setItem(slot, itemStack);
		if (slot == SLOT_INPUT && !same) {
			BakingRecipes.Baking recipe = BakingRecipes.get(itemStack);
			this.cookingTotalTime = recipe != null ? recipe.ticks() : 0;
			this.cookingProgress = 0;
		}
	}

	@Override
	public boolean canPlaceItem(int slot, ItemStack itemStack) {
		return switch (slot) {
			case SLOT_INPUT -> BakingRecipes.canBake(itemStack);
			case SLOT_FUEL -> this.level != null && isFuel(this.level, itemStack);
			default -> false;
		};
	}

	@Override
	public int[] getSlotsForFace(Direction direction) {
		if (direction == Direction.DOWN) {
			return SLOTS_FOR_DOWN;
		}
		return direction == Direction.UP ? SLOTS_FOR_UP : SLOTS_FOR_SIDES;
	}

	@Override
	public boolean canPlaceItemThroughFace(int slot, ItemStack itemStack, @Nullable Direction direction) {
		return this.canPlaceItem(slot, itemStack);
	}

	@Override
	public boolean canTakeItemThroughFace(int slot, ItemStack itemStack, Direction direction) {
		return slot == SLOT_RESULT || (slot == SLOT_FUEL && itemStack.is(Items.BUCKET));
	}

	@Override
	protected void loadAdditional(ValueInput input) {
		super.loadAdditional(input);
		this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
		ContainerHelper.loadAllItems(input, this.items);
		this.litTime = input.getIntOr("lit_time", 0);
		this.litDuration = input.getIntOr("lit_duration", 0);
		this.cookingProgress = input.getIntOr("cooking_progress", 0);
		this.cookingTotalTime = input.getIntOr("cooking_total_time", 0);
	}

	@Override
	protected void saveAdditional(ValueOutput output) {
		super.saveAdditional(output);
		ContainerHelper.saveAllItems(output, this.items);
		output.putInt("lit_time", this.litTime);
		output.putInt("lit_duration", this.litDuration);
		output.putInt("cooking_progress", this.cookingProgress);
		output.putInt("cooking_total_time", this.cookingTotalTime);
	}
}
