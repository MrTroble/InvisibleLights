package eu.gir.basics.proxy;

import java.util.ArrayList;

import eu.gir.basics.blocks.BlockInvisibleLight;
import eu.gir.basics.init.GIRInit;
import eu.gir.basics.init.GIRModel;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.event.world.BlockEvent.EntityPlaceEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ClientProxy extends CommonProxy {
	
	@Override
	public void preinit(final FMLPreInitializationEvent event) {
		super.preinit(event);
		MinecraftForge.EVENT_BUS.register(ClientProxy.class);
		ModelLoaderRegistry.registerLoader(new GIRModel());
	}
	
	@SubscribeEvent
	public static void modelEvents(final ModelRegistryEvent event) {
		GIRInit.itemsToRegister.forEach(item -> ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(item.getRegistryName(), "inventory")));
		GIRInit.blocksToRegister.forEach(block -> {
			final Item item = Item.getItemFromBlock(block);
			ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(item.getRegistryName(), "inventory"));
		});
		
	}
	
	private static final int RADIUS = 50;
	private static final int UPDATE_SPHERE = 50;
	private static final int RADIUSPLAYER = RADIUS * RADIUS + 10;
	private static double d1;
	private static double d2;
	private static double d3;
	
	public static void render(final BlockPos pos1) {
		RenderGlobal.drawSelectionBoundingBox(Block.FULL_BLOCK_AABB.offset((pos1.getX()) - d1, (pos1.getY()) - d2, (pos1.getZ()) - d3), 0, 1, 0, 1);
	}
	
	private static ArrayList<BlockPos> playerPlacedBlocks = new ArrayList<>();
	private static boolean dirty = true;
	private static BlockPos lastPosition = BlockPos.ORIGIN;
	
	public static void refill(final BlockPos pos, final World world) {
		lastPosition = pos;
		dirty = false;
		new Thread(() -> {
			for (int x = -RADIUS; x <= RADIUS; x++) {
				for (int y = -RADIUS; y <= RADIUS; y++) {
					for (int z = -RADIUS; z <= RADIUS; z++) {
						final BlockPos nPos = pos.add(x, y, z);
						final Block pBlock = world.getBlockState(nPos).getBlock();
						if (pBlock instanceof BlockInvisibleLight) {
							synchronized (playerPlacedBlocks) {
								playerPlacedBlocks.add(nPos);
							}
						}
					}
				}
			}
		}).start();
	}
	
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public static void renderOverlayEvent(final DrawBlockHighlightEvent render) {
		final EntityPlayer player = render.getPlayer();
		if (player == null)
			return;
		final World world = player.getEntityWorld();
		if (world == null)
			return;
		final RayTraceResult result = render.getTarget();
		if (result == null)
			return;
		final BlockPos pos = result.getBlockPos();
		if (pos == null)
			return;
		final IBlockState state = world.getBlockState(pos);
		if (state.getBlock() instanceof BlockInvisibleLight)
			render.setCanceled(true);
	}
	
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public static void blockPlaceEvent(final EntityPlaceEvent event) {
		final Entity placerEntity = event.getEntity();
		if (placerEntity == null)
			return;
		final EntityPlayerSP player = Minecraft.getMinecraft().player;
		if (player == null)
			return;
		final BlockPos playerPos = player.getPosition();
		final double distance = placerEntity.getPosition().distanceSq(playerPos);
		if (distance < RADIUSPLAYER) {
			if (event.getPlacedBlock().getBlock() instanceof BlockInvisibleLight) {
				playerPlacedBlocks.clear();
				refill(playerPos, player.getEntityWorld());
			}
		}
	}
	
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public static void blockPlaceEvent(final BreakEvent event) {
		synchronized (playerPlacedBlocks) {
			if (playerPlacedBlocks.contains(event.getPos()))
				playerPlacedBlocks.remove(event.getPos());
		}
	}
	
	@SubscribeEvent
	public static void modelEvents(final RenderWorldLastEvent event) {
		final EntityPlayerSP sp = Minecraft.getMinecraft().player;
		final Block block = Block.getBlockFromItem(sp.getHeldItemMainhand().getItem());
		if (block instanceof BlockInvisibleLight) {
			final BlockPos pos = sp.getPosition();
			if (pos.distanceSq(lastPosition) > UPDATE_SPHERE) {
				synchronized (playerPlacedBlocks) {
					playerPlacedBlocks.clear();
				}
				dirty = true;
			}
			if (dirty)
				refill(pos, sp.world);
			if (playerPlacedBlocks.isEmpty())
				return;
			final double part = event.getPartialTicks();
			d1 = sp.lastTickPosX + (sp.posX - sp.lastTickPosX) * part;
			d2 = sp.lastTickPosY + (sp.posY - sp.lastTickPosY) * part;
			d3 = sp.lastTickPosZ + (sp.posZ - sp.lastTickPosZ) * part;
			
			GlStateManager.disableTexture2D();
			synchronized (playerPlacedBlocks) {
				playerPlacedBlocks.forEach(ClientProxy::render);
			}
			GlStateManager.enableTexture2D();
		} else if (!playerPlacedBlocks.isEmpty()) {
			playerPlacedBlocks.clear();
			dirty = true;
		}
	}
	
	@Override
	public void init(final FMLInitializationEvent event) {
		super.init(event);
	}
	
	@Override
	public void postinit(final FMLPostInitializationEvent event) {
		super.postinit(event);
	}
	
}
