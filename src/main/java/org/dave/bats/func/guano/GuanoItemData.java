package org.dave.bats.func.guano;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.dave.bats.base.BaseNBTSerializable;
import org.dave.bats.func.batcage.BatCageTileEntity;
import org.dave.bats.init.Itemss;
import org.dave.bats.util.serialization.Store;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class GuanoItemData extends BaseNBTSerializable {
    @Store(key = "cagePos")
    BlockPos cagePosition;

    public GuanoItemData(BlockPos cagePosition) {
        super();

        this.cagePosition = cagePosition;
    }

    public GuanoItemData(ItemStack stack) {
        super();

        if(!stack.hasTagCompound()) {
            return;
        }

        this.deserializeNBT(stack.getTagCompound());
    }

    public ItemStack createNewItemStack() {
        ItemStack result = new ItemStack(Itemss.guano, 1, 0);
        result.setTagCompound(this.serializeNBT());
        return result;
    }

    public boolean hasBatCagePosition() {
        return cagePosition != null;
    }

    public BlockPos getCagePosition() {
        return cagePosition;
    }

    public GuanoItemData setCagePosition(BlockPos cagePos) {
        this.cagePosition = cagePos;
        return this;
    }

    @Nullable
    public BatCageTileEntity getBatCageTile(@Nonnull World world) {
        if(!this.hasBatCagePosition()) {
            return null;
        }

        TileEntity tileEntity = world.getTileEntity(this.getCagePosition());
        if(!(tileEntity instanceof BatCageTileEntity)) {
            return null;
        }

        return (BatCageTileEntity) tileEntity;
    }
}
