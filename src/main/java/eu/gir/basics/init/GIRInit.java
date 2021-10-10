package eu.gir.basics.init;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;

import eu.gir.basics.GIRMain;
import eu.gir.basics.blocks.BlockInvisibleLight;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;

public class GIRInit {

	public static final CreativeTabs LIGHT_TAB = new CreativeTabs("invisiblelights") {
		@Override
		public ItemStack getTabIconItem() {
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

	public static final ArrayList<Block> blocksToRegister = new ArrayList<>();
	public static final ArrayList<Item> itemsToRegister = new ArrayList<>();

	public static void init() {
		final Field[] fields = GIRInit.class.getFields();
		for (Field field : fields) {
			final int modifiers = field.getModifiers();
			if (Modifier.isStatic(modifiers) && Modifier.isFinal(modifiers) && Modifier.isPublic(modifiers)) {
				final String name = field.getName().toLowerCase().replace("_", "");
				try {
					final Object obj = field.get(null);
					if (obj instanceof Block) {
						final Block block = (Block) obj;
						block.setRegistryName(new ResourceLocation(GIRMain.MODID, name));
						block.setUnlocalizedName(name);
						blocksToRegister.add(block);
						if (block instanceof ITileEntityProvider) {
							ITileEntityProvider provider = (ITileEntityProvider) block;
							try {
								Class<? extends TileEntity> tileclass = provider.createNewTileEntity(null, 0)
										.getClass();
								TileEntity.register(tileclass.getSimpleName().toLowerCase(), tileclass);
							} catch (NullPointerException ex) {
								GIRMain.LOG.trace(
										"All tileentity provide need to call back a default entity if the world is null!",
										ex);
							}
						}
					} 
					if(obj instanceof Item) {
						final Item item = (Item) obj;
						item.setRegistryName(new ResourceLocation(GIRMain.MODID, name));
						item.setUnlocalizedName(name);
						itemsToRegister.add(item);
					}
				} catch (IllegalArgumentException | IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@SubscribeEvent
	public static void registerBlock(RegistryEvent.Register<Block> event) {
		IForgeRegistry<Block> registry = event.getRegistry();
		blocksToRegister.forEach(registry::register);
	}

	@SubscribeEvent
	public static void registerItem(RegistryEvent.Register<Item> event) {
		IForgeRegistry<Item> registry = event.getRegistry();
		blocksToRegister
				.forEach(block -> registry.register(new ItemBlock(block).setRegistryName(block.getRegistryName())));
		itemsToRegister.forEach(registry::register);
	}

}
