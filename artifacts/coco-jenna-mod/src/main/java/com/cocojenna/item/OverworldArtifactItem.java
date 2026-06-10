package com.cocojenna.item;

import com.cocojenna.capability.ModCapabilities;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

/** 主世界遺跡文物 — 右鍵閱讀解鎖回憶. */
public class OverworldArtifactItem extends Item {

    public enum Kind {
        WARRIOR_LETTER, FARMER_DIARY, OUTPOST_BADGE
    }

    private final Kind kind;

    public OverworldArtifactItem(Properties props, Kind kind) {
        super(props);
        this.kind = kind;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level.isClientSide) return InteractionResultHolder.success(stack);
        if (!(player instanceof ServerPlayer sp)) return InteractionResultHolder.pass(stack);

        var bond = ModCapabilities.getOrDefault(sp);
        bond.addOverworldInfluence(2);
        sp.displayClientMessage(Component.translatable("penetration.cocojenna.artifact." + kind.name().toLowerCase()), true);
        return InteractionResultHolder.consume(stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> lines, TooltipFlag flag) {
        lines.add(Component.translatable("penetration.cocojenna.artifact.hint." + kind.name().toLowerCase()));
    }
}
