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

/** 悲傷遺物（設計書 卷四 §5.2）. */
public class BlackMudRelicItem extends Item {

    public enum RelicKind {
        UNSENT_LETTER, RUSTED_BELL, HALF_SCARF, FADED_COLLAR, PURE_DROP
    }

    private final RelicKind kind;

    public BlackMudRelicItem(Properties props, RelicKind kind) {
        super(props);
        this.kind = kind;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level.isClientSide) return InteractionResultHolder.success(stack);
        if (!(player instanceof ServerPlayer sp)) return InteractionResultHolder.pass(stack);

        var bond = ModCapabilities.getOrDefault(sp);
        switch (kind) {
            case UNSENT_LETTER -> {
                bond.addKingdomProsperity(5);
                sp.displayClientMessage(Component.translatable("blackmud.cocojenna.relic.unsent"), true);
            }
            case RUSTED_BELL -> {
                bond.modifyCocoEmotion(2f);
                bond.modifyJennaEmotion(2f);
                sp.displayClientMessage(Component.translatable("blackmud.cocojenna.relic.bell"), true);
            }
            case HALF_SCARF -> {
                bond.addTownNpcFavor("sanhua", 20);
                sp.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                        com.cocojenna.init.ModEffects.WARM_SERENITY.get(), 3600, 0));
                sp.displayClientMessage(Component.translatable("blackmud.cocojenna.relic.scarf"), true);
            }
            case FADED_COLLAR -> {
                bond.modifyCocoEmotion(5f);
                bond.addKingdomHappiness(8);
                sp.displayClientMessage(Component.translatable("blackmud.cocojenna.relic.collar"), true);
            }
            case PURE_DROP -> {
                for (var p : com.cocojenna.endgame.kingdom.TownNpcProfile.ALL) {
                    if (bond.isTownNpcRecruited(p.id())) bond.addTownNpcFavor(p.id(), 10);
                }
                bond.addKingdomHappiness(15);
                sp.displayClientMessage(Component.translatable("blackmud.cocojenna.relic.pure"), true);
            }
        }
        stack.shrink(1);
        return InteractionResultHolder.consume(stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> tip, TooltipFlag flag) {
        tip.add(Component.translatable("blackmud.cocojenna.relic." + kind.name().toLowerCase() + ".lore"));
        tip.add(Component.translatable("blackmud.cocojenna.relic.use_hint"));
    }

}
