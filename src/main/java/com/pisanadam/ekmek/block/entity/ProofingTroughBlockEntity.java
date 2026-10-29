package com.pisanadam.ekmek.block.entity;

import com.pisanadam.ekmek.block.ProofingTroughBlock;
import com.pisanadam.ekmek.compat.Compat;
import com.pisanadam.ekmek.event.EasterEggs;
import com.pisanadam.ekmek.recipe.ProofingRecipes;
import com.pisanadam.ekmek.registry.ModBlockEntities;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class ProofingTroughBlockEntity extends BlockEntity implements Container {
	public static final int CAPACITY = 16;
	private static final int WARMTH_CHECK_INTERVAL = 40;
	/** Mayalanmış hamur bu kadar unutulursa köpürüp taşar (easter egg). */
	public static final int OVERPROOF_TICKS = 2400;
	private static final int CHEER_BOOST = 200;

	private final NonNullList<ItemStack> items = NonNullList.withSize(1, ItemStack.EMPTY);
	private int progress;
	private boolean warm;
	private int restTicks;
	private boolean overflowed;

	public ProofingTroughBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlockEntities.PROOFING_TROUGH, pos, state);
	}

	public static void serverTick(ServerLevel level, BlockPos pos, BlockState state, ProofingTroughBlockEntity trough) {
		ItemStack content = trough.items.get(0);
		ProofingRecipes.Proofing recipe = ProofingRecipes.get(content);
		int stage = content.isEmpty() ? 0 : recipe != null ? 1 : 2;
		if (state.getValue(ProofingTroughBlock.STAGE) != stage) {
			level.setBlock(pos, state.setValue(ProofingTroughBlock.STAGE, stage), 3);
		}
		if (recipe == null) {
			trough.progress = 0;
			// Ekmek köper: hazır hamur teknede unutulursa köpürüp taşar
			if (!content.isEmpty() && !trough.overflowed && ++trough.restTicks >= OVERPROOF_TICKS) {
				trough.overflowed = true;
				EasterEggs.onOverproof(level, pos, content);
			}
			return;
		}
		if (level.getGameTime() % WARMTH_CHECK_INTERVAL == 0) {
			trough.warm = isWarm(level, pos);
		}
		trough.progress += trough.warm ? 2 : 1;
		if (trough.progress >= recipe.ticks()) {
			trough.progress = 0;
			trough.items.set(0, new ItemStack(recipe.result(), content.getCount()));
			level.playSound(null, pos, SoundEvents.SLIME_BLOCK_PLACE, SoundSource.BLOCKS, 0.6F, 1.4F);
			level.setBlock(pos, state.setValue(ProofingTroughBlock.STAGE, 2), 3);
		}
		trough.setChanged();
	}

	/** Hamur ılık ortamda daha hızlı mayalanır: 2 blok içinde yanan fırın, kamp ateşi, ateş veya lav. */
	private static boolean isWarm(ServerLevel level, BlockPos center) {
		for (BlockPos p : BlockPos.betweenClosed(center.offset(-2, -1, -2), center.offset(2, 1, 2))) {
			BlockState s = level.getBlockState(p);
			if (s.hasProperty(BlockStateProperties.LIT) && s.getValue(BlockStateProperties.LIT) && !s.is(Blocks.REDSTONE_LAMP) && !s.is(Blocks.REDSTONE_ORE) && !s.is(Blocks.DEEPSLATE_REDSTONE_ORE)) {
				return true;
			}
			if (s.is(BlockTags.FIRE) || s.is(Blocks.LAVA) || s.is(Blocks.MAGMA_BLOCK)) {
				return true;
			}
		}
		return false;
	}

	/** "Ekmek köper" sohbet easter egg'i: mayalanan hamura moral verir. */
	public boolean cheer() {
		ProofingRecipes.Proofing recipe = ProofingRecipes.get(this.items.get(0));
		if (recipe == null) {
			return false;
		}
		this.progress = Math.min(recipe.ticks() - 1, this.progress + CHEER_BOOST);
		this.setChanged();
		return true;
	}

	private void resetRest() {
		this.restTicks = 0;
		this.overflowed = false;
	}

	public boolean accepts(ItemStack stack) {
		if (ProofingRecipes.get(stack) == null) {
			return false;
		}
		ItemStack content = this.items.get(0);
		return content.isEmpty() || (ItemStack.isSameItemSameComponents(content, stack) && content.getCount() < CAPACITY);
	}

	public void insert(ItemStack stack, Player player) {
		ItemStack content = this.items.get(0);
		int space = CAPACITY - content.getCount();
		int moved = Math.min(space, stack.getCount());
		if (moved <= 0) {
			return;
		}
		if (content.isEmpty()) {
			this.items.set(0, stack.copyWithCount(moved));
			this.progress = 0;
			this.resetRest();
		} else {
			content.grow(moved);
		}
		if (!player.getAbilities().instabuild) {
			stack.shrink(moved);
		}
		if (this.level != null) {
			this.level.playSound(null, this.worldPosition, SoundEvents.WOOL_PLACE, SoundSource.BLOCKS, 0.8F, 1.0F);
		}
		this.setChanged();
		this.showStatus(player);
	}

	public void takeAll(Player player) {
		ItemStack content = this.items.get(0);
		if (content.isEmpty()) {
			return;
		}
		this.items.set(0, ItemStack.EMPTY);
		this.progress = 0;
		this.resetRest();
		Compat.giveItem(player, content);
		if (this.level != null) {
			this.level.playSound(null, this.worldPosition, SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.4F, 1.0F);
		}
		this.setChanged();
	}

	public void showStatus(Player player) {
		ItemStack content = this.items.get(0);
		Component msg;
		if (content.isEmpty()) {
			msg = Component.translatable("message.ekmekmodu.trough.empty").withStyle(ChatFormatting.GRAY);
		} else {
			ProofingRecipes.Proofing recipe = ProofingRecipes.get(content);
			if (recipe == null) {
				msg = Component.translatable("message.ekmekmodu.trough.ready", content.getHoverName()).withStyle(ChatFormatting.GREEN);
			} else {
				int percent = Math.min(99, this.progress * 100 / recipe.ticks());
				msg = Component.translatable(this.warm ? "message.ekmekmodu.trough.proofing_warm" : "message.ekmekmodu.trough.proofing",
					content.getHoverName(), percent).withStyle(ChatFormatting.GOLD);
			}
		}
		player.sendOverlayMessage(msg);
	}

	// ----------------------------------------------------------- Container
	@Override
	public int getContainerSize() {
		return 1;
	}

	@Override
	public boolean isEmpty() {
		return this.items.get(0).isEmpty();
	}

	@Override
	public ItemStack getItem(int slot) {
		return this.items.get(slot);
	}

	@Override
	public ItemStack removeItem(int slot, int count) {
		ItemStack result = ContainerHelper.removeItem(this.items, slot, count);
		if (!result.isEmpty()) {
			this.setChanged();
		}
		return result;
	}

	@Override
	public ItemStack removeItemNoUpdate(int slot) {
		return ContainerHelper.takeItem(this.items, slot);
	}

	@Override
	public void setItem(int slot, ItemStack itemStack) {
		this.items.set(slot, itemStack);
		this.progress = 0;
		this.resetRest();
		this.setChanged();
	}

	@Override
	public int getMaxStackSize() {
		return CAPACITY;
	}

	@Override
	public boolean canPlaceItem(int slot, ItemStack itemStack) {
		return this.accepts(itemStack);
	}

	@Override
	public boolean stillValid(Player player) {
		return Container.stillValidBlockEntity(this, player);
	}

	@Override
	public void clearContent() {
		this.items.set(0, ItemStack.EMPTY);
	}

	// ------------------------------------------------------------ kayıt
	@Override
	protected void loadAdditional(ValueInput input) {
		super.loadAdditional(input);
		this.items.set(0, ItemStack.EMPTY);
		ContainerHelper.loadAllItems(input, this.items);
		this.progress = input.getIntOr("proofing_progress", 0);
	}

	@Override
	protected void saveAdditional(ValueOutput output) {
		super.saveAdditional(output);
		ContainerHelper.saveAllItems(output, this.items);
		output.putInt("proofing_progress", this.progress);
	}
}
