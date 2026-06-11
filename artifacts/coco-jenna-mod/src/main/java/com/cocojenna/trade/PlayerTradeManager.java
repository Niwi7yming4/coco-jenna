package com.cocojenna.trade;

import com.cocojenna.init.ModSounds;
import com.cocojenna.network.ModNetwork;
import com.cocojenna.network.TradeConfirmPacket;
import com.cocojenna.network.TradeRequestPacket;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.PacketDistributor;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/** 玩家對玩家交易 */
public final class PlayerTradeManager {

    public record TradeSession(UUID a, UUID b, ItemStack offerA, ItemStack offerB, boolean confirmA, boolean confirmB) {}

    private static final Map<UUID, TradeSession> SESSIONS = new HashMap<>();

    private PlayerTradeManager() {}

    public static void request(ServerPlayer from, ServerPlayer to) {
        TradeSession session = new TradeSession(from.getUUID(), to.getUUID(),
                ItemStack.EMPTY, ItemStack.EMPTY, false, false);
        SESSIONS.put(from.getUUID(), session);
        SESSIONS.put(to.getUUID(), session);
        ModNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> to),
                new TradeRequestPacket(from.getUUID(), from.getName().getString()));
        from.displayClientMessage(Component.translatable("kingdom.cocojenna.trade_sent", to.getName()), true);
    }

    public static boolean isTradeable(ItemStack stack) {
        if (stack.isEmpty()) return true;
        String id = net.minecraftforge.registries.ForgeRegistries.ITEMS.getKey(stack.getItem()).toString();
        if (!id.startsWith("cocojenna:")) return true;
        if (id.contains("daikatana") || id.contains("daikata") || id.contains("musou")) return false;
        if (id.contains("sequence_badge") || id.contains("promotion") || id.contains("seal_orb")) return false;
        if (id.contains("soul") || id.contains("supreme")) return false;
        if (id.contains("memory_shard") || id.contains("shadow_coin") || id.contains("catnip")) return true;
        if (id.contains("ryokatana")) return true;
        return id.contains("velvet_fur") || id.contains("memory_clay") || id.contains("purr_coin");
    }

    public static void confirm(ServerPlayer player, ItemStack offer) {
        TradeSession session = SESSIONS.get(player.getUUID());
        if (session == null) return;
        if (!isTradeable(offer)) {
            player.displayClientMessage(Component.translatable("kingdom.cocojenna.trade_blocked"), true);
            return;
        }
        ServerPlayer other = player.server.getPlayerList().getPlayer(
                session.a().equals(player.getUUID()) ? session.b() : session.a());
        if (other == null) return;
        if (session.a().equals(player.getUUID())) {
            execute(session, offer, session.offerB(), player, other);
        } else {
            execute(session, session.offerA(), offer, other, player);
        }
    }

    private static void execute(TradeSession session, ItemStack fromA, ItemStack fromB,
                                ServerPlayer a, ServerPlayer b) {
        if (!fromA.isEmpty() && !a.getInventory().hasAnyMatching(s -> ItemStack.isSameItem(s, fromA))) return;
        if (!fromB.isEmpty() && !b.getInventory().hasAnyMatching(s -> ItemStack.isSameItem(s, fromB))) return;
        if (!fromA.isEmpty()) {
            a.getInventory().removeItem(fromA.copy());
            b.getInventory().add(fromA.copy());
        }
        if (!fromB.isEmpty()) {
            b.getInventory().removeItem(fromB.copy());
            a.getInventory().add(fromB.copy());
        }
        SESSIONS.remove(a.getUUID());
        SESSIONS.remove(b.getUUID());
        a.displayClientMessage(Component.translatable("kingdom.cocojenna.trade_done"), true);
        b.displayClientMessage(Component.translatable("kingdom.cocojenna.trade_done"), true);
        a.level().playSound(null, a.blockPosition(), ModSounds.KINGDOM_TRADE_COMPLETE.get(),
                net.minecraft.sounds.SoundSource.PLAYERS, 0.7f, 1.0f);
    }
}
