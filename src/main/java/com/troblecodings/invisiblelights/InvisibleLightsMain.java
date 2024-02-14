package com.troblecodings.invisiblelights;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.troblecodings.invisiblelights.init.ILInit;
import com.troblecodings.invisiblelights.proxy.CommonProxy;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = InvisibleLightsMain.MODID, acceptedMinecraftVersions = "[1.12.2]")
public class InvisibleLightsMain {

    @Instance
    private static InvisibleLightsMain instance;
    public static final String MODID = "invisiblelights";

    public static InvisibleLightsMain getInstance() {
        return instance;
    }

    @SidedProxy(serverSide = "com.troblecodings.invisiblelights.proxy.CommonProxy",
            clientSide = "com.troblecodings.invisiblelights.proxy.ClientProxy")
    public static CommonProxy PROXY;
    public static Logger LOG;

    @EventHandler
    public void preinit(final FMLPreInitializationEvent event) {
        LOG = event.getModLog();
        PROXY.preinit(event);
        final Path path = event.getModConfigurationDirectory().toPath()
                .resolve("gircLightBlocks.json");
        if (Files.notExists(path)) {
            LOG.debug("Did not find {} skipping!", path.toString());
            return;
        }
        final Gson gson = new Gson();
        try (final Reader reader = Files.newBufferedReader(path)) {
            gson.fromJson(reader, BlockLists.class).addToList(ILInit.blocksToRegister);
        } catch (final IOException e) {
            e.printStackTrace();
        } catch (final JsonSyntaxException e) {
            LOG.error("Could not parse json! Error {}", e.getMessage());
        }
    }

    @EventHandler
    public void init(final FMLInitializationEvent event) {
        PROXY.init(event);
    }

    @EventHandler
    public void postinit(final FMLPostInitializationEvent event) {
        PROXY.postinit(event);
    }
}
