package eu.gir.basics.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;

public class BlockInvisibleLight extends Block {

	public BlockInvisibleLight(final int light) {
		super(Block.Properties.create(Material.GROUND)
				.lightValue(light)
				.hardnessAndResistance(0.5f));
	}

	@Override
	public boolean isSolid(final IBlockState state) {
		return false;
	}

	@Override
	public EnumBlockRenderType getRenderType(final IBlockState state) {
		return EnumBlockRenderType.INVISIBLE;
	}

	@Override
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.CUTOUT;
	}

	@Override
	public VoxelShape getCollisionShape(final IBlockState state, final IBlockReader worldIn, final BlockPos pos) {
		return VoxelShapes.empty();
	}

	@Override
	public VoxelShape getShape(final IBlockState state, final IBlockReader worldIn, final BlockPos pos) {
		return VoxelShapes.fullCube();
	}
}
