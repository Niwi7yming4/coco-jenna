package com.cocojenna.abyss;

import com.cocojenna.capability.BondData;
import com.cocojenna.capability.ModCapabilities;
import com.cocojenna.network.ModNetwork;
import com.cocojenna.network.OpenAbyssRunPacket;
import com.cocojenna.sequence.PromotionCardCatalog;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.*;

/**
 * 深淵牌局：夢境的殘響 — Roguelike 卡牌房間.
 */
public final class AbyssRunManager {

  public enum RoomType { COMBAT, REST, SHOP, BOSS }
  public enum EnemyIntent { ATTACK, DEFEND, BUFF }

  public static class Session {
    public int floor = 1;
    public int playerHp = 24;
    public int playerBlock = 0;
    public int energy = 3;
    public int enemyHp = 12;
    public int enemyBlock = 0;
    public EnemyIntent intent = EnemyIntent.ATTACK;
    public RoomType room = RoomType.COMBAT;
    public final List<String> deck = new ArrayList<>();
    public final List<String> hand = new ArrayList<>();
    public final List<String> discard = new ArrayList<>();
    public boolean playerTurn = true;
    public int rewardPending = -1;
    public boolean shopPending = false;
  }

  private static final Map<UUID, Session> SESSIONS = new HashMap<>();

  private AbyssRunManager() {}

  public static Session getSession(ServerPlayer player) {
    return SESSIONS.get(player.getUUID());
  }

  public static void startRun(ServerPlayer player) {
    BondData bond = ModCapabilities.getOrDefault(player);
    if (bond.getOwnedPromotionCards().isEmpty()) {
      player.displayClientMessage(Component.translatable("abyss.cocojenna.need_cards"), true);
      return;
    }
    Session s = new Session();
    s.deck.addAll(bond.getOwnedPromotionCards());
    if (s.deck.size() < 5) {
      for (int i = 0; i < 5; i++) s.deck.add("resonance_t9_a");
    }
    Collections.shuffle(s.deck, new Random(player.getRandom().nextLong()));
    scaleEnemy(s);
    drawToHand(s, 5);
    SESSIONS.put(player.getUUID(), s);
    sync(player, s);
    player.displayClientMessage(Component.translatable("abyss.cocojenna.enter"), true);
  }

  public static void playCard(ServerPlayer player, int handIndex) {
    Session s = SESSIONS.get(player.getUUID());
    if (s == null || s.shopPending || !s.playerTurn || handIndex < 0 || handIndex >= s.hand.size()) return;
    if (s.energy <= 0) return;

    String cardId = s.hand.remove(handIndex);
    s.discard.add(cardId);
    s.energy--;

    int tier = parseTier(cardId);
    String force = parseForce(cardId);
    int dmg = tier + (force.equals("chaos") ? 2 : force.equals("shadow") ? 1 : 0);
    int block = tier / 2 + (force.equals("resonance") ? 2 : 0);

    if (force.equals("resonance")) {
      s.playerBlock += block;
    } else {
      int dealt = Math.max(0, dmg - s.enemyBlock);
      s.enemyHp -= dealt;
      s.enemyBlock = Math.max(0, s.enemyBlock - dmg);
    }

    if (s.enemyHp <= 0) {
      onRoomCleared(player, s);
      return;
    }
    sync(player, s);
  }

  public static void endTurn(ServerPlayer player) {
    Session s = SESSIONS.get(player.getUUID());
    if (s == null || !s.playerTurn) return;
    s.playerTurn = false;
    resolveEnemy(s);
    if (s.playerHp <= 0) {
      endRun(player, false);
      return;
    }
    s.playerTurn = true;
    s.energy = 3;
    s.playerBlock = 0;
    drawToHand(s, 3);
    sync(player, s);
  }

  public static void pickReward(ServerPlayer player, int index) {
    Session s = SESSIONS.get(player.getUUID());
    if (s == null || s.rewardPending < 0) return;
    List<String> options = rewardOptions(s);
    if (index >= 0 && index < options.size()) {
      s.deck.add(options.get(index));
    }
    s.rewardPending = -1;
    advanceFloor(player, s);
  }

  public static void abandon(ServerPlayer player) {
    SESSIONS.remove(player.getUUID());
    player.displayClientMessage(Component.translatable("abyss.cocojenna.leave"), true);
  }

  /** 深淵商店：0=治療 1=移除卡牌 2=購買隨機卡（扣血）. */
  public static void shopAction(ServerPlayer player, int choice) {
    Session s = SESSIONS.get(player.getUUID());
    if (s == null || !s.shopPending) return;
    switch (choice) {
      case 0 -> s.playerHp = Math.min(28, s.playerHp + 10);
      case 1 -> {
        if (!s.deck.isEmpty()) {
          s.deck.remove(player.getRandom().nextInt(s.deck.size()));
        }
      }
      case 2 -> {
        List<String> pool = new ArrayList<>(PromotionCardCatalog.all().keySet());
        if (!pool.isEmpty()) {
          s.deck.add(pool.get(player.getRandom().nextInt(pool.size())));
          s.playerHp = Math.max(1, s.playerHp - 4);
        }
      }
      default -> {}
    }
    s.shopPending = false;
    player.displayClientMessage(Component.translatable("abyss.cocojenna.shop_done"), true);
    advanceFloor(player, s);
  }

  private static void resolveEnemy(Session s) {
    switch (s.intent) {
      case ATTACK -> {
        int dmg = 4 + s.floor / 2;
        int dealt = Math.max(0, dmg - s.playerBlock);
        s.playerHp -= dealt;
        s.playerBlock = Math.max(0, s.playerBlock - dmg);
      }
      case DEFEND -> s.enemyBlock += 5;
      case BUFF -> s.enemyHp += 3;
    }
    s.intent = EnemyIntent.values()[new Random().nextInt(3)];
  }

  private static void onRoomCleared(ServerPlayer player, Session s) {
    if (s.room == RoomType.BOSS || s.floor % 5 == 0) {
      s.rewardPending = 0;
      sync(player, s);
      return;
    }
    advanceFloor(player, s);
  }

  private static void advanceFloor(ServerPlayer player, Session s) {
    s.floor++;
    s.discard.addAll(s.hand);
    s.hand.clear();
    s.energy = 3;
    s.playerBlock = 0;
    s.playerTurn = true;
    s.shopPending = false;
    s.room = pickRoom(s.floor);
    if (s.room == RoomType.SHOP) {
      s.shopPending = true;
      s.enemyHp = 0;
      s.enemyBlock = 0;
      sync(player, s);
      player.displayClientMessage(Component.translatable("abyss.cocojenna.shop_enter"), true);
      return;
    }
    if (s.room == RoomType.REST) {
      s.playerHp = Math.min(28, s.playerHp + 8);
    }
    scaleEnemy(s);
    drawToHand(s, 5);
    if (s.floor > 50) {
      endRun(player, true);
      return;
    }
    sync(player, s);
  }

  private static RoomType pickRoom(int floor) {
    if (floor % 10 == 0) return RoomType.BOSS;
    if (floor % 4 == 0) return RoomType.REST;
    if (floor % 6 == 0) return RoomType.SHOP;
    return RoomType.COMBAT;
  }

  private static void scaleEnemy(Session s) {
    s.enemyHp = 10 + s.floor * 3 + (s.room == RoomType.BOSS ? 20 : 0);
    s.enemyBlock = 0;
  }

  private static void endRun(ServerPlayer player, boolean victory) {
    BondData bond = ModCapabilities.getOrDefault(player);
    if (victory) {
      bond.addKingdomProsperity(5);
      bond.addMemoryShard(3);
      player.displayClientMessage(Component.translatable("abyss.cocojenna.victory"), true);
    } else {
      player.displayClientMessage(Component.translatable("abyss.cocojenna.defeat"), true);
    }
    SESSIONS.remove(player.getUUID());
  }

  private static List<String> rewardOptions(Session s) {
    List<String> pool = new ArrayList<>(PromotionCardCatalog.all().keySet());
    Collections.shuffle(pool);
    return pool.subList(0, Math.min(3, pool.size()));
  }

  private static void drawToHand(Session s, int n) {
    for (int i = 0; i < n; i++) {
      if (s.deck.isEmpty()) {
        s.deck.addAll(s.discard);
        s.discard.clear();
        Collections.shuffle(s.deck);
      }
      if (!s.deck.isEmpty()) s.hand.add(s.deck.remove(0));
    }
  }

  private static int parseTier(String id) {
    try {
      int t = Integer.parseInt(id.replaceAll(".*_t(\\d+).*", "$1"));
      return 10 - t;
    } catch (Exception e) {
      return 3;
    }
  }

  private static String parseForce(String id) {
    if (id.startsWith("shadow")) return "shadow";
    if (id.startsWith("chaos")) return "chaos";
    return "resonance";
  }

  public static void sync(ServerPlayer player, Session s) {
    ModNetwork.CHANNEL.send(net.minecraftforge.network.PacketDistributor.PLAYER.with(() -> player),
        new OpenAbyssRunPacket(s));
  }
}
