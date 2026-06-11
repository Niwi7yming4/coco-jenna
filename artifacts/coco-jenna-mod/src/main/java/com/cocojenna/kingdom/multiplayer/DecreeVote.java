package com.cocojenna.kingdom.multiplayer;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/** 進行中的法案投票 */
public final class DecreeVote {

    public final String id;
    public final UUID proposer;
    public final String proposal;
    public final long deadlineMs;
    public final Map<UUID, Boolean> votes = new HashMap<>();
    public boolean monarchApproved = false;
    public boolean monarchVeto = false;
    public boolean resolved;

    public DecreeVote(String id, UUID proposer, String proposal, long deadlineMs) {
        this.id = id;
        this.proposer = proposer;
        this.proposal = proposal;
        this.deadlineMs = deadlineMs;
    }

    public CompoundTag save() {
        CompoundTag tag = new CompoundTag();
        tag.putString("id", id);
        tag.putUUID("proposer", proposer);
        tag.putString("proposal", proposal);
        tag.putLong("deadline", deadlineMs);
        tag.putBoolean("monarchOk", monarchApproved);
        tag.putBoolean("monarchVeto", monarchVeto);
        tag.putBoolean("resolved", resolved);
        ListTag list = new ListTag();
        votes.forEach((uuid, yes) -> {
            CompoundTag v = new CompoundTag();
            v.putUUID("who", uuid);
            v.putBoolean("yes", yes);
            list.add(v);
        });
        tag.put("votes", list);
        return tag;
    }

    public static DecreeVote load(CompoundTag tag) {
        DecreeVote v = new DecreeVote(
                tag.getString("id"),
                tag.getUUID("proposer"),
                tag.getString("proposal"),
                tag.getLong("deadline"));
        v.monarchApproved = tag.getBoolean("monarchOk");
        v.monarchVeto = tag.getBoolean("monarchVeto");
        v.resolved = tag.getBoolean("resolved");
        if (tag.contains("votes")) {
            for (Tag t : tag.getList("votes", Tag.TAG_COMPOUND)) {
                CompoundTag c = (CompoundTag) t;
                v.votes.put(c.getUUID("who"), c.getBoolean("yes"));
            }
        }
        return v;
    }
}
