package eu.gir.basics.blocks;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

public class BlockLightBlocker extends BlockInvisibleLight {

	public BlockLightBlocker() {
		super(0);
	}

	@Override
	public int getOpacity(final BlockState state, final IBlockReader worldIn, final BlockPos pos) {
		return 15;
	}
}
