package com.cocojenna.capability;

import com.cocojenna.CocoJennaMod;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

@Mod.EventBusSubscriber(modid = CocoJennaMod.MOD_ID)
public class ModCapabilities {

    public static final Capability<BondData> BOND_DATA = CapabilityManager.get(new CapabilityToken<>() {});

    public static BondData getOrDefault(Player player) {
        return player.getCapability(BOND_DATA).orElse(new BondData());
    }

    public static Optional<BondData> get(Player player) {
        return player.getCapability(BOND_DATA).resolve();
    }

    @SubscribeEvent
    public static void onAttachCapabilities(AttachCapabilitiesEvent<net.minecraft.world.entity.Entity> event) {
        if (event.getObject() instanceof Player) {
            if (!event.getObject().getCapability(BOND_DATA).isPresent()) {
                event.addCapability(
                        new ResourceLocation(CocoJennaMod.MOD_ID, "bond_data"),
                        new BondDataProvider());
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        if (event.isWasDeath()) {
            BondData oldData = getOrDefault(event.getOriginal());
            BondData newData = getOrDefault(event.getEntity());
            newData.deserializeNBT(oldData.serializeNBT());
        }
    }

    private static class BondDataProvider implements ICapabilitySerializable<CompoundTag> {

        private final BondData data = new BondData();
        private final LazyOptional<BondData> lazy = LazyOptional.of(() -> data);

        @Override
        public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
            return BOND_DATA.orEmpty(cap, lazy);
        }

        @Override
        public CompoundTag serializeNBT() {
            return data.serializeNBT();
        }

        @Override
        public void deserializeNBT(CompoundTag nbt) {
            data.deserializeNBT(nbt);
        }
    }
}
