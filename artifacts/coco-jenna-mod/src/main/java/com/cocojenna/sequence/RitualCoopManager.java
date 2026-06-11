package com.cocojenna.sequence;

import com.cocojenna.network.ModNetwork;
import com.cocojenna.network.RitualAidPacket;
import com.cocojenna.network.RitualGuardianStatusPacket;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraftforge.network.PacketDistributor;

import java.util.UUID;

/** 儀式注入祝福 / 共鳴 / 守護 */
public final class RitualCoopManager {

    private RitualCoopManager() {}

    public static boolean injectBlessing(ServerPlayer helper, UUID promoterId) {
        ActiveRitualSavedData data = ActiveRitualSavedData.get(helper.serverLevel());
        ActiveRitualSavedData.RitualState state = data.getActive();
        if (state == null || !state.promoter.equals(promoterId)) return false;
        if (state.blessCount >= 5) return false;
        state.blessCount++;
        state.helpers.add(helper.getUUID());
        data.setDirty();
        helper.displayClientMessage(Component.translatable("kingdom.cocojenna.ritual_bless"), true);
        ModNetwork.CHANNEL.send(PacketDistributor.ALL.noArg(),
                new RitualGuardianStatusPacket(promoterId, state.blessCount, state.resonanceSlots));
        return true;
    }

    public static int resonanceBonus(ServerPlayer promoter) {
        ActiveRitualSavedData.RitualState state = ActiveRitualSavedData.get(promoter.serverLevel()).getActive();
        if (state == null) return 0;
        return Math.min(10, state.resonanceSlots * 2);
    }

    public static void onGuardianKill(ServerPlayer killer, UUID promoterId) {
        ActiveRitualSavedData data = ActiveRitualSavedData.get(killer.serverLevel());
        ActiveRitualSavedData.RitualState state = data.getActive();
        if (state == null || !state.promoter.equals(promoterId)) return;
        state.guardianKills++;
        state.helpers.add(killer.getUUID());
        data.setDirty();
    }

    public static void grantGuardianBuff(ServerPlayer player) {
        player.addEffect(new MobEffectInstance(MobEffects.LUCK, 72000, 0));
        player.displayClientMessage(Component.translatable("kingdom.cocojenna.ritual_guardian_buff"), true);
    }
}
