package eu.gir.basics.proxy;

import eu.gir.basics.init.GIRInit;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.FMLEventChannel;

public class CommonProxy {

	public FMLEventChannel CHANNEL; 
	
	public void preinit(FMLPreInitializationEvent event) {
		GIRInit.init();
		MinecraftForge.EVENT_BUS.register(GIRInit.class);
	}

	public void init(FMLInitializationEvent event) {

	}

	public void postinit(FMLPostInitializationEvent event) {

	}

}
