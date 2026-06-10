package com.cocojenna.gamble;

import com.cocojenna.init.ModMenuTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;

public class BlackjackGambleMenu extends AbstractContainerMenu {

    private final Player player;
    private final ContainerData data;

    public BlackjackGambleMenu(int id, Inventory inv) {
        super(ModMenuTypes.BLACKJACK_GAMBLE.get(), id);
        this.player = inv.player;
        this.data = new ContainerData() {
            @Override
            public int get(int index) {
                if (!(player instanceof ServerPlayer sp)) return 0;
                BlackjackGambleManager.Session s = BlackjackGambleManager.getSession(sp);
                if (s == null) return 0;
                return switch (index) {
                    case 0 -> s.playerHand();
                    case 1 -> s.dealerHand();
                    case 2 -> s.matchRound();
                    case 3 -> s.playerMatchWins();
                    case 4 -> s.dealerMatchWins();
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {}

            @Override
            public int getCount() { return 5; }
        };
        addDataSlots(data);
    }

    public BlackjackGambleMenu(int id, Inventory inv, FriendlyByteBuf buf) {
        this(id, inv);
    }

    public void action(BlackjackGambleAction action) {
        if (player instanceof ServerPlayer sp) {
            switch (action) {
                case START -> BlackjackGambleManager.startMatch(sp);
                case HIT -> BlackjackGambleManager.hit(sp);
                case STAND -> BlackjackGambleManager.stand(sp);
            }
        }
    }

    public ContainerData getSyncData() { return data; }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    public enum BlackjackGambleAction { START, HIT, STAND }
}
