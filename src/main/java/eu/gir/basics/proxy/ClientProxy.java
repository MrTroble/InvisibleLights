package eu.gir.basics.proxy;

import java.util.ArrayList;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import eu.gir.basics.blocks.BlockCustomLight;
import eu.gir.basics.blocks.BlockInvisibleLight;
import eu.gir.basics.blocks.BlockLightBlocker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ExtractBlockOutlineRenderStateEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.event.level.BlockEvent.BreakEvent;
import net.neoforged.neoforge.event.level.BlockEvent.EntityPlaceEvent;

public final class ClientProxy {

	private ClientProxy() {}

	public static void onClientSetup(final FMLClientSetupEvent event) {
	}

	private static final int RADIUS = 50;
	private static final int UPDATE_SPHERE = 50;
	private static final int RADIUSPLAYER = RADIUS * RADIUS + 10;

	private static final float[] COLOR_NORMAL = new float[] { 0, 1, 0, 1 };
	private static final float[] COLOR_BLOCKER = new float[] { 1, 0, 0, 1 };
	private static final float[] COLOR_CUSTOM = new float[] { 1, 0.5f, 0, 1 };

	private static final ArrayList<BlockPos> playerPlacedBlocks = new ArrayList<>();
	private static boolean dirty = true;
	private static BlockPos lastPosition = BlockPos.ZERO;

	public static void refill(final BlockPos pos, final Level level) {
		lastPosition = pos;
		dirty = false;
		new Thread(() -> {
			for (int x = -RADIUS; x <= RADIUS; x++) {
				for (int y = -RADIUS; y <= RADIUS; y++) {
					for (int z = -RADIUS; z <= RADIUS; z++) {
						final BlockPos nPos = pos.offset(x, y, z);
						final Block pBlock = level.getBlockState(nPos).getBlock();
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
	public static void renderOverlayEvent(final ExtractBlockOutlineRenderStateEvent event) {
		if (event.getBlockState().getBlock() instanceof BlockInvisibleLight)
			event.setCanceled(true);
	}

	@SubscribeEvent
	public static void blockPlaceEvent(final EntityPlaceEvent event) {
		final Entity placerEntity = event.getEntity();
		if (placerEntity == null)
			return;
		final LocalPlayer player = Minecraft.getInstance().player;
		if (player == null)
			return;
		final BlockPos playerPos = player.blockPosition();
		final double distance = placerEntity.blockPosition().distSqr(playerPos);
		if (distance < RADIUSPLAYER) {
			if (event.getPlacedBlock().getBlock() instanceof BlockInvisibleLight) {
				playerPlacedBlocks.clear();
				refill(playerPos, player.level());
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
	public static void renderWorldLastEvent(final RenderLevelStageEvent.AfterParticles event) {
		final LocalPlayer sp = Minecraft.getInstance().player;
		if (sp == null)
			return;
		final Block block = Block.byItem(sp.getMainHandItem().getItem());
		if (!(block instanceof BlockInvisibleLight)) {
			if (!playerPlacedBlocks.isEmpty()) {
				playerPlacedBlocks.clear();
				dirty = true;
			}
			return;
		}

		final BlockPos pos = sp.blockPosition();
		if (pos.distSqr(lastPosition) > UPDATE_SPHERE) {
			synchronized (playerPlacedBlocks) {
				playerPlacedBlocks.clear();
			}
			dirty = true;
		}
		if (dirty)
			refill(pos, sp.level());
		if (playerPlacedBlocks.isEmpty())
			return;

		final Vec3 view = Minecraft.getInstance().gameRenderer.getMainCamera().position();
		final PoseStack poseStack = new PoseStack();

		final MultiBufferSource.BufferSource buffers = Minecraft.getInstance().renderBuffers().bufferSource();
		final RenderType lines = RenderTypes.LINES;
		final VertexConsumer builder = buffers.getBuffer(lines);

		synchronized (playerPlacedBlocks) {
			playerPlacedBlocks.forEach(posIn -> {
				final Block blockIn = sp.level().getBlockState(posIn).getBlock();
				final float[] color = blockIn instanceof BlockLightBlocker
						? COLOR_BLOCKER
						: (blockIn instanceof BlockCustomLight ? COLOR_CUSTOM : COLOR_NORMAL);
				renderLineBox(poseStack.last(), builder,
						(float) (posIn.getX() - view.x), (float) (posIn.getY() - view.y), (float) (posIn.getZ() - view.z),
						(float) (posIn.getX() + 1.0 - view.x), (float) (posIn.getY() + 1.0 - view.y), (float) (posIn.getZ() + 1.0 - view.z),
						color[0], color[1], color[2], color[3]);
			});
		}

		buffers.endBatch(lines);
	}

	private static void renderLineBox(final PoseStack.Pose pose, final VertexConsumer vc,
			final float x1, final float y1, final float z1,
			final float x2, final float y2, final float z2,
			final float r, final float g, final float b, final float a) {
		line(pose, vc, x1, y1, z1, x2, y1, z1, r, g, b, a, 1f, 0f, 0f);
		line(pose, vc, x1, y1, z1, x1, y2, z1, r, g, b, a, 0f, 1f, 0f);
		line(pose, vc, x1, y1, z1, x1, y1, z2, r, g, b, a, 0f, 0f, 1f);
		line(pose, vc, x2, y1, z1, x2, y2, z1, r, g, b, a, 0f, 1f, 0f);
		line(pose, vc, x2, y1, z1, x2, y1, z2, r, g, b, a, 0f, 0f, 1f);
		line(pose, vc, x1, y2, z1, x2, y2, z1, r, g, b, a, 1f, 0f, 0f);
		line(pose, vc, x1, y2, z1, x1, y2, z2, r, g, b, a, 0f, 0f, 1f);
		line(pose, vc, x1, y1, z2, x2, y1, z2, r, g, b, a, 1f, 0f, 0f);
		line(pose, vc, x1, y1, z2, x1, y2, z2, r, g, b, a, 0f, 1f, 0f);
		line(pose, vc, x2, y2, z1, x2, y2, z2, r, g, b, a, 0f, 0f, 1f);
		line(pose, vc, x2, y1, z2, x2, y2, z2, r, g, b, a, 0f, 1f, 0f);
		line(pose, vc, x1, y2, z2, x2, y2, z2, r, g, b, a, 1f, 0f, 0f);
	}

	private static void line(final PoseStack.Pose pose, final VertexConsumer vc,
			final float ax, final float ay, final float az,
			final float bx, final float by, final float bz,
			final float r, final float g, final float bCol, final float a,
			final float nx, final float ny, final float nz) {
		vc.addVertex(pose, ax, ay, az).setColor(r, g, bCol, a).setNormal(pose, nx, ny, nz).setLineWidth(1f);
		vc.addVertex(pose, bx, by, bz).setColor(r, g, bCol, a).setNormal(pose, nx, ny, nz).setLineWidth(1f);
	}
}
