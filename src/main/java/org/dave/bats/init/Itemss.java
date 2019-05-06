package org.dave.bats.init;

import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.dave.bats.Bats;
import org.dave.bats.func.bat.CapturedBatItem;
import org.dave.bats.func.guano.GuanoItem;

@GameRegistry.ObjectHolder(Bats.MODID)
public class Itemss {
    @GameRegistry.ObjectHolder("guano")
    public static GuanoItem guano;

    @GameRegistry.ObjectHolder("capturedbat")
    public static CapturedBatItem capturedBat;

    @SideOnly(Side.CLIENT)
    public static void initModels() {
        guano.initModel();
        capturedBat.initModel();
    }
}
