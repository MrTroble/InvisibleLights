package eu.gir.basics.init;

import java.util.function.Supplier;

import eu.gir.basics.GIRMain;
import eu.gir.basics.blocks.BlockGhostGlowstone;
import eu.gir.basics.blocks.BlockInvisibleLight;
import eu.gir.basics.blocks.BlockLightBlocker;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class GIRInit {

	public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, GIRMain.MODID);
	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, GIRMain.MODID);

	public static final CreativeModeTab LIGHT_TAB = new CreativeModeTab("invisiblelights") {
		@Override
		public ItemStack makeIcon() {
			return new ItemStack(INVISIBLE_LIGHTS_2.get());
		}
	};

	public static final RegistryObject<Block> INVISIBLE_LIGHTS_2 = registerLight("invisiblelights2", 2);
	public static final RegistryObject<Block> INVISIBLE_LIGHTS_3 = registerLight("invisiblelights3", 3);
	public static final RegistryObject<Block> INVISIBLE_LIGHTS_4 = registerLight("invisiblelights4", 4);
	public static final RegistryObject<Block> INVISIBLE_LIGHTS_5 = registerLight("invisiblelights5", 5);
	public static final RegistryObject<Block> INVISIBLE_LIGHTS_6 = registerLight("invisiblelights6", 6);
	public static final RegistryObject<Block> INVISIBLE_LIGHTS_7 = registerLight("invisiblelights7", 7);
	public static final RegistryObject<Block> INVISIBLE_LIGHTS_8 = registerLight("invisiblelights8", 8);
	public static final RegistryObject<Block> INVISIBLE_LIGHTS_9 = registerLight("invisiblelights9", 9);
	public static final RegistryObject<Block> INVISIBLE_LIGHTS_10 = registerLight("invisiblelights10", 10);
	public static final RegistryObject<Block> INVISIBLE_LIGHTS_11 = registerLight("invisiblelights11", 11);
	public static final RegistryObject<Block> INVISIBLE_LIGHTS_12 = registerLight("invisiblelights12", 12);
	public static final RegistryObject<Block> INVISIBLE_LIGHTS_13 = registerLight("invisiblelights13", 13);
	public static final RegistryObject<Block> INVISIBLE_LIGHTS_14 = registerLight("invisiblelights14", 14);
	public static final RegistryObject<Block> INVISIBLE_LIGHTS_15 = registerLight("invisiblelights15", 15);
	public static final RegistryObject<Block> BLOCKER = register("blocker", BlockLightBlocker::new);
	public static final RegistryObject<BlockGhostGlowstone> GHOST_GLOWSTONE = register("ghostglowstone", BlockGhostGlowstone::new);

	private static RegistryObject<Block> registerLight(final String name, final int level) {
		return register(name, () -> new BlockInvisibleLight(level));
	}

	public static <B extends Block> RegistryObject<B> register(final String name, final Supplier<B> blockFactory) {
		final RegistryObject<B> block = BLOCKS.register(name, blockFactory);
		ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties().tab(LIGHT_TAB)));
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
