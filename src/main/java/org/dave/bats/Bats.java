package org.dave.bats;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.dave.bats.base.BaseEvents;
import org.dave.bats.config.ConfigurationHandler;
import org.dave.bats.func.guano.GuanoEvents;
import org.dave.bats.network.PackageHandler;
import org.dave.bats.proxy.CommonProxy;
import org.dave.bats.proxy.GuiHandler;
import org.dave.bats.util.Logz;
import org.dave.bats.util.autoreg.AnnotationLoader;

@Mod(modid = Bats.MODID, version = Bats.VERSION, name = "Bats", acceptedMinecraftVersions = "[1.12,1.13)")
public class Bats {
    public static final String MODID = "bats";
    public static final String VERSION = "0.9.0";

    @Mod.Instance(Bats.MODID)
    public static Bats instance;

    @SidedProxy(clientSide = "org.dave.bats.proxy.ClientProxy", serverSide = "org.dave.bats.proxy.ServerProxy")
    public static CommonProxy proxy;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        Logz.logger = event.getModLog();

        ConfigurationHandler.init(event.getSuggestedConfigurationFile());
        MinecraftForge.EVENT_BUS.register(ConfigurationHandler.class);
        MinecraftForge.EVENT_BUS.register(BaseEvents.class);
        MinecraftForge.EVENT_BUS.register(GuanoEvents.class);

        PackageHandler.init();
        GuiHandler.init();
        AnnotationLoader.asmDataTable = event.getAsmData();

        proxy.preInit(event);
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init(event);
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit(event);
    }
}
