package com.cocojenna.capability;

import com.cocojenna.CocoJennaMod;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.Villager;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.Nullable;

@Mod.EventBusSubscriber(modid = CocoJennaMod.MOD_ID)
public final class FragmentedSequenceCapability {

    public static final Capability<FragmentedSequenceData> FRAGMENTED =
            CapabilityManager.get(new CapabilityToken<>() {});

    private FragmentedSequenceCapability() {}

    public static FragmentedSequenceData getOrDefault(Entity entity) {
        return entity.getCapability(FRAGMENTED).orElse(new FragmentedSequenceData());
    }

    @SubscribeEvent
    public static void onAttach(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Villager) {
            event.addCapability(
                    new ResourceLocation(CocoJennaMod.MOD_ID, "fragmented_sequence"),
                    new Provider());
        }
    }

    private static class Provider implements ICapabilitySerializable<CompoundTag> {
        private final FragmentedSequenceData data = new FragmentedSequenceData();
        private final LazyOptional<FragmentedSequenceData> lazy = LazyOptional.of(() -> data);

        @Override
        public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
            return FRAGMENTED.orEmpty(cap, lazy);
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
