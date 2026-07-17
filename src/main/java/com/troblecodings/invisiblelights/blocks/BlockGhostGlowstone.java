package com.troblecodings.invisiblelights.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;

public class BlockGhostGlowstone extends Block {

	public BlockGhostGlowstone() {
		super(Block.Properties.create(Material.GLASS)
				.sound(SoundType.GLASS)
				.hardnessAndResistance(0.3f)
				.notSolid());
	}
}
