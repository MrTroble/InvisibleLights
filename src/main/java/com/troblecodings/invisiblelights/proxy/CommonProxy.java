package com.troblecodings.invisiblelights.proxy;

import com.troblecodings.invisiblelights.blocks.BlockGhostGlowstone;
import com.troblecodings.invisiblelights.init.ILInit;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class CommonProxy {

    public void preinit(final FMLPreInitializationEvent event) {
        ILInit.init();
        MinecraftForge.EVENT_BUS.register(ILInit.class);
    }

    public void init(final FMLInitializationEvent event) {
        BlockGhostGlowstone.init();
    }

    public void postinit(final FMLPostInitializationEvent event) {

    }

}
