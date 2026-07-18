package com.troblecodings.invisiblelights;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.troblecodings.invisiblelights.init.ILInit;
import com.troblecodings.invisiblelights.proxy.ClientProxy;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.common.NeoForge;

@Mod(InvisibleLightsMain.MODID)
public class InvisibleLightsMain {

	public static final String MODID = "invisiblelights";
	public static final Logger LOG = LogManager.getLogger(MODID);

	public InvisibleLightsMain(final IEventBus modBus, final ModContainer container, final Dist dist) {
		loadCustomBlocks();

		ILInit.BLOCKS.register(modBus);
		ILInit.ITEMS.register(modBus);
		ILInit.CREATIVE_MODE_TABS.register(modBus);

		NeoForge.EVENT_BUS.register(ILInit.class);

		if (dist.isClient()) {
			modBus.addListener(ClientProxy::onClientSetup);
			NeoForge.EVENT_BUS.register(ClientProxy.class);
		}
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
