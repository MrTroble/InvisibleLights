package com.troblecodings.invisiblelights.blocks;

import com.troblecodings.invisiblelights.init.ILInit;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public class BlockLightBlocker extends BlockInvisibleLight {
	
	public BlockLightBlocker() {
		super(0);
		this.setCreativeTab(ILInit.LIGHT_TAB);
		this.disableStats();
	}
	
	@Override
	public boolean isFullBlock(final IBlockState state) {
		return false;
	}
	
	@Override
	public boolean isOpaqueCube(final IBlockState state) {
		return true;
	}
	
	@Override
	public boolean doesSideBlockRendering(final IBlockState state, final IBlockAccess world, final BlockPos pos, final EnumFacing face) {
		return false;
	}
}
