package com.troblecodings.invisiblelights;

import java.util.List;

import com.troblecodings.invisiblelights.blocks.BlockCustomLight;
import com.troblecodings.invisiblelights.blocks.BlockCustomState;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

public final class BlockLists {

    private List<String> stateless;
    private List<String> statebased;

    private static Block register(final Block block, final String name) {
        block.setRegistryName(new ResourceLocation(InvisibleLightsMain.MODID, name));
        return block;
    }

    public void addToList(final List<Block> blocks) {
        stateless.forEach(name -> blocks.add(register(new BlockCustomLight(0), name)));
        statebased.forEach(name -> blocks.add(register(new BlockCustomState(0), name)));
    }
}
