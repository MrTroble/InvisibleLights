package com.troblecodings.invisiblelights.blocks;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;

public class BlockGhostGlowstone extends Block {

    public BlockGhostGlowstone(final Block.Properties props) {
        super(props.sound(SoundType.GLASS).strength(0.3f).noOcclusion());
    }
}
