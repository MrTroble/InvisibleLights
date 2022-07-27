package eu.gir.basics.blocks;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockCustomState extends BlockCustomLight {
	
	public static final PropertyBool POWERED = PropertyBool.create("powered");
	
	public BlockCustomState(final int light) {
		super(light);
	}
	
	@Override
	public IBlockState getStateFromMeta(final int meta) {
		return this.getDefaultState().withProperty(POWERED, meta == 0 ? false : true);
	}
	
	@Override
	public int getMetaFromState(final IBlockState state) {
		return state.getValue(POWERED) ? 1 : 0;
	}
	
	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, POWERED);
	}
	
	private void updateState(final World worldIn, final BlockPos pos, final IBlockState state) {
		final boolean lastPowered = state.getValue(POWERED);
		if (worldIn.isBlockPowered(pos) && !lastPowered) {
			worldIn.setBlockState(pos, state.withProperty(POWERED, true), 3);
		} else if (lastPowered) {
			worldIn.setBlockState(pos, state.withProperty(POWERED, false), 3);
		}
	}
	
	@Override
	public void onBlockAdded(final World worldIn, final BlockPos pos, final IBlockState state) {
		super.onBlockAdded(worldIn, pos, state);
		updateState(worldIn, pos, state);
	}
	
	@Override
	public void updateTick(final World worldIn, final BlockPos pos, final IBlockState state, final Random rand) {
		super.updateTick(worldIn, pos, state, rand);
		updateState(worldIn, pos, state);
	}
	
	@Override
	public void neighborChanged(final IBlockState state, final World worldIn, final BlockPos pos, final Block blockIn, final BlockPos fromPos) {
		updateState(worldIn, fromPos, state);
	}
}
