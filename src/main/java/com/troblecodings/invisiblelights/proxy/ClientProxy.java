package com.troblecodings.invisiblelights.proxy;

import java.util.ArrayList;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.troblecodings.invisiblelights.blocks.BlockCustomLight;
import com.troblecodings.invisiblelights.blocks.BlockInvisibleLight;
import com.troblecodings.invisiblelights.blocks.BlockLightBlocker;
import com.troblecodings.invisiblelights.init.ILInit;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.DrawHighlightEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.event.world.BlockEvent.EntityPlaceEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@OnlyIn(Dist.CLIENT)
public final class ClientProxy {

    private ClientProxy() {
    }

    public static void onClientSetup(final FMLClientSetupEvent event) {
        RenderTypeLookup.setRenderLayer(ILInit.GHOST_GLOWSTONE, RenderType.getCutoutMipped());
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

    private static final ArrayList<BlockPos> PLAYER_PLACED_BLOCKS = new ArrayList<>();
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
    public static void renderOverlayEvent(final DrawHighlightEvent.HighlightBlock event) {
        final BlockRayTraceResult result = event.getTarget();
        final PlayerEntity player = Minecraft.getInstance().player;
        if (player == null)
            return;
        final World world = player.getEntityWorld();
        if (world == null)
            return;
        final BlockState state = world.getBlockState(result.getPos());
        if (state.getBlock() instanceof BlockInvisibleLight) {
            event.setCanceled(true);
        }
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
                PLAYER_PLACED_BLOCKS.clear();
                refill(playerPos, player.getEntityWorld());
            }
        }
    }

    @SubscribeEvent
    public static void blockBreakEvent(final BreakEvent event) {
        synchronized (PLAYER_PLACED_BLOCKS) {
            PLAYER_PLACED_BLOCKS.remove(event.getPos());
        }
    }

    @SubscribeEvent
    public static void renderWorldLastEvent(final RenderWorldLastEvent event) {
        final ClientPlayerEntity sp = Minecraft.getInstance().player;
        if (sp == null)
            return;
        final Block block = Block.getBlockFromItem(sp.getHeldItemMainhand().getItem());
        if (!(block instanceof BlockInvisibleLight)) {
            if (!PLAYER_PLACED_BLOCKS.isEmpty()) {
                PLAYER_PLACED_BLOCKS.clear();
                dirty = true;
            }
            return;
        }

        final BlockPos pos = sp.getPosition();
        if (pos.distanceSq(lastPosition) > UPDATE_SPHERE) {
            synchronized (PLAYER_PLACED_BLOCKS) {
                PLAYER_PLACED_BLOCKS.clear();
            }
            dirty = true;
        }
        if (dirty) {
            refill(pos, sp.world);
        }
        if (PLAYER_PLACED_BLOCKS.isEmpty())
            return;

        final Vector3d view =
                Minecraft.getInstance().gameRenderer.getActiveRenderInfo().getProjectedView();
        final MatrixStack matrixStack = event.getMatrixStack();
        matrixStack.push();
        matrixStack.translate(-view.x, -view.y, -view.z);

        final IRenderTypeBuffer.Impl buffers =
                Minecraft.getInstance().getRenderTypeBuffers().getBufferSource();
        final IVertexBuilder builder = buffers.getBuffer(RenderType.getLines());

        synchronized (PLAYER_PLACED_BLOCKS) {
            PLAYER_PLACED_BLOCKS.forEach(posIn -> {
                final Block blockIn = sp.world.getBlockState(posIn).getBlock();
                final float[] color = blockIn instanceof BlockLightBlocker ? COLOR_BLOCKER
                        : (blockIn instanceof BlockCustomLight ? COLOR_CUSTOM : COLOR_NORMAL);
                final AxisAlignedBB box = new AxisAlignedBB(posIn.getX(), posIn.getY(),
                        posIn.getZ(), posIn.getX() + 1.0, posIn.getY() + 1.0, posIn.getZ() + 1.0);
                WorldRenderer.drawBoundingBox(matrixStack, builder, box, color[0], color[1],
                        color[2], color[3]);
            });
        }

        matrixStack.pop();
        buffers.finish(RenderType.getLines());
    }
}
