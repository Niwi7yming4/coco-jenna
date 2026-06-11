package com.cocojenna.kingdom.multiplayer;

import java.util.EnumSet;
import java.util.Set;

/** 王國內閣職位（我的王國需要多人 2.1.1） */
public enum KingdomRole {
    MONARCH,
    ARCHITECT,
    TREASURER,
    CATNIP_MINISTER,
    FESTIVAL_MASTER,
    DECREE_ADVISOR,
    GUARD_CAPTAIN,
    CITIZEN;

    public Set<Permission> defaultPermissions() {
        return switch (this) {
            case MONARCH -> EnumSet.allOf(Permission.class);
            case ARCHITECT -> EnumSet.of(Permission.BUILD, Permission.DESTROY);
            case TREASURER -> EnumSet.of(Permission.ACCESS_VAULT, Permission.SET_TAX, Permission.MANAGE_MARKET);
            case CATNIP_MINISTER -> EnumSet.of(Permission.MANAGE_PLANT, Permission.MANAGE_MARKET);
            case FESTIVAL_MASTER -> EnumSet.of(Permission.START_FESTIVAL);
            case DECREE_ADVISOR -> EnumSet.of(Permission.DECREE_VOTE, Permission.DECREE_PROPOSE);
            case GUARD_CAPTAIN -> EnumSet.of(Permission.RECRUIT_GUARD);
            case CITIZEN -> EnumSet.noneOf(Permission.class);
        };
    }
}
