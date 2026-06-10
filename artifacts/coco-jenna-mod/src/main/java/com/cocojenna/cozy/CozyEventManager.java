package com.cocojenna.cozy;

import com.cocojenna.capability.BondData;
import com.cocojenna.capability.ModCapabilities;
import com.cocojenna.init.ModDimensions;
import com.cocojenna.init.ModItems;
import com.cocojenna.village.VillageManager;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
/** 溫馨隨機事件（設計書 §3.1）— 每天 1–3 次. */
public final class CozyEventManager {

    private CozyEventManager() {}

    public static void tickPlayer(ServerPlayer player) {
        if (!player.level().dimension().equals(ModDimensions.CAT_KINGDOM)) return;
        if (player.tickCount % 1200 != 0) return;

        BondData bond = ModCapabilities.getOrDefault(player);
        long day = player.level().getDayTime() / 24000L;
        if (bond.getLastCozyEventDay() != day) {
            bond.setLastCozyEventDay(day);
            bond.setCozyEventsToday(0);
        }
        if (bond.getCozyEventsToday() >= 3) return;
        if (player.getRandom().nextFloat() > 0.35f) return;

        List<Event> eligible = new ArrayList<>();
        for (Event e : Event.values()) {
            if (e.canTrigger(player, bond)) eligible.add(e);
        }
        if (eligible.isEmpty()) return;

        Event pick = eligible.get(player.getRandom().nextInt(eligible.size()));
        pick.trigger(player, bond);
        bond.setCozyEventsToday(bond.getCozyEventsToday() + 1);
    }

    public enum Event {
        COCO_GIFT {
            @Override
            boolean canTrigger(ServerPlayer p, BondData b) {
                return b.getCocoEmotion() > 40 && VillageManager.isInTown(p);
            }
            @Override
            void trigger(ServerPlayer p, BondData b) {
                p.displayClientMessage(Component.translatable("cozy.cocojenna.coco_gift"), true);
                ItemStack gift = new ItemStack(ModItems.VELVET_FUR.get(), 1 + p.getRandom().nextInt(2));
                if (!p.addItem(gift)) p.drop(gift, false);
                b.modifyCocoEmotion(1f);
            }
        },
        JENNA_PRANK {
            @Override
            boolean canTrigger(ServerPlayer p, BondData b) {
                return b.getJennaPlayfulness() > 60;
            }
            @Override
            void trigger(ServerPlayer p, BondData b) {
                p.displayClientMessage(Component.translatable("cozy.cocojenna.jenna_prank"), true);
                b.modifyJennaEmotion(0.5f);
                b.modifyJennaPlayfulness(2f);
            }
        },
        AFTERNOON_SUN {
            @Override
            boolean canTrigger(ServerPlayer p, BondData b) {
                long t = p.level().getDayTime() % 24000L;
                return b.getSisterBond() > 50 && t > 5000 && t < 12000;
            }
            @Override
            void trigger(ServerPlayer p, BondData b) {
                p.displayClientMessage(Component.translatable("cozy.cocojenna.afternoon_sun"), true);
                b.modifySisterBond(1f);
            }
        },
        ROOFTOP_MEETING {
            @Override
            boolean canTrigger(ServerPlayer p, BondData b) {
                long t = p.level().getDayTime() % 24000L;
                return b.getSisterBond() > 70 && (t > 18000 || t < 2000);
            }
            @Override
            void trigger(ServerPlayer p, BondData b) {
                p.displayClientMessage(Component.translatable("cozy.cocojenna.rooftop"), true);
                b.modifySisterBond(2f);
            }
        },
        IRONPAW_TEA {
            @Override
            boolean canTrigger(ServerPlayer p, BondData b) {
                return b.isMetIronpaw() && b.getTownNpcFavor("ironpaw") > 30;
            }
            @Override
            void trigger(ServerPlayer p, BondData b) {
                p.displayClientMessage(Component.translatable("cozy.cocojenna.ironpaw_tea"), true);
                ItemStack tea = new ItemStack(ModItems.SILVERVINE_BISCUIT.get());
                if (!p.addItem(tea)) p.drop(tea, false);
                b.addTownNpcFavor("ironpaw", 2);
            }
        },
        SANHUA_NAP {
            @Override
            boolean canTrigger(ServerPlayer p, BondData b) {
                return b.getTownNpcFavor("sanhua") > 20;
            }
            @Override
            void trigger(ServerPlayer p, BondData b) {
                p.displayClientMessage(Component.translatable("cozy.cocojenna.sanhua_nap"), true);
                b.addTownNpcFavor("sanhua", 3);
            }
        },
        CHESHIRE_RIDDLE {
            @Override
            boolean canTrigger(ServerPlayer p, BondData b) {
                return p.getRandom().nextFloat() < 0.15f;
            }
            @Override
            void trigger(ServerPlayer p, BondData b) {
                int r = p.getRandom().nextInt(3);
                p.displayClientMessage(Component.translatable("cozy.cocojenna.cheshire_riddle." + r), true);
                if (!p.addItem(new ItemStack(ModItems.PURR_COIN.get(), 2))) {
                    p.drop(new ItemStack(ModItems.PURR_COIN.get(), 2), false);
                }
            }
        };

        abstract boolean canTrigger(ServerPlayer player, BondData bond);
        abstract void trigger(ServerPlayer player, BondData bond);
    }
}
