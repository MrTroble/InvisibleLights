package com.troblecodings.invisiblelights.proxy;

import java.util.ArrayList;

import com.mojang.blaze3d.vertex.PoseStack;
import com.troblecodings.invisiblelights.blocks.BlockCustomLight;
import com.troblecodings.invisiblelights.blocks.BlockInvisibleLight;
import com.troblecodings.invisiblelights.blocks.BlockLightBlocker;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.util.ARGB;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ExtractBlockOutlineRenderStateEvent;
import net.neoforged.neoforge.client.event.SubmitCustomGeometryEvent;
import net.neoforged.neoforge.event.level.BlockEvent.EntityPlaceEvent;
import net.neoforged.neoforge.event.level.block.BreakBlockEvent;

public final class ClientProxy {

    private ClientProxy() {
    }

    public static void onClientSetup(final FMLClientSetupEvent event) {
    }

    private static final int RADIUS = 50;
    private static final int UPDATE_SPHERE = 50;
    private static final int RADIUSPLAYER = RADIUS * RADIUS + 10;

    private static final int COLOR_NORMAL = ARGB.colorFromFloat(1.0F, 0.0F, 1.0F, 0.0F);
    private static final int COLOR_BLOCKER = ARGB.colorFromFloat(1.0F, 1.0F, 0.0F, 0.0F);
    private static final int COLOR_CUSTOM = ARGB.colorFromFloat(1.0F, 1.0F, 0.5F, 0.0F);

    private static final ArrayList<BlockPos> PLAYER_PLACED_BLOCKS = new ArrayList<>();
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
                            synchronized (PLAYER_PLACED_BLOCKS) {
                                PLAYER_PLACED_BLOCKS.add(nPos);
                            }
                        }
                    }
                }
            }
        }).start();
    }

    @SubscribeEvent
    public static void renderOverlayEvent(final ExtractBlockOutlineRenderStateEvent event) {
        if (event.getBlockState().getBlock() instanceof BlockInvisibleLight) {
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
                PLAYER_PLACED_BLOCKS.clear();
                refill(playerPos, player.level());
            }
        }
    }

    @SubscribeEvent
    public static void blockBreakEvent(final BreakBlockEvent event) {
        synchronized (PLAYER_PLACED_BLOCKS) {
            PLAYER_PLACED_BLOCKS.remove(event.getPos());
        }
    }

    @SubscribeEvent
    public static void onSubmitCustomGeometry(final SubmitCustomGeometryEvent event) {
        final LocalPlayer sp = Minecraft.getInstance().player;
        if (sp == null)
            return;

        final Block block = Block.byItem(sp.getMainHandItem().getItem());
        if (!(block instanceof BlockInvisibleLight)) {
            if (!PLAYER_PLACED_BLOCKS.isEmpty()) {
                PLAYER_PLACED_BLOCKS.clear();
                dirty = true;
            }
            return;
        }

        final BlockPos pos = sp.blockPosition();
        if (pos.distSqr(lastPosition) > UPDATE_SPHERE) {
            synchronized (PLAYER_PLACED_BLOCKS) {
                PLAYER_PLACED_BLOCKS.clear();
            }
            dirty = true;
        }
        if (dirty) {
            refill(pos, sp.level());
        }
        if (PLAYER_PLACED_BLOCKS.isEmpty())
            return;

        final Vec3 view = Minecraft.getInstance().gameRenderer.mainCamera().position();
        final PoseStack poseStack = event.getPoseStack();
        final RenderType lines = RenderTypes.lines();

        synchronized (PLAYER_PLACED_BLOCKS) {
            PLAYER_PLACED_BLOCKS.forEach(posIn -> {
                final Block blockIn = sp.level().getBlockState(posIn).getBlock();
                final int color = blockIn instanceof BlockLightBlocker ? COLOR_BLOCKER
                        : (blockIn instanceof BlockCustomLight ? COLOR_CUSTOM : COLOR_NORMAL);

                poseStack.pushPose();
                poseStack.translate(posIn.getX() - view.x, posIn.getY() - view.y,
                        posIn.getZ() - view.z);
                event.getSubmitNodeCollector().submitShapeOutline(poseStack, Shapes.block(), lines,
                        color, 1.0F, true);
                poseStack.popPose();
            });
        }
    }

}
