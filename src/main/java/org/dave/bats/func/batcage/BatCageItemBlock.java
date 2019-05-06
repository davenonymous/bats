package org.dave.bats.func.batcage;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.dave.bats.base.BaseItemBlock;
import org.dave.bats.init.Blockss;

import javax.annotation.Nullable;
import java.util.List;

public class BatCageItemBlock extends BaseItemBlock {
    public BatCageItemBlock() {
        super(Blockss.batCage);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);

        tooltip.add(I18n.format("tile.bats.batcage.tooltip"));
    }


}
