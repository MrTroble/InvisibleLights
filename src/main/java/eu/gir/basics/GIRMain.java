package eu.gir.basics;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import eu.gir.basics.init.GIRInit;
import eu.gir.basics.proxy.ClientProxy;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;

@Mod(GIRMain.MODID)
public class GIRMain {

	public static final String MODID = "invisiblelights";
	public static final Logger LOG = LogManager.getLogger(MODID);

	public GIRMain() {
		loadCustomBlocks();

		final IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
		GIRInit.BLOCKS.register(modBus);
		GIRInit.ITEMS.register(modBus);
		DistExecutor.runWhenOn(Dist.CLIENT,
				() -> () -> modBus.addListener(ClientProxy::onClientSetup));

		MinecraftForge.EVENT_BUS.register(GIRInit.class);
		DistExecutor.runWhenOn(Dist.CLIENT,
				() -> () -> MinecraftForge.EVENT_BUS.register(ClientProxy.class));
	}

	private void loadCustomBlocks() {
		final Path path = FMLPaths.CONFIGDIR.get().resolve("gircLightBlocks.json");
		if (Files.notExists(path)) {
			LOG.debug("Did not find {} skipping!", path);
			return;
		}
		final Gson gson = new Gson();
		try (final Reader reader = Files.newBufferedReader(path)) {
			gson.fromJson(reader, BlockLists.class).registerInto();
		} catch (final IOException e) {
			LOG.error("Could not read {}", path, e);
		} catch (final JsonSyntaxException e) {
			LOG.error("Could not parse json! Error {}", e.getMessage());
		}
	}
}
