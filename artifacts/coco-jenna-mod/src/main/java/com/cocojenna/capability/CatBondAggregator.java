package com.cocojenna.capability;

import com.cocojenna.init.ModDimensions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** 在線玩家個人好感中位數 → 更新整體 coco/jenna emotion */
public final class CatBondAggregator {

    private CatBondAggregator() {}

    public static void tick(ServerLevel level) {
        if (!level.dimension().equals(ModDimensions.CAT_KINGDOM)) return;
        if (level.getGameTime() % 200 != 0) return;
        List<Float> coco = new ArrayList<>();
        List<Float> jenna = new ArrayList<>();
        for (ServerPlayer p : level.players()) {
            BondData bond = com.cocojenna.capability.ModCapabilities.getOrDefault(p);
            coco.add(bond.getPersonalCocoAffection(p.getUUID()));
            jenna.add(bond.getPersonalJennaAffection(p.getUUID()));
        }
        if (coco.isEmpty()) return;
        float cocoMed = median(coco);
        float jennaMed = median(jenna);
        for (ServerPlayer p : level.players()) {
            BondData bond = com.cocojenna.capability.ModCapabilities.getOrDefault(p);
            bond.setCocoEmotion(cocoMed);
            bond.setJennaEmotion(jennaMed);
        }
    }

    private static float median(List<Float> vals) {
        List<Float> copy = new ArrayList<>(vals);
        Collections.sort(copy);
        int n = copy.size();
        if (n % 2 == 1) return copy.get(n / 2);
        return (copy.get(n / 2 - 1) + copy.get(n / 2)) / 2f;
    }
}
