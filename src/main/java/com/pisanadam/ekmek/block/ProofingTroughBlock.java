package com.pisanadam.ekmek.block;

import com.pisanadam.ekmek.block.entity.ProofingTroughBlockEntity;
import com.pisanadam.ekmek.compat.CompatEntityBlock;
import com.pisanadam.ekmek.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;

/**
 * Mayalama teknesi (hamur teknesi): yoğrulan hamur, üstü bezle örtülerek burada mayalanır.
 * Sağ tık: hamur koy / mayalanmış hamuru al. Eğilip sağ tık: durumu gör.
 * Yakında yanan bir fırın, kamp ateşi vb. varsa mayalanma iki kat hızlanır.
 */
public class ProofingTroughBlock extends CompatEntityBlock {
	/** 0 = boş, 1 = mayalanıyor, 2 = hazır */
	public static final IntegerProperty STAGE = IntegerProperty.create("stage", 0, 2);
	private static final VoxelShape SHAPE = Block.box(1.0, 0.0, 1.0, 15.0, 8.0, 15.0);

	public ProofingTroughBlock(BlockBehaviour.Properties properties) {
		super(properties, ProofingTroughBlock::new);
		this.registerDefaultState(this.stateDefinition.any().setValue(STAGE, 0));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(STAGE);
	}

	@Override
	protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return SHAPE;
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new ProofingTroughBlockEntity(pos, state);
	}

	@Override
	public <T extends BlockEntity> @Nullable BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
		if (level instanceof ServerLevel serverLevel) {
			return createTickerHelper(type, ModBlockEntities.PROOFING_TROUGH,
				(lvl, pos, st, trough) -> ProofingTroughBlockEntity.serverTick(serverLevel, pos, st, trough));
		}
		return null;
	}

	@Override
	protected InteractionResult useItemOn(ItemStack itemStack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
		if (!(level.getBlockEntity(pos) instanceof ProofingTroughBlockEntity trough) || !trough.accepts(itemStack)) {
			return InteractionResult.TRY_WITH_EMPTY_HAND;
		}
		if (!level.isClientSide()) {
			trough.insert(itemStack, player);
		}
		return InteractionResult.SUCCESS;
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
		if (!(level.getBlockEntity(pos) instanceof ProofingTroughBlockEntity trough)) {
			return InteractionResult.PASS;
		}
		if (!level.isClientSide()) {
			if (player.isSecondaryUseActive() || trough.isEmpty()) {
				trough.showStatus(player);
			} else {
				trough.takeAll(player);
			}
		}
		return InteractionResult.SUCCESS;
	}

	@Override
	protected boolean hasAnalogOutputSignal(BlockState state) {
		return true;
	}

	@Override
	protected int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos, net.minecraft.core.Direction direction) {
		return state.getValue(STAGE) * 7;
	}
}
