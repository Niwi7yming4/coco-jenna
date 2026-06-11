package com.cocojenna.kingdom.multiplayer;

import com.cocojenna.init.ModDimensions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.saveddata.SavedData;

import javax.annotation.Nullable;
import java.util.*;

/** 王國內閣權限 — 貓之國維度世界共享 */
public class KingdomAuthoritySavedData extends SavedData {

    private static final String DATA_NAME = "cocojenna_kingdom_authority";
    private static final long OFFLINE_ABDICATE_MS = 14L * 24 * 60 * 60 * 1000;

    @Nullable
    private UUID monarch;
    private final Map<UUID, KingdomRole> playerRoles = new HashMap<>();
    private final List<UUID> cabinetMembers = new ArrayList<>();
    private final Map<UUID, Long> lastOnlineTime = new HashMap<>();
    private final List<DecreeVote> activeVotes = new ArrayList<>();
    private final List<String> auditLog = new ArrayList<>();
    @Nullable
    private UUID pendingAbdicationVoteTarget;

    public static KingdomAuthoritySavedData get(ServerLevel level) {
        if (!level.dimension().equals(ModDimensions.CAT_KINGDOM)) {
            throw new IllegalArgumentException("KingdomAuthoritySavedData only for cat kingdom");
        }
        return level.getDataStorage().computeIfAbsent(
                KingdomAuthoritySavedData::load, KingdomAuthoritySavedData::new, DATA_NAME);
    }

    @Nullable
    public UUID getMonarch() { return monarch; }

    public KingdomRole getRole(UUID playerId) {
        if (monarch != null && monarch.equals(playerId)) return KingdomRole.MONARCH;
        return playerRoles.getOrDefault(playerId, KingdomRole.CITIZEN);
    }

    public void ensureMonarch(ServerPlayer player) {
        if (monarch != null) return;
        monarch = player.getUUID();
        playerRoles.put(player.getUUID(), KingdomRole.MONARCH);
        touchOnline(player);
        audit("monarch_elected", player.getUUID(), player.getName().getString());
        setDirty();
    }

    /** 單人伺服器：在線玩家自動成為君主 */
    public void ensureSoloMonarch(ServerPlayer player) {
        if (monarch == null) ensureMonarch(player);
    }

    public void touchOnline(ServerPlayer player) {
        lastOnlineTime.put(player.getUUID(), System.currentTimeMillis());
        setDirty();
    }

    public boolean assignRole(UUID actor, UUID target, KingdomRole role) {
        if (monarch == null || !monarch.equals(actor)) return false;
        if (role == KingdomRole.MONARCH) return false;
        if (role == KingdomRole.DECREE_ADVISOR && !cabinetMembers.contains(target)) {
            cabinetMembers.add(target);
        }
        playerRoles.put(target, role);
        audit("role_assign", target, role.name());
        setDirty();
        return true;
    }

    public boolean abdicate(UUID from, UUID to) {
        if (monarch == null || !monarch.equals(from)) return false;
        monarch = to;
        playerRoles.put(from, KingdomRole.CITIZEN);
        playerRoles.put(to, KingdomRole.MONARCH);
        audit("abdicate", to, from.toString());
        setDirty();
        return true;
    }

    public List<UUID> getCabinetMembers() { return Collections.unmodifiableList(cabinetMembers); }

    public void addAudit(String line) {
        auditLog.add(System.currentTimeMillis() + "|" + line);
        while (auditLog.size() > 200) auditLog.remove(0);
        setDirty();
    }

    private void audit(String type, UUID who, String detail) {
        addAudit(type + ":" + who + ":" + detail);
    }

    public List<DecreeVote> getActiveVotes() { return activeVotes; }

    public void tickDaily(ServerLevel level) {
        if (monarch == null) return;
        Long last = lastOnlineTime.get(monarch);
        if (last != null && System.currentTimeMillis() - last > OFFLINE_ABDICATE_MS
                && cabinetMembers.size() >= 2) {
            pendingAbdicationVoteTarget = monarch;
            audit("no_confidence_started", monarch, "offline_14d");
        }
        activeVotes.removeIf(v -> {
            if (v.resolved) return true;
            if (System.currentTimeMillis() > v.deadlineMs) {
                DecreeVoteManager.resolveVote(level, this, v);
                return true;
            }
            return false;
        });
        setDirty();
    }

    public CompoundTag toSyncTag() {
        CompoundTag tag = new CompoundTag();
        if (monarch != null) tag.putUUID("monarch", monarch);
        ListTag roles = new ListTag();
        playerRoles.forEach((uuid, role) -> {
            CompoundTag r = new CompoundTag();
            r.putUUID("id", uuid);
            r.putString("role", role.name());
            roles.add(r);
        });
        tag.put("roles", roles);
        ListTag cabinet = new ListTag();
        for (UUID u : cabinetMembers) cabinet.add(StringTag.valueOf(u.toString()));
        tag.put("cabinet", cabinet);
        ListTag votes = new ListTag();
        for (DecreeVote v : activeVotes) votes.add(v.save());
        tag.put("votes", votes);
        return tag;
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        if (monarch != null) tag.putUUID("monarch", monarch);
        ListTag roles = new ListTag();
        playerRoles.forEach((uuid, role) -> {
            CompoundTag r = new CompoundTag();
            r.putUUID("id", uuid);
            r.putString("role", role.name());
            roles.add(r);
        });
        tag.put("roles", roles);
        ListTag cabinet = new ListTag();
        for (UUID u : cabinetMembers) cabinet.add(StringTag.valueOf(u.toString()));
        tag.put("cabinet", cabinet);
        ListTag online = new ListTag();
        lastOnlineTime.forEach((uuid, t) -> {
            CompoundTag o = new CompoundTag();
            o.putUUID("id", uuid);
            o.putLong("t", t);
            online.add(o);
        });
        tag.put("lastOnline", online);
        ListTag votes = new ListTag();
        for (DecreeVote v : activeVotes) votes.add(v.save());
        tag.put("votes", votes);
        ListTag log = new ListTag();
        for (String s : auditLog) log.add(StringTag.valueOf(s));
        tag.put("audit", log);
        return tag;
    }

    public static KingdomAuthoritySavedData load(CompoundTag tag) {
        KingdomAuthoritySavedData data = new KingdomAuthoritySavedData();
        if (tag.hasUUID("monarch")) data.monarch = tag.getUUID("monarch");
        if (tag.contains("roles")) {
            for (Tag t : tag.getList("roles", Tag.TAG_COMPOUND)) {
                CompoundTag r = (CompoundTag) t;
                data.playerRoles.put(r.getUUID("id"),
                        KingdomRole.valueOf(r.getString("role")));
            }
        }
        if (tag.contains("cabinet")) {
            for (Tag t : tag.getList("cabinet", Tag.TAG_STRING)) {
                data.cabinetMembers.add(UUID.fromString(t.getAsString()));
            }
        }
        if (tag.contains("lastOnline")) {
            for (Tag t : tag.getList("lastOnline", Tag.TAG_COMPOUND)) {
                CompoundTag o = (CompoundTag) t;
                data.lastOnlineTime.put(o.getUUID("id"), o.getLong("t"));
            }
        }
        if (tag.contains("votes")) {
            for (Tag t : tag.getList("votes", Tag.TAG_COMPOUND)) {
                data.activeVotes.add(DecreeVote.load((CompoundTag) t));
            }
        }
        if (tag.contains("audit")) {
            for (Tag t : tag.getList("audit", Tag.TAG_STRING)) {
                data.auditLog.add(t.getAsString());
            }
        }
        return data;
    }
}
