package org.dave.bats.func.bat;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.dave.bats.base.BaseItem;

import javax.annotation.Nullable;
import java.util.List;

public class CapturedBatItem extends BaseItem {
    public CapturedBatItem() {
        super("capturedbat");
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);

        tooltip.add(I18n.format("item.bats.capturedbat.tooltip"));
    }
}
