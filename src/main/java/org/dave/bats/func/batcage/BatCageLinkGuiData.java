package org.dave.bats.func.batcage;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import org.dave.bats.gui.BatCageSlot;

import java.util.List;

public class BatCageLinkGuiData {
    public BatCageLinkConfig linkConfig;
    public IBlockState currentBlockState;
    public List<BatCageSlot> slots;
    public ItemStack stack;
    public BlockPos cagePos;
    public boolean hasValidPath;
    public boolean supportsItems;
}
