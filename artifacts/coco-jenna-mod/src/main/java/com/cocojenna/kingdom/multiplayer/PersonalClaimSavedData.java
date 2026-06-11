package com.cocojenna.kingdom.multiplayer;

import com.cocojenna.init.ModDimensions;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

import javax.annotation.Nullable;
import java.util.*;

/** 個人領地 16×16（最多 3 區 / 最小間距 64） */
public class PersonalClaimSavedData extends SavedData {

    private static final String DATA_NAME = "cocojenna_personal_claims";
    public static final int CLAIM_SIZE = 16;
    public static final int MIN_DISTANCE = 64;
    public static final int MAX_CLAIMS = 3;

    public record Claim(UUID owner, BlockPos anchor, Set<UUID> guests) {}

    private final List<Claim> claims = new ArrayList<>();

    public static PersonalClaimSavedData get(ServerLevel level) {
        if (!level.dimension().equals(ModDimensions.CAT_KINGDOM)) {
            throw new IllegalArgumentException("PersonalClaimSavedData only for cat kingdom");
        }
        return level.getDataStorage().computeIfAbsent(
                PersonalClaimSavedData::load, PersonalClaimSavedData::new, DATA_NAME);
    }

    public List<Claim> allClaims() { return Collections.unmodifiableList(claims); }

    @Nullable
    public Claim at(BlockPos pos) {
        for (Claim c : claims) {
            if (contains(c, pos)) return c;
        }
        return null;
    }

    public boolean canBuild(UUID playerId, BlockPos pos) {
        Claim c = at(pos);
        if (c == null) return true;
        if (c.owner().equals(playerId)) return true;
        return c.guests().contains(playerId);
    }

    public boolean tryPlaceClaim(UUID owner, BlockPos anchor) {
        long count = claims.stream().filter(c -> c.owner().equals(owner)).count();
        if (count >= MAX_CLAIMS) return false;
        for (Claim existing : claims) {
            if (distanceSq(existing.anchor(), anchor) < MIN_DISTANCE * MIN_DISTANCE) return false;
        }
        claims.add(new Claim(owner, anchor.immutable(), new HashSet<>()));
        setDirty();
        return true;
    }

    public boolean addGuest(BlockPos anchor, UUID guest) {
        for (int i = 0; i < claims.size(); i++) {
            Claim c = claims.get(i);
            if (c.anchor().equals(anchor)) {
                Set<UUID> g = new HashSet<>(c.guests());
                g.add(guest);
                claims.set(i, new Claim(c.owner(), c.anchor(), g));
                setDirty();
                return true;
            }
        }
        return false;
    }

    private static boolean contains(Claim c, BlockPos pos) {
        int dx = pos.getX() - c.anchor().getX();
        int dz = pos.getZ() - c.anchor().getZ();
        return dx >= 0 && dx < CLAIM_SIZE && dz >= 0 && dz < CLAIM_SIZE;
    }

    private static long distanceSq(BlockPos a, BlockPos b) {
        long dx = a.getX() - b.getX();
        long dz = a.getZ() - b.getZ();
        return dx * dx + dz * dz;
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        ListTag list = new ListTag();
        for (Claim c : claims) {
            CompoundTag t = new CompoundTag();
            t.putUUID("owner", c.owner());
            t.putInt("x", c.anchor().getX());
            t.putInt("y", c.anchor().getY());
            t.putInt("z", c.anchor().getZ());
            ListTag guests = new ListTag();
            c.guests().forEach(u -> guests.add(net.minecraft.nbt.StringTag.valueOf(u.toString())));
            t.put("guests", guests);
            list.add(t);
        }
        tag.put("claims", list);
        return tag;
    }

    public static PersonalClaimSavedData load(CompoundTag tag) {
        PersonalClaimSavedData data = new PersonalClaimSavedData();
        if (!tag.contains("claims")) return data;
        for (Tag t : tag.getList("claims", Tag.TAG_COMPOUND)) {
            CompoundTag c = (CompoundTag) t;
            Set<UUID> guests = new HashSet<>();
            if (c.contains("guests")) {
                for (Tag g : c.getList("guests", Tag.TAG_STRING)) {
                    guests.add(UUID.fromString(g.getAsString()));
                }
            }
            data.claims.add(new Claim(
                    c.getUUID("owner"),
                    new BlockPos(c.getInt("x"), c.getInt("y"), c.getInt("z")),
                    guests));
        }
        return data;
    }
}
