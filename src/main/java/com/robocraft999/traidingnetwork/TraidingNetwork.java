package com.robocraft999.traidingnetwork;

import com.mojang.logging.LogUtils;
import com.robocraft999.traidingnetwork.api.capabilities.impl.ShredderOffline;
import com.robocraft999.traidingnetwork.api.resourcepoints.ItemNetwork;
import com.robocraft999.traidingnetwork.net.PacketHandler;
import com.robocraft999.traidingnetwork.registry.TNBlockEntities;
import com.robocraft999.traidingnetwork.registry.TNBlocks;
import com.robocraft999.traidingnetwork.registry.TNMenuTypes;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.Registrate;
import com.tterrag.registrate.util.entry.RegistryEntry;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(TraidingNetwork.MODID)
public class TraidingNetwork {

    // Define mod id in a common place for everything to reference
    public static final String MODID = "traidingnetwork";

    //public static final NonNullSupplier<Registrate> REGISTRATE = NonNullSupplier.lazy(() -> Registrate.create(MODID));
    public static final CreateRegistrate REGISTRATE = CreateRegistrate.create(MODID);

    public static final RegistryEntry<CreativeModeTab> testcreativetab = REGISTRATE.object("test_creative_mode_tab")
            .defaultCreativeTab(tab -> tab.withLabelColor(0xFF00AA00))
            .register();


    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    public TraidingNetwork() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);
        REGISTRATE.registerEventListeners(modEventBus);

        TNBlocks.register();
        TNBlockEntities.register();
        TNMenuTypes.register();

        PacketHandler.register();

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);

        // Register our mod's ForgeConfigSpec so that Forge can create and load the config file for us
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        // Some common setup code
        LOGGER.info("HELLO FROM COMMON SETUP");
        LOGGER.info("DIRT BLOCK >> {}", ForgeRegistries.BLOCKS.getKey(Blocks.DIRT));

        if (Config.logDirtBlock)
            LOGGER.info("DIRT BLOCK >> {}", ForgeRegistries.BLOCKS.getKey(Blocks.DIRT));

        LOGGER.info(Config.magicNumberIntroduction + Config.magicNumber);

        Config.items.forEach((item) -> LOGGER.info("ITEM >> {}", item.toString()));
    }

    public static ResourceLocation rl(String name){
        return new ResourceLocation(MODID, name);
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // Do something when the server starts
        LOGGER.info("HELLO from server starting");
    }

    @SubscribeEvent
    public void onServerStopping(ServerStoppingEvent event){
        for (Slot slot : ItemNetwork.INSTANCE.slots){
            LOGGER.info(slot.getItem().toString());
        }
    }

    @SubscribeEvent
    public void onServerStopped(ServerStoppedEvent event){
        ShredderOffline.clearAll();
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {

        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
            // Some client setup code
            LOGGER.info("HELLO FROM CLIENT SETUP");
            LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
        }
    }
}