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

import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;

@Mod(InvisibleLightsMain.MODID)
public class InvisibleLightsMain {

    public static final String MODID = "invisiblelights";
    public static final Logger LOG = LogManager.getLogger(MODID);

    public InvisibleLightsMain() {
        ILInit.init();
        loadCustomBlocks();

        final FMLJavaModLoadingContext ctx = FMLJavaModLoadingContext.get();
        ctx.getModEventBus().addGenericListener(Block.class, ILInit::registerBlocks);
        ctx.getModEventBus().addGenericListener(Item.class, ILInit::registerItems);
        DistExecutor.runWhenOn(Dist.CLIENT,
                () -> () -> ctx.getModEventBus().addListener(ClientProxy::onClientSetup));

        MinecraftForge.EVENT_BUS.register(ILInit.class);
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
            gson.fromJson(reader, BlockLists.class).addToList(ILInit.BLOCKS_TO_REGISTER);
        } catch (final IOException e) {
            LOG.error("Could not read {}", path, e);
        } catch (final JsonSyntaxException e) {
            LOG.error("Could not parse json! Error {}", e.getMessage());
        }
    }
}
