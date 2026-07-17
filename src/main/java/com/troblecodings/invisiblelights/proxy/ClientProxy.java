package com.troblecodings.invisiblelights.proxy;

import java.util.ArrayList;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.troblecodings.invisiblelights.blocks.BlockCustomLight;
import com.troblecodings.invisiblelights.blocks.BlockInvisibleLight;
import com.troblecodings.invisiblelights.blocks.BlockLightBlocker;
import com.troblecodings.invisiblelights.init.ILInit;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderHighlightEvent;
import net.minecraftforge.client.event.RenderLevelLastEvent;
import net.minecraftforge.event.level.BlockEvent.BreakEvent;
import net.minecraftforge.event.level.BlockEvent.EntityPlaceEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@OnlyIn(Dist.CLIENT)
public final class ClientProxy {

    private ClientProxy() {
    }

    public static void onClientSetup(final FMLClientSetupEvent event) {
        ItemBlockRenderTypes.setRenderLayer(ILInit.GHOST_GLOWSTONE.get(),
                RenderType.cutoutMipped());
    }

    private static final int RADIUS = 50;
    private static final int UPDATE_SPHERE = 50;
    private static final int RADIUSPLAYER = RADIUS * RADIUS + 10;

    private static final float[] COLOR_NORMAL = new float[] {
            0, 1, 0, 1
    };
    private static final float[] COLOR_BLOCKER = new float[] {
            1, 0, 0, 1
    };
    private static final float[] COLOR_CUSTOM = new float[] {
            1, 0.5f, 0, 1
    };

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
    public static void renderOverlayEvent(final RenderHighlightEvent.Block event) {
        final BlockHitResult result = event.getTarget();
        final Player player = Minecraft.getInstance().player;
        if (player == null)
            return;
        final Level level = player.level;
        if (level == null)
            return;
        final BlockState state = level.getBlockState(result.getBlockPos());
        if (state.getBlock() instanceof BlockInvisibleLight) {
            event.setCanceled(true);
        }
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
                refill(playerPos, player.level);
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
    public static void renderWorldLastEvent(final RenderLevelLastEvent event) {
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
        if (dirty) {
            refill(pos, sp.level);
        }
        if (playerPlacedBlocks.isEmpty())
            return;

        final Vec3 view = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        final PoseStack poseStack = event.getPoseStack();
        poseStack.pushPose();
        poseStack.translate(-view.x, -view.y, -view.z);

        final MultiBufferSource.BufferSource buffers =
                Minecraft.getInstance().renderBuffers().bufferSource();
        final VertexConsumer builder = buffers.getBuffer(RenderType.lines());

        synchronized (playerPlacedBlocks) {
            playerPlacedBlocks.forEach(posIn -> {
                final Block blockIn = sp.level.getBlockState(posIn).getBlock();
                final float[] color = blockIn instanceof BlockLightBlocker ? COLOR_BLOCKER
                        : (blockIn instanceof BlockCustomLight ? COLOR_CUSTOM : COLOR_NORMAL);
                final AABB box = new AABB(posIn.getX(), posIn.getY(), posIn.getZ(),
                        posIn.getX() + 1.0, posIn.getY() + 1.0, posIn.getZ() + 1.0);
                net.minecraft.client.renderer.LevelRenderer.renderLineBox(poseStack, builder, box,
                        color[0], color[1], color[2], color[3]);
            });
        }

        poseStack.popPose();
        buffers.endBatch(RenderType.lines());
    }
}
