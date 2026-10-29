package com.pisanadam.ekmek.event;

import com.pisanadam.ekmek.compat.Compat;
import com.pisanadam.ekmek.registry.ModItems;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

/**
 * Bagel haşlama: mayalanmış bagel hamuru, altında ateş olan (kaynayan) su kazanına
 * sağ tıklanarak haşlanır. Her 4 bagel kazandaki suyu bir seviye azaltır.
 */
public final class BoilingHandler {
	private BoilingHandler() {
	}

	public static void init() {
		UseBlockCallback.EVENT.register((player, level, hand, hitResult) -> {
			ItemStack stack = player.getItemInHand(hand);
			if (!stack.is(ModItems.BAGEL.proofed())) {
				return InteractionResult.PASS;
			}
			BlockPos pos = hitResult.getBlockPos();
			BlockState state = level.getBlockState(pos);
			if (!state.is(Blocks.WATER_CAULDRON)) {
				return InteractionResult.PASS;
			}
			if (level.isClientSide()) {
				return InteractionResult.SUCCESS;
			}
			if (!isHeated(level, pos.below())) {
				player.sendOverlayMessage(Component.translatable("message.ekmekmodu.cauldron.not_boiling").withStyle(ChatFormatting.RED));
				return InteractionResult.FAIL;
			}
			stack.shrink(1);
			Compat.giveItem(player, new ItemStack(ModItems.BAGEL.ovenInput()));
			level.playSound(null, pos, SoundEvents.GENERIC_SPLASH, SoundSource.BLOCKS, 0.5F, 1.2F);
			if (level instanceof ServerLevel serverLevel) {
				serverLevel.sendParticles(ParticleTypes.BUBBLE_POP, pos.getX() + 0.5, pos.getY() + 0.95, pos.getZ() + 0.5, 8, 0.25, 0.05, 0.25, 0.0);
			}
			if (level.getRandom().nextInt(4) == 0) {
				LayeredCauldronBlock.lowerFillLevel(state, level, pos);
			}
			return InteractionResult.SUCCESS;
		});
	}

	private static boolean isHeated(Level level, BlockPos below) {
		BlockState s = level.getBlockState(below);
		if (s.is(BlockTags.FIRE) || s.is(Blocks.LAVA) || s.is(Blocks.MAGMA_BLOCK)) {
			return true;
		}
		return s.is(BlockTags.CAMPFIRES) && s.getValue(BlockStateProperties.LIT);
	}
}
