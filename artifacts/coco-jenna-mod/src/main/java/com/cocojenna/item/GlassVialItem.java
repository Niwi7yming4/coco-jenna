package com.cocojenna.item;

import com.cocojenna.entity.SealedEntity;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;

/** 玻璃瓶 — 對封印物使用以手動採集（封印物也會自動被拾取） */
public class GlassVialItem extends Item {

    public GlassVialItem(Properties props) {
        super(props);
    }
}
