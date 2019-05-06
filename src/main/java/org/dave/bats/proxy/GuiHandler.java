package org.dave.bats.proxy;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import org.dave.bats.Bats;
import org.dave.bats.func.batcage.BatCageContainer;
import org.dave.bats.func.batcage.BatCageGuiContainer;

public class GuiHandler implements IGuiHandler {
    public static void init() {
        NetworkRegistry.INSTANCE.registerGuiHandler(Bats.instance, new GuiHandler());
    }

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        if(ID == GuiIDs.BAT_CAGE.ordinal()) {
            return new BatCageContainer(world, new BlockPos(x, y, z), player);
        }
        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        if(ID == GuiIDs.BAT_CAGE.ordinal()) {
            return new BatCageGuiContainer(new BatCageContainer(world, new BlockPos(x, y, z), player));
        }
        return null;
    }

    public enum GuiIDs {
        GUANO,
        BAT_CAGE;
    }
}
