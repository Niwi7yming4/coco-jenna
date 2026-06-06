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

/**
 * 玩家能力（Capability）系統。
 *
 * <p>每位玩家都附加一個 {@link BondData} 能力，儲存與可可、珍奶的所有關係數值。
 */
@Mod.EventBusSubscriber(modid = CocoJennaMod.MOD_ID)
public class ModCapabilities {

    /** BondData 能力物件 */
    @CapabilityInject(BondData.class)
    public static Capability<BondData> BOND_DATA = CapabilityManager.get(new CapabilityToken<>() {});

    public static void register() {
        CapabilityManager.get(new CapabilityToken<BondData>() {});
    }

    /** 快速取得玩家的 BondData，若不存在則返回預設值 */
    public static BondData getOrDefault(Player player) {
        return player.getCapability(BOND_DATA).orElse(new BondData());
    }

    /** 取得玩家的 BondData（Optional） */
    public static Optional<BondData> get(Player player) {
        return player.getCapability(BOND_DATA).resolve();
    }

    // ─────────────────────────────────────────────────────────────────────
    // 附加能力到玩家
    // ─────────────────────────────────────────────────────────────────────

    @SubscribeEvent
    public static void onAttachCapabilities(AttachCapabilitiesEvent<net.minecraft.world.entity.Entity> event) {
        if (event.getObject() instanceof Player player) {
            if (!player.getCapability(BOND_DATA).isPresent()) {
                event.addCapability(
                        new ResourceLocation(CocoJennaMod.MOD_ID, "bond_data"),
                        new BondDataProvider());
            }
        }
    }

    /** 玩家死亡重生後保留數據 */
    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        if (event.isWasDeath()) {
            BondData oldData = getOrDefault(event.getOriginal());
            BondData newData = getOrDefault(event.getEntity());
            newData.deserializeNBT(oldData.serializeNBT());
        }
    }

    // ─────────────────────────────────────────────────────────────────────
    // Provider & Storage
    // ─────────────────────────────────────────────────────────────────────

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
