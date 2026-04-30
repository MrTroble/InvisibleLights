package eu.gir.basics.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;

public class BlockLightBlocker extends BlockInvisibleLight {

	public BlockLightBlocker() {
		super(0);
	}

	@Override
	public int getLightBlock(final BlockState state, final BlockGetter worldIn, final BlockPos pos) {
		return 15;
	}
}
