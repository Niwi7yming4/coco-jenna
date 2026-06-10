package com.cocojenna;

import com.cocojenna.capability.BondData;
import com.cocojenna.capability.FragmentedSequenceData;
import com.cocojenna.capability.ModCapabilities;
import com.cocojenna.client.ModClientRenderLayers;
import com.cocojenna.init.*;
import com.cocojenna.network.ModNetwork;
import com.mojang.logging.LogUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(CocoJennaMod.MOD_ID)
public class CocoJennaMod {

    public static final String MOD_ID = "cocojenna";
    public static final Logger LOGGER = LogUtils.getLogger();

    public CocoJennaMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModItems.ITEMS.register(modEventBus);
        ModBlocks.BLOCKS.register(modEventBus);
        ModBlockEntities.BLOCK_ENTITIES.register(modEventBus);
        ModMenuTypes.MENUS.register(modEventBus);
        ModCreativeTabs.CREATIVE_TABS.register(modEventBus);
        ModEntities.ENTITY_TYPES.register(modEventBus);
        ModSounds.SOUNDS.register(modEventBus);
        ModParticles.PARTICLE_TYPES.register(modEventBus);
        ModEffects.MOB_EFFECTS.register(modEventBus);
        ModDimensions.register(modEventBus);

        modEventBus.addListener(this::registerCapabilities);
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::clientSetup);

        MinecraftForge.EVENT_BUS.register(this);
        LOGGER.info("Coco & Jenna: Memories of the Cat Kingdom — Loading...");
    }

    private void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.register(BondData.class);
        event.register(FragmentedSequenceData.class);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            ModNetwork.register();
            LOGGER.info("[CocoJenna] Common setup complete.");
        });
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            ModClientRenderLayers.register();
            com.cocojenna.client.renderer.SwordBoneRenderer.INSTANCE.toString();
            com.cocojenna.client.gui.MemoryForgeHudOverlay.INSTANCE.toString();
        });
        LOGGER.info("[CocoJenna] Client setup complete.");
    }
}
