package eu.gir.basics;

import java.io.StringReader;
import java.nio.charset.Charset;
import java.util.List;

import org.apache.commons.io.input.ReaderInputStream;

import eu.gir.basics.blocks.BlockCustomLight;
import eu.gir.basics.blocks.BlockCustomState;
import net.minecraft.block.Block;
import net.minecraft.util.text.translation.LanguageMap;

public final class BlockLists {
	
	private List<String> stateless;
	private List<String> statebased;
	
	private Block addLanguage(final Block block, final String name, final StringBuilder builder) {
		block.setUnlocalizedName(name);
		block.setRegistryName(GIRMain.MODID, name);
		builder.append(block.getLocalizedName());
		builder.append("=");
		builder.append(name);
		builder.append(System.lineSeparator());
		return block;
	}
	
	public void addToList(final List<Block> blocks) {
		final StringBuilder builder = new StringBuilder();
		stateless.forEach(name -> blocks.add(addLanguage(new BlockCustomLight(0), name, builder)));
		statebased.forEach(name -> blocks.add(addLanguage(new BlockCustomState(0), name, builder)));
		LanguageMap.inject(new ReaderInputStream(new StringReader(builder.toString()), Charset.defaultCharset()));
	}
}
