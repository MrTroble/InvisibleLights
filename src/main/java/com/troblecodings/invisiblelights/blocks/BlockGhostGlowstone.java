package com.troblecodings.invisiblelights.blocks;

import com.troblecodings.invisiblelights.init.ILInit;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class BlockGhostGlowstone extends Block {

    public BlockGhostGlowstone() {
        super(Material.GLASS);
        setCreativeTab(ILInit.LIGHT_TAB);
        setSoundType(SoundType.GLASS);
    }

    @Override
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.CUTOUT_MIPPED;
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos,
            EnumFacing face) {
        return BlockFaceShape.CENTER;
    }
    
    @Override
    public boolean doesSideBlockRendering(IBlockState state, IBlockAccess world, BlockPos pos,
            EnumFacing face) {
        return false;
    }

    public static void init() {
        GameRegistry.addSmelting(Blocks.GLOWSTONE, new ItemStack(ILInit.GHOST_GLOWSTONE), 0.35f);
    }

}
