package com.troblecodings.invisiblelights.init;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;

import com.troblecodings.invisiblelights.InvisibleLightsMain;
import com.troblecodings.invisiblelights.blocks.BlockGhostGlowstone;
import com.troblecodings.invisiblelights.blocks.BlockInvisibleLight;
import com.troblecodings.invisiblelights.blocks.BlockLightBlocker;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;

public final class ILInit {

    public static final CreativeModeTab LIGHT_TAB = new CreativeModeTab("invisiblelights") {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(INVISIBLE_LIGHTS_2);
        }
    };

    public static final Block INVISIBLE_LIGHTS_2 = new BlockInvisibleLight(2);
    public static final Block INVISIBLE_LIGHTS_3 = new BlockInvisibleLight(3);
    public static final Block INVISIBLE_LIGHTS_4 = new BlockInvisibleLight(4);
    public static final Block INVISIBLE_LIGHTS_5 = new BlockInvisibleLight(5);
    public static final Block INVISIBLE_LIGHTS_6 = new BlockInvisibleLight(6);
    public static final Block INVISIBLE_LIGHTS_7 = new BlockInvisibleLight(7);
    public static final Block INVISIBLE_LIGHTS_8 = new BlockInvisibleLight(8);
    public static final Block INVISIBLE_LIGHTS_9 = new BlockInvisibleLight(9);
    public static final Block INVISIBLE_LIGHTS_10 = new BlockInvisibleLight(10);
    public static final Block INVISIBLE_LIGHTS_11 = new BlockInvisibleLight(11);
    public static final Block INVISIBLE_LIGHTS_12 = new BlockInvisibleLight(12);
    public static final Block INVISIBLE_LIGHTS_13 = new BlockInvisibleLight(13);
    public static final Block INVISIBLE_LIGHTS_14 = new BlockInvisibleLight(14);
    public static final Block INVISIBLE_LIGHTS_15 = new BlockInvisibleLight(15);
    public static final Block BLOCKER = new BlockLightBlocker();
    public static final BlockGhostGlowstone GHOST_GLOWSTONE = new BlockGhostGlowstone();

    public static final ArrayList<Block> BLOCKS_TO_REGISTER = new ArrayList<>();

    private ILInit() {
    }

    public static void init() {
        for (final Field field : ILInit.class.getFields()) {
            final int modifiers = field.getModifiers();
            if (!Modifier.isStatic(modifiers) || !Modifier.isFinal(modifiers)
                    || !Modifier.isPublic(modifiers)) {
                continue;
            }
            try {
                final Object obj = field.get(null);
                if (!(obj instanceof Block)) {
                    continue;
                }
                final Block block = (Block) obj;
                final String name = field.getName().toLowerCase().replace("_", "");
                block.setRegistryName(new ResourceLocation(InvisibleLightsMain.MODID, name));
                BLOCKS_TO_REGISTER.add(block);
            } catch (final IllegalArgumentException | IllegalAccessException e) {
                InvisibleLightsMain.LOG.error("Could not access field {}", field.getName(), e);
            }
        }
    }

    public static void registerBlocks(final RegistryEvent.Register<Block> event) {
        final IForgeRegistry<Block> registry = event.getRegistry();
        BLOCKS_TO_REGISTER.forEach(registry::register);
    }

    public static void registerItems(final RegistryEvent.Register<Item> event) {
        final IForgeRegistry<Item> registry = event.getRegistry();
        BLOCKS_TO_REGISTER.forEach(block -> {
            final BlockItem item = new BlockItem(block, new Item.Properties().tab(LIGHT_TAB));
            item.setRegistryName(block.getRegistryName());
            registry.register(item);
        });
    }

    @SubscribeEvent
    public static void blockBreakEven(final BreakEvent event) {
        if (!(event.getState().getBlock() instanceof BlockInvisibleLight))
            return;
        final Player player = event.getPlayer();
        final Item item = player.getMainHandItem().getItem();
        if (!(Block.byItem(item) instanceof BlockInvisibleLight)) {
            event.setCanceled(true);
        }
    }
}
