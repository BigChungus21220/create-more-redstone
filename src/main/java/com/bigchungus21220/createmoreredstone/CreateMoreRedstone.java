package com.bigchungus21220.createmoreredstone;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;
import com.bigchungus21220.createmoreredstone.index.MRBlockEntities;
import com.bigchungus21220.createmoreredstone.index.MRBlockShapes;
import com.bigchungus21220.createmoreredstone.index.MRBlocks;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

import com.simibubi.create.foundation.data.CreateRegistrate;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(CreateMoreRedstone.MODID)
public class CreateMoreRedstone {
    public static final String MODID = "createmoreredstone";
    public static final Logger LOGGER = LogUtils.getLogger();

	public static final CreateRegistrate REGISTRATE = CreateRegistrate.create(MODID);

    // The constructor for the mod class is the first code that is run when your mod is loaded.
    // FML will recognize some parameter types like IEventBus or ModContainer and pass them in automatically.
    public CreateMoreRedstone(IEventBus modEventBus, ModContainer modContainer) {
        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        NeoForge.EVENT_BUS.register(this);

        // Register the item to a creative tab
        modEventBus.addListener(this::addCreative);

        // Register our mod's ModConfigSpec so that FML can create and load the config file for us
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);

        /*
        // might use these at some point, idk
        IE_ACTIVE = ModList.get().isLoaded("immersiveengineering");
        CC_ACTIVE = ModList.get().isLoaded("computercraft");
        AE2_ACTIVE = ModList.get().isLoaded("ae2");
        */

        REGISTRATE.registerEventListeners(modEventBus);
        MRBlocks.register();
        MRBlockEntities.register();
        MRBlockShapes.register();
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        // Some common setup code
        LOGGER.info("HELLO FROM COMMON SETUP");

        if (Config.LOG_DIRT_BLOCK.getAsBoolean()) {
            LOGGER.info("DIRT BLOCK >> {}", BuiltInRegistries.BLOCK.getKey(Blocks.DIRT));
        }

        LOGGER.info("{}{}", Config.MAGIC_NUMBER_INTRODUCTION.get(), Config.MAGIC_NUMBER.getAsInt());

        Config.ITEM_STRINGS.get().forEach((item) -> LOGGER.info("ITEM >> {}", item));
    }

    // Add the example block item to the building blocks tab
    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        /*if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS) {
            event.accept(EXAMPLE_BLOCK_ITEM);
        }*/
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // Do something when the server starts
        LOGGER.info("HELLO from server starting");
    }

    public static ResourceLocation path(final String path) {
        return ResourceLocation.tryBuild(MODID, path);
    }
}
