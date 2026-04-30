package eu.gir.basics.blocks;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;

public class BlockGhostGlowstone extends Block {

	public BlockGhostGlowstone() {
		super(Block.Properties.of()
				.sound(SoundType.GLASS)
				.strength(0.3f)
				.noOcclusion());
	}
}
