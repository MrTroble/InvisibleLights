package eu.gir.basics;

import java.util.List;

import eu.gir.basics.blocks.BlockCustomLight;
import eu.gir.basics.blocks.BlockCustomState;
import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;

public final class BlockLists {

	private List<String> stateless;
	private List<String> statebased;

	private static Block register(final Block block, final String name) {
		block.setRegistryName(new ResourceLocation(GIRMain.MODID, name));
		return block;
	}

	public void addToList(final List<Block> blocks) {
		stateless.forEach(name -> blocks.add(register(new BlockCustomLight(0), name)));
		statebased.forEach(name -> blocks.add(register(new BlockCustomState(0), name)));
	}
}
