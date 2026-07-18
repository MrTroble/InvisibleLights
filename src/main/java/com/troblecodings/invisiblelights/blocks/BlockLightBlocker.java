package com.troblecodings.invisiblelights.blocks;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class BlockLightBlocker extends BlockInvisibleLight {

    public BlockLightBlocker(final Block.Properties props) {
        super(props, 0);
    }

    @Override
    protected int getLightBlock(final BlockState state) {
        return 15;
    }
}
