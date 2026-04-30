package eu.gir.basics;

import java.util.List;

import eu.gir.basics.blocks.BlockCustomLight;
import eu.gir.basics.blocks.BlockCustomState;
import eu.gir.basics.init.GIRInit;

public final class BlockLists {

	private List<String> stateless;
	private List<String> statebased;

	public void registerInto() {
		stateless.forEach(name -> GIRInit.register(name, () -> new BlockCustomLight(0)));
		statebased.forEach(name -> GIRInit.register(name, () -> new BlockCustomState(0)));
	}
}
