package com.cocojenna.endgame.kingdom;

import com.cocojenna.capability.BondData;
import com.cocojenna.capability.ModCapabilities;
import com.cocojenna.endgame.AfterRainGameplayManager;
import com.cocojenna.init.ModItems;
import com.cocojenna.init.ModDimensions;
import com.cocojenna.network.ModNetwork;
import com.cocojenna.network.SyncBondDataPacket;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.PacketDistributor;

/** 四季慶典：初啼節／日照節／豐收節／安眠節（雨後 Ch.13.3）. */
public final class SeasonalFestivalManager {

    public static final int DAYS_PER_SEASON = 32;
    public static final int DAYS_PER_MOON_CYCLE = 8;

    private SeasonalFestivalManager() {}

    public static void tickDaily(ServerPlayer player) {
        if (!AfterRainGameplayManager.isPeaceMode(player)) return;
        BondData bond = ModCapabilities.getOrDefault(player);
        long day = player.level().getDayTime() / 24000L;
        if (bond.getKingdomCalendarDay() >= day) return;
        bond.setKingdomCalendarDay(day);

        int season = (int) ((day / DAYS_PER_SEASON) % 4);
        int weekInSeason = (int) ((day % DAYS_PER_SEASON) / 8);
        bond.setKingdomSeason(season);

        if (day > 0 && day % DAYS_PER_MOON_CYCLE == 0 && bond.getFestivalPhase() == 0) {
            bond.setFestivalPrepDay(7);
        }

        trySeasonalEvent(player, bond, season, weekInSeason, day);
        tryDailyRandomEvent(player, bond, day);
    }

    private static void trySeasonalEvent(ServerPlayer player, BondData bond,
            int season, int weekInSeason, long day) {
        String id = switch (season) {
            case 0 -> weekInSeason == 0 ? "spring_first_meow" : null;
            case 1 -> weekInSeason == 1 ? "summer_sunbeams" : null;
            case 2 -> weekInSeason == 2 ? "autumn_plenty" : null;
            case 3 -> weekInSeason == 3 ? "winter_rest" : null;
            default -> null;
        };
        if (id == null) return;
        int dayInSeason = (int) (day % DAYS_PER_SEASON);
        if (dayInSeason != weekInSeason * 8) return;
        String key = id + "_s" + (day / DAYS_PER_SEASON);
        if (key.equals(bond.getLastSeasonalFestival())) return;
        bond.setLastSeasonalFestival(key);
        runSeasonal(player, bond, id);
    }

    private static void runSeasonal(ServerPlayer player, BondData bond, String id) {
        switch (id) {
            case "spring_first_meow" -> {
                bond.addKingdomHappiness(8);
                bond.modifyCocoEmotion(3f);
                bond.addKingdomProsperity(5);
                unlockSeasonRecipe(player, bond, 0, "spring_blossom_parfait");
                grantRecipeHint(player, "spring_furniture");
            }
            case "summer_sunbeams" -> {
                bond.addKingdomHappiness(10);
                bond.modifyJennaEmotion(4f);
                bond.modifyJennaPlayfulness(2f);
                unlockSeasonRecipe(player, bond, 1, "summer_sun_sorbet");
                grantRecipeHint(player, "summer_toy");
            }
            case "autumn_plenty" -> {
                bond.addKingdomHappiness(12);
                bond.addKingdomProsperity(10);
                bond.addVillageFoodStock(15);
                unlockSeasonRecipe(player, bond, 2, "autumn_harvest_stew");
                grantRecipeHint(player, "autumn_ingredient");
            }
            case "winter_rest" -> {
                bond.addKingdomStability(8);
                bond.modifySisterBond(3f);
                player.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                        com.cocojenna.init.ModEffects.WARM_SERENITY.get(), 4800, 0));
                unlockSeasonRecipe(player, bond, 3, "winter_warm_broth");
                grantRecipeHint(player, "winter_furniture");
            }
            default -> { return; }
        }
        player.displayClientMessage(Component.translatable("season.cocojenna." + id)
                .withStyle(ChatFormatting.LIGHT_PURPLE), false);
        spawnSeasonParade(player, id);
        ModNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player),
                new SyncBondDataPacket(bond.serializeNBT()));
    }

    private static void spawnSeasonParade(ServerPlayer player, String id) {
        if (!player.level().dimension().equals(ModDimensions.CAT_KINGDOM)) return;
        if (!(player.level() instanceof ServerLevel sl)) return;
        var pos = player.blockPosition();
        var particle = switch (id) {
            case "spring_first_meow" -> ParticleTypes.HAPPY_VILLAGER;
            case "summer_sunbeams" -> ParticleTypes.FLAME;
            case "autumn_plenty" -> ParticleTypes.COMPOSTER;
            case "winter_rest" -> ParticleTypes.SNOWFLAKE;
            default -> ParticleTypes.END_ROD;
        };
        sl.sendParticles(particle, pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5,
                48, 6.0, 1.5, 6.0, 0.04);
        bondAddParadeHappiness(player, id);
    }

    private static void bondAddParadeHappiness(ServerPlayer player, String id) {
        BondData bond = ModCapabilities.getOrDefault(player);
        if ("autumn_plenty".equals(id)) {
            bond.addVillageFoodStock(5);
        }
        if ("winter_rest".equals(id)) {
            bond.addNpcFatigue(-8);
        }
    }

    private static void unlockSeasonRecipe(ServerPlayer player, BondData bond, int season, String recipeId) {
        com.cocojenna.endgame.CookingRecipeRegistry.unlockSeasonal(bond, season);
        bond.unlockCookingRecipe(recipeId);
        player.displayClientMessage(Component.translatable("season.cocojenna.recipe_unlocked",
                Component.translatable("food.cocojenna." + recipeId)), true);
    }

    private static void grantRecipeHint(ServerPlayer player, String recipeKey) {
        ItemStack gift = switch (recipeKey) {
            case "spring_furniture" -> new ItemStack(ModItems.HIBISCUS_FLOWER_ITEM.get(), 4);
            case "summer_toy" -> new ItemStack(ModItems.CATNIP_ITEM.get(), 6);
            case "autumn_ingredient" -> new ItemStack(ModItems.BASIC_FISH_PUREE.get(), 4);
            default -> new ItemStack(ModItems.VELVET_FUR.get(), 8);
        };
        if (!player.addItem(gift)) player.drop(gift, false);
    }

    /** Ch.27 小型隨機事件 */
    private static void tryDailyRandomEvent(ServerPlayer player, BondData bond, long day) {
        if (player.getRandom().nextFloat() > 0.12f) return;
        int roll = player.getRandom().nextInt(5);
        switch (roll) {
            case 0 -> {
                bond.addKingdomHappiness(2);
                player.displayClientMessage(Component.translatable("season.cocojenna.random.furball"), true);
            }
            case 1 -> {
                bond.addTownNpcFavor("sanhua", 3);
                player.displayClientMessage(Component.translatable("season.cocojenna.random.gift"), true);
            }
            case 2 -> bond.addKingdomProsperity(2);
            case 3 -> bond.modifyCocoEmotion(1f);
            default -> bond.modifyJennaEmotion(1f);
        }
    }
}
