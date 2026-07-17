package com.troblecodings.invisiblelights.blocks;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

public class BlockLightBlocker extends BlockInvisibleLight {

	public BlockLightBlocker() {
		super(0);
	}

	@Override
	public int getOpacity(final IBlockState state, final IBlockReader worldIn, final BlockPos pos) {
		return 15;
	}
}
