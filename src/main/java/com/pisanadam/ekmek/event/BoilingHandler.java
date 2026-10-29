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
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

/**
 * Kaynayan kazan: altında ateş, kamp ateşi, lav veya magma olan kazan.
 * <ul>
 *   <li>Mayalanmış bagel hamuru + su dolu kazan → Haşlanmış Bagel (her 4 bagelde su bir seviye azalır)</li>
 *   <li>Meyve Şırası + kazan (dolu ya da boş) → Pekmez (şıra kaynatılıp koyulaştırılır)</li>
 *   <li>Boş el + su dolu kazan → Tuz (su buharlaşır, her seferinde bir seviye azalır)</li>
 * </ul>
 */
public final class BoilingHandler {
	private BoilingHandler() {
	}

	public static void init() {
		UseBlockCallback.EVENT.register((player, level, hand, hitResult) -> {
			BlockPos pos = hitResult.getBlockPos();
			BlockState state = level.getBlockState(pos);
			boolean water = state.is(Blocks.WATER_CAULDRON);
			if (!water && !state.is(Blocks.CAULDRON)) {
				return InteractionResult.PASS;
			}
			ItemStack stack = player.getItemInHand(hand);
			if (stack.is(ModItems.BAGEL.proofed()) && water) {
				return boil(player, level, pos, state, stack, ModItems.BAGEL.ovenInput(), level.getRandom().nextInt(4) == 0);
			}
			if (stack.is(ModItems.BERRY_MUST)) {
				return boil(player, level, pos, state, stack, ModItems.PEKMEZ, false);
			}
			if (stack.isEmpty() && hand == InteractionHand.MAIN_HAND && water && isHeated(level, pos.below())) {
				return evaporate(player, level, pos, state);
			}
			return InteractionResult.PASS;
		});
	}

	private static InteractionResult boil(Player player, Level level, BlockPos pos, BlockState state, ItemStack stack, Item result, boolean useWater) {
		if (level.isClientSide()) {
			return InteractionResult.SUCCESS;
		}
		if (!isHeated(level, pos.below())) {
			player.sendOverlayMessage(Component.translatable("message.ekmekmodu.cauldron.not_boiling").withStyle(ChatFormatting.RED));
			return InteractionResult.FAIL;
		}
		stack.shrink(1);
		Compat.giveItem(player, new ItemStack(result));
		level.playSound(null, pos, SoundEvents.GENERIC_SPLASH, SoundSource.BLOCKS, 0.5F, 1.2F);
		bubbles(level, pos, 8);
		if (useWater) {
			LayeredCauldronBlock.lowerFillLevel(state, level, pos);
		}
		return InteractionResult.SUCCESS;
	}

	/** Suyu kaynatıp buharlaştırınca kazanın dibinde tuz kalır. */
	private static InteractionResult evaporate(Player player, Level level, BlockPos pos, BlockState state) {
		if (level.isClientSide()) {
			return InteractionResult.SUCCESS;
		}
		LayeredCauldronBlock.lowerFillLevel(state, level, pos);
		Compat.giveItem(player, new ItemStack(ModItems.SALT));
		level.playSound(null, pos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.4F, 1.6F);
		if (level instanceof ServerLevel serverLevel) {
			serverLevel.sendParticles(ParticleTypes.CLOUD, pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5, 6, 0.25, 0.1, 0.25, 0.02);
		}
		player.sendOverlayMessage(Component.translatable("message.ekmekmodu.cauldron.salt").withStyle(ChatFormatting.AQUA));
		return InteractionResult.SUCCESS;
	}

	private static void bubbles(Level level, BlockPos pos, int count) {
		if (level instanceof ServerLevel serverLevel) {
			serverLevel.sendParticles(ParticleTypes.BUBBLE_POP, pos.getX() + 0.5, pos.getY() + 0.95, pos.getZ() + 0.5, count, 0.25, 0.05, 0.25, 0.0);
		}
	}

	private static boolean isHeated(Level level, BlockPos below) {
		BlockState s = level.getBlockState(below);
		if (s.is(BlockTags.FIRE) || s.is(Blocks.LAVA) || s.is(Blocks.MAGMA_BLOCK)) {
			return true;
		}
		return s.is(BlockTags.CAMPFIRES) && s.getValue(BlockStateProperties.LIT);
	}
}
