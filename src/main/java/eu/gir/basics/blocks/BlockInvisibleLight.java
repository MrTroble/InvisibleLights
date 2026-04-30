package eu.gir.basics.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;

public class BlockInvisibleLight extends Block {

	public BlockInvisibleLight(final int light) {
		super(Block.Properties.create(Material.EARTH)
				.lightValue(light)
				.hardnessAndResistance(0.5f));
	}

	@Override
	public boolean isSolid(final BlockState state) {
		return false;
	}

	@Override
	public BlockRenderType getRenderType(final BlockState state) {
		return BlockRenderType.INVISIBLE;
	}

	@Override
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.CUTOUT;
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
}
