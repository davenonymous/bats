package org.dave.bats.base;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.EnumHand;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.dave.bats.Bats;

public class BaseItem extends Item {
    public BaseItem(String name) {
        this.setRegistryName(Bats.MODID, name);
        this.setTranslationKey(Bats.MODID + "." + name);
    }

    @SideOnly(Side.CLIENT)
    public void initModel() {
        ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(getRegistryName(), "inventory"));
    }

    public void renderEffectOnHeldItem(EntityPlayer player, EnumHand mainHand, float partialTicks) {
    }
}
