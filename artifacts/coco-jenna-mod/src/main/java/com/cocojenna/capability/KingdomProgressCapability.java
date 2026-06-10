package com.cocojenna.capability;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/** 終局王國、聲望、貨幣與建築進度（從 BondData 水平拆分）. */
public final class KingdomProgressCapability {

    private boolean endgameUnlocked;
    private int kingdomProsperity;
    private int kingdomHappiness = 50;
    private int kingdomStability = 50;
    private int kingdomReputation;
    private int buildCreativity;
    private String kingdomDecree = "";
    private int ironpawForgeLevel = 1;
    private int remnantBurned;
    private int shadowCoins;
    private int repGearTown;
    private int repRoyal;
    private int repDawn;
    private int repBlindPort;
    private int repFirstCry;
    private final Set<String> purchasedRepOffers = new HashSet<>();
    private final Map<String, Integer> buildingProgress = new HashMap<>();
    private final Set<String> buildingsPlaced = new HashSet<>();

    public void copyFrom(BondData bond) {
        endgameUnlocked = bond.isEndgameUnlocked();
        kingdomProsperity = bond.getKingdomProsperity();
        kingdomHappiness = bond.getKingdomHappiness();
        kingdomStability = bond.getKingdomStability();
        kingdomReputation = bond.getKingdomReputation();
        buildCreativity = bond.getBuildCreativity();
        kingdomDecree = bond.getKingdomDecree();
        ironpawForgeLevel = bond.getIronpawForgeLevel();
        remnantBurned = bond.getRemnantBurned();
        shadowCoins = bond.getShadowCoins();
        repGearTown = bond.getReputation("gear_town");
        repRoyal = bond.getReputation("royal");
        repDawn = bond.getReputation("dawn");
        repBlindPort = bond.getReputation("blind_port");
        repFirstCry = bond.getReputation("first_cry");
        purchasedRepOffers.clear();
        purchasedRepOffers.addAll(bond.getPurchasedRepOfferIds());
        buildingProgress.clear();
        buildingProgress.putAll(bond.snapshotBuildingProgress());
        buildingsPlaced.clear();
        buildingsPlaced.addAll(bond.snapshotBuildingsPlaced());
    }

    public void applyTo(BondData bond) {
        bond.setEndgameUnlocked(endgameUnlocked);
        bond.setKingdomProsperity(kingdomProsperity);
        bond.setKingdomHappiness(kingdomHappiness);
        bond.setKingdomStabilityDirect(kingdomStability);
        bond.setKingdomReputationDirect(kingdomReputation);
        bond.setBuildCreativityDirect(buildCreativity);
        bond.setKingdomDecree(kingdomDecree);
        bond.setIronpawForgeLevel(ironpawForgeLevel);
        bond.setRemnantBurned(remnantBurned);
        bond.setShadowCoins(shadowCoins);
        bond.setReputation("gear_town", repGearTown);
        bond.setReputation("royal", repRoyal);
        bond.setReputation("dawn", repDawn);
        bond.setReputation("blind_port", repBlindPort);
        bond.setReputation("first_cry", repFirstCry);
        bond.replacePurchasedRepOffers(purchasedRepOffers);
        bond.replaceBuildingProgress(buildingProgress);
        bond.replaceBuildingsPlaced(buildingsPlaced);
    }

    public CompoundTag serialize() {
        CompoundTag tag = new CompoundTag();
        tag.putBoolean("endgameUnlocked", endgameUnlocked);
        tag.putInt("kingdomProsperity", kingdomProsperity);
        tag.putInt("kingdomHappiness", kingdomHappiness);
        tag.putInt("kingdomStability", kingdomStability);
        tag.putInt("kingdomReputation", kingdomReputation);
        tag.putInt("buildCreativity", buildCreativity);
        tag.putString("kingdomDecree", kingdomDecree);
        tag.putInt("ironpawForgeLevel", ironpawForgeLevel);
        tag.putInt("remnantBurned", remnantBurned);
        tag.putInt("shadowCoins", shadowCoins);
        tag.putInt("repGearTown", repGearTown);
        tag.putInt("repRoyal", repRoyal);
        tag.putInt("repDawn", repDawn);
        tag.putInt("repBlindPort", repBlindPort);
        tag.putInt("repFirstCry", repFirstCry);
        ListTag repShop = new ListTag();
        for (String id : purchasedRepOffers) repShop.add(StringTag.valueOf(id));
        tag.put("purchasedRepOffers", repShop);
        CompoundTag buildings = new CompoundTag();
        buildingProgress.forEach(buildings::putInt);
        tag.put("buildingProgress", buildings);
        ListTag placed = new ListTag();
        for (String id : buildingsPlaced) placed.add(StringTag.valueOf(id));
        tag.put("buildingsPlaced", placed);
        return tag;
    }

    public void deserialize(CompoundTag tag) {
        if (tag == null || tag.isEmpty()) return;
        endgameUnlocked = tag.getBoolean("endgameUnlocked");
        kingdomProsperity = tag.getInt("kingdomProsperity");
        kingdomHappiness = tag.getInt("kingdomHappiness");
        kingdomStability = tag.getInt("kingdomStability");
        kingdomReputation = tag.getInt("kingdomReputation");
        buildCreativity = tag.getInt("buildCreativity");
        kingdomDecree = tag.getString("kingdomDecree");
        ironpawForgeLevel = tag.getInt("ironpawForgeLevel");
        remnantBurned = tag.getInt("remnantBurned");
        shadowCoins = tag.getInt("shadowCoins");
        repGearTown = tag.getInt("repGearTown");
        repRoyal = tag.getInt("repRoyal");
        repDawn = tag.getInt("repDawn");
        repBlindPort = tag.getInt("repBlindPort");
        repFirstCry = tag.getInt("repFirstCry");
        purchasedRepOffers.clear();
        if (tag.contains("purchasedRepOffers")) {
            for (Tag t : tag.getList("purchasedRepOffers", Tag.TAG_STRING)) {
                purchasedRepOffers.add(t.getAsString());
            }
        }
        buildingProgress.clear();
        if (tag.contains("buildingProgress")) {
            CompoundTag buildings = tag.getCompound("buildingProgress");
            for (String key : buildings.getAllKeys()) {
                buildingProgress.put(key, buildings.getInt(key));
            }
        }
        buildingsPlaced.clear();
        if (tag.contains("buildingsPlaced")) {
            for (Tag t : tag.getList("buildingsPlaced", Tag.TAG_STRING)) {
                buildingsPlaced.add(t.getAsString());
            }
        }
    }
}
