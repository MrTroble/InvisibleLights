package com.troblecodings.invisiblelights.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class BlockInvisibleLight extends Block {

	public BlockInvisibleLight(final int light) {
		super(Block.Properties.create(Material.EARTH)
				.lightValue(light)
				.hardnessAndResistance(0.5f)
				.notSolid());
	}

	@Override
	public BlockRenderType getRenderType(final BlockState state) {
		return BlockRenderType.INVISIBLE;
	}

	@Override
	public VoxelShape getCollisionShape(final BlockState state, final IBlockReader worldIn, final BlockPos pos,
			final ISelectionContext context) {
		return VoxelShapes.empty();
	}

	@Override
	public VoxelShape getShape(final BlockState state, final IBlockReader worldIn, final BlockPos pos,
			final ISelectionContext context) {
		return VoxelShapes.fullCube();
	}

	@Override
	public boolean addHitEffects(final BlockState state, final World world, final RayTraceResult target,
			final ParticleManager manager) {
		return true;
	}

	@Override
	public boolean addDestroyEffects(final BlockState state, final World world, final BlockPos pos,
			final ParticleManager manager) {
		return true;
	}
}
