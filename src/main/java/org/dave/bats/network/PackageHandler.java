package org.dave.bats.network;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import org.dave.bats.Bats;

public class PackageHandler {
    public static final SimpleNetworkWrapper instance = NetworkRegistry.INSTANCE.newSimpleChannel(Bats.MODID);

    public static void init() {
        instance.registerMessage(MessageInventoryStackRule.class, MessageInventoryStackRule.class, 0, Side.SERVER);
        instance.registerMessage(MessageInventoryLinkConfig.class, MessageInventoryLinkConfig.class, 1, Side.SERVER);
        instance.registerMessage(MessageEnabledSlots.class, MessageEnabledSlots.class, 2, Side.SERVER);
        instance.registerMessage(MessageGetGuano.class, MessageGetGuano.class, 3, Side.SERVER);
    }
}
