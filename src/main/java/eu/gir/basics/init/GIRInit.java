package eu.gir.basics.init;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;

import eu.gir.basics.GIRMain;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;

public class GIRInit {

	private static final ArrayList<Block> blocksToRegister = new ArrayList<>();
	private static final ArrayList<Item> itemsToRegister = new ArrayList<>();

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
