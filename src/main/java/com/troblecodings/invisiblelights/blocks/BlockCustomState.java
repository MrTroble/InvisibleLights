package com.troblecodings.invisiblelights.blocks;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockCustomState extends BlockCustomLight {

    public static final BooleanProperty POWERED = BooleanProperty.create("powered");

    public BlockCustomState(final int light) {
        super(light);
        setDefaultState(this.stateContainer.getBaseState().with(POWERED, false));
    }

    @Override
    protected void fillStateContainer(final StateContainer.Builder<Block, BlockState> builder) {
        builder.add(POWERED);
    }

    private void updatePowerState(final BlockState state, final World worldIn, final BlockPos pos) {
        if (worldIn.isRemote)
            return;
        final boolean lastPowered = state.get(POWERED);
        if (worldIn.isBlockPowered(pos) && !lastPowered) {
            worldIn.setBlockState(pos, state.with(POWERED, true), 3);
        } else if (!worldIn.isBlockPowered(pos) && lastPowered) {
            worldIn.getPendingBlockTicks().scheduleTick(pos, this, 4);
        }
    }

    @Override
    public void onBlockAdded(final BlockState state, final World worldIn, final BlockPos pos,
            final BlockState oldState, final boolean isMoving) {
        super.onBlockAdded(state, worldIn, pos, oldState, isMoving);
        updatePowerState(state, worldIn, pos);
    }

    @Override
    public void neighborChanged(final BlockState state, final World worldIn, final BlockPos pos,
            final Block blockIn, final BlockPos fromPos, final boolean isMoving) {
        updatePowerState(state, worldIn, pos);
    }

    @Override
    public void tick(final BlockState state, final World worldIn, final BlockPos pos,
            final Random rand) {
        if (worldIn.isRemote)
            return;
        if (!worldIn.isBlockPowered(pos) && state.get(POWERED)) {
            worldIn.setBlockState(pos, state.with(POWERED, false), 3);
        }
    }
}
