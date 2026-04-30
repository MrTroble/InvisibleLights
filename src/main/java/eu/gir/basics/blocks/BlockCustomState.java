package eu.gir.basics.blocks;

import java.util.Random;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public class BlockCustomState extends BlockCustomLight {

	public static final BooleanProperty POWERED = BooleanProperty.create("powered");

	public BlockCustomState(final int light) {
		super(light);
		registerDefaultState(this.stateDefinition.any().setValue(POWERED, false));
	}

	@Override
	protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(POWERED);
	}

	private void updatePowerState(final BlockState state, final Level level, final BlockPos pos) {
		if (level.isClientSide)
			return;
		final boolean lastPowered = state.getValue(POWERED);
		if (level.hasNeighborSignal(pos) && !lastPowered) {
			level.setBlock(pos, state.setValue(POWERED, true), 3);
		} else if (!level.hasNeighborSignal(pos) && lastPowered) {
			level.getBlockTicks().scheduleTick(pos, this, 4);
		}
	}

	@Override
	public void onPlace(final BlockState state, final Level level, final BlockPos pos,
			final BlockState oldState, final boolean isMoving) {
		super.onPlace(state, level, pos, oldState, isMoving);
		updatePowerState(state, level, pos);
	}

	@Override
	public void neighborChanged(final BlockState state, final Level level, final BlockPos pos,
			final Block blockIn, final BlockPos fromPos, final boolean isMoving) {
		updatePowerState(state, level, pos);
	}

	@Override
	public void tick(final BlockState state, final ServerLevel level, final BlockPos pos, final Random rand) {
		if (!level.hasNeighborSignal(pos) && state.getValue(POWERED)) {
			level.setBlock(pos, state.setValue(POWERED, false), 3);
		}
	}
}
