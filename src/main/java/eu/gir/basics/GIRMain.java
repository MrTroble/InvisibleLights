package eu.gir.basics;

import org.apache.logging.log4j.Logger;

import eu.gir.basics.proxy.CommonProxy;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = GIRMain.MODID, acceptedMinecraftVersions = "[1.12.2]")
public class GIRMain {

	@Instance
	private static GIRMain instance;
	public static final String MODID = "test";

	public static GIRMain getInstance() {
		return instance;
	}

	@SidedProxy(serverSide = "eu.gir.basics.proxy.CommonProxy", clientSide = "eu.gir.basics.proxy.ClientProxy")
	public static CommonProxy PROXY;
	public static Logger LOG;

	@EventHandler
	public void preinit(FMLPreInitializationEvent event) {
		LOG = event.getModLog();
		PROXY.preinit(event);
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		PROXY.init(event);
	}

	@EventHandler
	public void postinit(FMLPostInitializationEvent event) {
		PROXY.postinit(event);
	}	
}
