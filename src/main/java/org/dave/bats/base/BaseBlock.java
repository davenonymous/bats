package org.dave.bats.base;

import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.dave.bats.Bats;


public class BaseBlock extends Block {
    public BaseBlock(String name, Material blockMaterial, MapColor blockMapColor) {
        super(blockMaterial, blockMapColor);
        this.setRegistryName(Bats.MODID, name);
        this.setTranslationKey(Bats.MODID + "." + name);

        this.setDefaultState(blockState.getBaseState());
    }

    @SideOnly(Side.CLIENT)
    public void initModel() {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(getRegistryName(), "inventory"));
    }

    @SideOnly(Side.CLIENT)
    public void initItemModel() {
    }

    @SuppressWarnings("deprecation")
    @Override
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block blockIn, BlockPos fromPos) {
        super.neighborChanged(state, world, pos, blockIn, fromPos);

        if(world.isRemote) {
            return;
        }

        TileEntity tileEntity = world.getTileEntity(pos);
        if(!(tileEntity instanceof BaseTileEntity)) {
            return;
        }

        BaseTileEntity base = (BaseTileEntity) tileEntity;
        int previous = base.getIncomingRedstonePower();
        int now = world.getRedstonePowerFromNeighbors(pos);

        if(now == 0) {
            if(previous > 0) {
                base.redstonePulse();
            }
        } else {
            if(previous != now) {
                base.redstoneChanged(previous, now);
            }
        }

        base.setIncomingRedstonePower(now);
    }

    public void onBlockPlacedBySided(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack, EnumFacing side) {
        this.onBlockPlacedBy(world, pos, state, placer, stack);
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        super.onBlockPlacedBy(world, pos, state, placer, stack);

        if(!(world.getTileEntity(pos) instanceof BaseTileEntity)) {
            return;
        }

        BaseTileEntity baseTile = (BaseTileEntity) world.getTileEntity(pos);
        baseTile.loadFromItem(stack);
    }

    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        if(world.isRemote) {
            super.breakBlock(world, pos, state);
            return;
        }

        if(world.getTileEntity(pos) == null || !(world.getTileEntity(pos) instanceof BaseTileEntity)) {
            super.breakBlock(world, pos, state);
            return;
        }

        BaseTileEntity baseTile = (BaseTileEntity) world.getTileEntity(pos);
        baseTile.spawnItemInWorld();
    }
}
