package com.troblecodings.invisiblelights.init;

import java.util.function.Supplier;

import com.troblecodings.invisiblelights.InvisibleLightsMain;
import com.troblecodings.invisiblelights.blocks.BlockGhostGlowstone;
import com.troblecodings.invisiblelights.blocks.BlockInvisibleLight;
import com.troblecodings.invisiblelights.blocks.BlockLightBlocker;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.level.BlockEvent.BreakEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ILInit {

    public static final DeferredRegister.Blocks BLOCKS =
            DeferredRegister.createBlocks(InvisibleLightsMain.MODID);
    public static final DeferredRegister.Items ITEMS =
            DeferredRegister.createItems(InvisibleLightsMain.MODID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, InvisibleLightsMain.MODID);

    public static final DeferredHolder<Block, Block> INVISIBLE_LIGHTS_2 =
            registerLight("invisiblelights2", 2);
    public static final DeferredHolder<Block, Block> INVISIBLE_LIGHTS_3 =
            registerLight("invisiblelights3", 3);
    public static final DeferredHolder<Block, Block> INVISIBLE_LIGHTS_4 =
            registerLight("invisiblelights4", 4);
    public static final DeferredHolder<Block, Block> INVISIBLE_LIGHTS_5 =
            registerLight("invisiblelights5", 5);
    public static final DeferredHolder<Block, Block> INVISIBLE_LIGHTS_6 =
            registerLight("invisiblelights6", 6);
    public static final DeferredHolder<Block, Block> INVISIBLE_LIGHTS_7 =
            registerLight("invisiblelights7", 7);
    public static final DeferredHolder<Block, Block> INVISIBLE_LIGHTS_8 =
            registerLight("invisiblelights8", 8);
    public static final DeferredHolder<Block, Block> INVISIBLE_LIGHTS_9 =
            registerLight("invisiblelights9", 9);
    public static final DeferredHolder<Block, Block> INVISIBLE_LIGHTS_10 =
            registerLight("invisiblelights10", 10);
    public static final DeferredHolder<Block, Block> INVISIBLE_LIGHTS_11 =
            registerLight("invisiblelights11", 11);
    public static final DeferredHolder<Block, Block> INVISIBLE_LIGHTS_12 =
            registerLight("invisiblelights12", 12);
    public static final DeferredHolder<Block, Block> INVISIBLE_LIGHTS_13 =
            registerLight("invisiblelights13", 13);
    public static final DeferredHolder<Block, Block> INVISIBLE_LIGHTS_14 =
            registerLight("invisiblelights14", 14);
    public static final DeferredHolder<Block, Block> INVISIBLE_LIGHTS_15 =
            registerLight("invisiblelights15", 15);
    public static final DeferredHolder<Block, Block> BLOCKER =
            register("blocker", BlockLightBlocker::new);
    public static final DeferredHolder<Block, BlockGhostGlowstone> GHOST_GLOWSTONE =
            register("ghostglowstone", BlockGhostGlowstone::new);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> INVISIBLE_LIGHTS_TAB =
            CREATIVE_MODE_TABS.register("invisiblelights",
                    () -> CreativeModeTab.builder()
                            .title(Component.translatable("itemGroup.invisiblelights"))
                            .icon(() -> new ItemStack(INVISIBLE_LIGHTS_2.get()))
                            .displayItems((params, output) -> ITEMS.getEntries()
                                    .forEach(item -> output.accept(item.get())))
                            .build());

    private ILInit() {
    }

    private static DeferredHolder<Block, Block> registerLight(final String name, final int level) {
        return register(name, () -> new BlockInvisibleLight(level));
    }

    public static <B extends Block> DeferredHolder<Block, B> register(final String name,
            final Supplier<B> blockFactory) {
        final DeferredHolder<Block, B> block = BLOCKS.register(name, blockFactory);
        ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
        return block;
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
