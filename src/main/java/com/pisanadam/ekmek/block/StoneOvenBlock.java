package com.pisanadam.ekmek.block;

import com.pisanadam.ekmek.block.entity.StoneOvenBlockEntity;
import com.pisanadam.ekmek.compat.CompatFurnaceBlock;
import com.pisanadam.ekmek.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

/**
 * Taş fırın: tüm ekmekler yalnızca burada pişer. Vanilla fırın gibi yakıtla çalışır,
 * yandığında ışık verir, bacasından duman çıkar.
 */
public class StoneOvenBlock extends CompatFurnaceBlock {
	public StoneOvenBlock(BlockBehaviour.Properties properties) {
		super(properties, StoneOvenBlock::new);
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new StoneOvenBlockEntity(pos, state);
	}

	@Override
	public <T extends BlockEntity> @Nullable BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
		if (level instanceof ServerLevel serverLevel) {
			return createTickerHelper(type, ModBlockEntities.STONE_OVEN,
				(lvl, pos, st, oven) -> StoneOvenBlockEntity.serverTick(serverLevel, pos, st, oven));
		}
		return null;
	}

	@Override
	protected void openContainer(Level level, BlockPos pos, Player player) {
		if (level.getBlockEntity(pos) instanceof StoneOvenBlockEntity oven) {
			player.openMenu((MenuProvider) oven);
		}
	}

	@Override
	public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
		if (!state.getValue(LIT)) {
			return;
		}
		double x = pos.getX() + 0.5;
		double y = pos.getY();
		double z = pos.getZ() + 0.5;
		if (random.nextDouble() < 0.1) {
			level.playLocalSound(x, y, z, SoundEvents.FURNACE_FIRE_CRACKLE, SoundSource.BLOCKS, 1.0F, 0.9F, false);
		}
		// bacadan duman
		level.addParticle(ParticleTypes.SMOKE, x + (random.nextDouble() - 0.5) * 0.2, y + 1.05, z + (random.nextDouble() - 0.5) * 0.2, 0.0, 0.04, 0.0);
		// ağızdan alev
		Direction facing = state.getValue(FACING);
		Direction.Axis axis = facing.getAxis();
		double side = random.nextDouble() * 0.5 - 0.25;
		double dx = axis == Direction.Axis.X ? facing.getStepX() * 0.52 : side;
		double dz = axis == Direction.Axis.Z ? facing.getStepZ() * 0.52 : side;
		double dy = random.nextDouble() * 4.0 / 16.0;
		level.addParticle(ParticleTypes.FLAME, x + dx, y + dy, z + dz, 0.0, 0.0, 0.0);
	}
}
