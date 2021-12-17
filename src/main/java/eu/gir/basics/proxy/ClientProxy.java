package eu.gir.basics.proxy;

import eu.gir.basics.blocks.BlockInvisibleLight;
import eu.gir.basics.init.GIRInit;
import eu.gir.basics.init.GIRModel;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ClientProxy extends CommonProxy {

	@Override
	public void preinit(FMLPreInitializationEvent event) {
		super.preinit(event);
		MinecraftForge.EVENT_BUS.register(ClientProxy.class);
		ModelLoaderRegistry.registerLoader(new GIRModel());
	}

	@SubscribeEvent
	public static void modelEvents(final ModelRegistryEvent event) {
		GIRInit.itemsToRegister.forEach(item -> ModelLoader.setCustomModelResourceLocation(item, 0,
				new ModelResourceLocation(item.getRegistryName(), "inventory")));
		GIRInit.blocksToRegister.forEach(block -> {
			final Item item = Item.getItemFromBlock(block);
			ModelLoader.setCustomModelResourceLocation(item, 0,
					new ModelResourceLocation(item.getRegistryName(), "inventory"));
		});

	}

	private static final int RADIUS = 50;
	private static double d1;
	private static double d2;
	private static double d3;

	public static void render(final BlockPos pos1) {
		RenderGlobal.drawSelectionBoundingBox(Block.FULL_BLOCK_AABB.offset(((double) pos1.getX()) - d1,
				((double) pos1.getY()) - d2, ((double) pos1.getZ()) - d3), 0, 1, 0, 1);
	}

	@SubscribeEvent
	public static void modelEvents(final RenderWorldLastEvent event) {
		final EntityPlayerSP sp = Minecraft.getMinecraft().player;
		final Block block = Block.getBlockFromItem(sp.getHeldItemMainhand().getItem());
		if (block instanceof BlockInvisibleLight) {
			final double part = event.getPartialTicks();
			d1 = sp.lastTickPosX + (sp.posX - sp.lastTickPosX) * part;
			d2 = sp.lastTickPosY + (sp.posY - sp.lastTickPosY) * part;
			d3 = sp.lastTickPosZ + (sp.posZ - sp.lastTickPosZ) * part;

			final BlockPos pos = sp.getPosition();
			GlStateManager.disableTexture2D();
			for (int x = -RADIUS; x <= RADIUS; x++) {
				for (int y = -RADIUS; y <= RADIUS; y++) {
					for (int z = -RADIUS; z <= RADIUS; z++) {
						final BlockPos nPos = pos.add(x, y, z);
						final Block pBlock = sp.world.getBlockState(nPos).getBlock();
						if(pBlock instanceof BlockInvisibleLight) {
							render(nPos);
						}
					}
				}
			}
			GlStateManager.enableTexture2D();
		}
	}

	@Override
	public void init(FMLInitializationEvent event) {
		super.init(event);
	}

	@Override
	public void postinit(FMLPostInitializationEvent event) {
		super.postinit(event);
	}

}
