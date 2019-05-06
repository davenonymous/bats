package org.dave.bats.init;

import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.dave.bats.Bats;
import org.dave.bats.func.batcage.BatCageBlock;

@GameRegistry.ObjectHolder(Bats.MODID)
public class Blockss {
    @GameRegistry.ObjectHolder("batcage")
    public static BatCageBlock batCage;

    @SideOnly(Side.CLIENT)
    public static void initModels() {
        batCage.initModel();
    }

    @SideOnly(Side.CLIENT)
    public static void initItemModels() {
        batCage.initItemModel();
    }
}
