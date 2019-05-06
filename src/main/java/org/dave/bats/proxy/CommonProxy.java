package org.dave.bats.proxy;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;
import org.dave.bats.Bats;
import org.dave.bats.func.bat.CapturedBatItem;
import org.dave.bats.func.batcage.BatCageBlock;
import org.dave.bats.func.batcage.BatCageItemBlock;
import org.dave.bats.func.batcage.BatCageTileEntity;
import org.dave.bats.func.guano.GuanoItem;

@Mod.EventBusSubscriber
public class CommonProxy {
    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        IForgeRegistry<Block> registry = event.getRegistry();
        registry.register(new BatCageBlock());

        GameRegistry.registerTileEntity(BatCageTileEntity.class, new ResourceLocation(Bats.MODID, "batcage"));
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        IForgeRegistry<Item> registry = event.getRegistry();

        registry.register(new BatCageItemBlock());
        registry.register(new GuanoItem());
        registry.register(new CapturedBatItem());
    }

    public void preInit(FMLPreInitializationEvent event) {
    }

    public void init(FMLInitializationEvent event) {
    }

    public void postInit(FMLPostInitializationEvent event) {
    }
}
