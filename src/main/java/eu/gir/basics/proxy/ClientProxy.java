package eu.gir.basics.proxy;

import java.util.ArrayList;

import com.mojang.blaze3d.platform.GlStateManager;

import eu.gir.basics.blocks.BlockCustomLight;
import eu.gir.basics.blocks.BlockInvisibleLight;
import eu.gir.basics.blocks.BlockLightBlocker;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.event.world.BlockEvent.EntityPlaceEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@OnlyIn(Dist.CLIENT)
public final class ClientProxy {

	private ClientProxy() {}

	private static final int RADIUS = 50;
	private static final int UPDATE_SPHERE = 50;
	private static final int RADIUSPLAYER = RADIUS * RADIUS + 10;
	private static final AxisAlignedBB FULL_CUBE = new AxisAlignedBB(0, 0, 0, 1, 1, 1);

	private static double d1;
	private static double d2;
	private static double d3;

	private static final float[] COLOR_NORMAL = new float[] { 0, 1, 0, 1 };
	private static final float[] COLOR_BLOCKER = new float[] { 1, 0, 0, 1 };
	private static final float[] COLOR_CUSTOM = new float[] { 1, 0.5f, 0, 1 };

	public static void render(final BlockPos pos1, final float[] color) {
		WorldRenderer.drawSelectionBoundingBox(
				FULL_CUBE.offset(pos1.getX() - d1, pos1.getY() - d2, pos1.getZ() - d3),
				color[0], color[1], color[2], color[3]);
	}

	private static final ArrayList<BlockPos> playerPlacedBlocks = new ArrayList<>();
	private static boolean dirty = true;
	private static BlockPos lastPosition = BlockPos.ZERO;

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

	@SubscribeEvent
	public static void renderOverlayEvent(final DrawBlockHighlightEvent render) {
		final RayTraceResult result = render.getTarget();
		if (!(result instanceof BlockRayTraceResult))
			return;
		final PlayerEntity player = Minecraft.getInstance().player;
		if (player == null)
			return;
		final World world = player.getEntityWorld();
		if (world == null)
			return;
		final BlockPos pos = ((BlockRayTraceResult) result).getPos();
		final BlockState state = world.getBlockState(pos);
		if (state.getBlock() instanceof BlockInvisibleLight)
			render.setCanceled(true);
	}

	@SubscribeEvent
	public static void blockPlaceEvent(final EntityPlaceEvent event) {
		final Entity placerEntity = event.getEntity();
		if (placerEntity == null)
			return;
		final ClientPlayerEntity player = Minecraft.getInstance().player;
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

	@SubscribeEvent
	public static void blockBreakEvent(final BreakEvent event) {
		synchronized (playerPlacedBlocks) {
			playerPlacedBlocks.remove(event.getPos());
		}
	}

	@SubscribeEvent
	public static void renderWorldLastEvent(final RenderWorldLastEvent event) {
		final ClientPlayerEntity sp = Minecraft.getInstance().player;
		if (sp == null)
			return;
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
			final Vec3d view = Minecraft.getInstance().gameRenderer.getActiveRenderInfo().getProjectedView();
			d1 = view.x;
			d2 = view.y;
			d3 = view.z;

			GlStateManager.disableTexture();
			synchronized (playerPlacedBlocks) {
				playerPlacedBlocks.forEach(posIn -> {
					final Block blockIn = sp.world.getBlockState(posIn).getBlock();
					final float[] color = blockIn instanceof BlockLightBlocker
							? COLOR_BLOCKER
							: (blockIn instanceof BlockCustomLight ? COLOR_CUSTOM : COLOR_NORMAL);
					ClientProxy.render(posIn, color);
				});
			}
			GlStateManager.enableTexture();
		} else if (!playerPlacedBlocks.isEmpty()) {
			playerPlacedBlocks.clear();
			dirty = true;
		}
	}
}
