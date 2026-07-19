package com.troblecodings.invisiblelights.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BlockInvisibleLight extends Block {

    public BlockInvisibleLight(final int light) {
        super(Block.Properties.of().lightLevel(state -> light).strength(0.5f).noOcclusion());
    }

    @Override
    public RenderShape getRenderShape(final BlockState state) {
        return RenderShape.INVISIBLE;
    }

    @Override
    public VoxelShape getCollisionShape(final BlockState state, final BlockGetter worldIn,
            final BlockPos pos, final CollisionContext context) {
        return Shapes.empty();
    }

    @Override
    public VoxelShape getShape(final BlockState state, final BlockGetter worldIn,
            final BlockPos pos, final CollisionContext context) {
        return Shapes.block();
    }
}