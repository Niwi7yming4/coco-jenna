package com.cocojenna.command;

import com.cocojenna.CocoJennaMod;
import com.cocojenna.kingdom.multiplayer.*;
import com.cocojenna.network.DecreeProposalPacket;
import com.cocojenna.network.DecreeVotePacket;
import com.cocojenna.network.ModNetwork;
import com.cocojenna.network.RoleAssignPacket;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.UUID;

@Mod.EventBusSubscriber(modid = CocoJennaMod.MOD_ID)
public final class KingdomCommand {

    private KingdomCommand() {}

    @SubscribeEvent
    public static void register(RegisterCommandsEvent event) {
        LiteralArgumentBuilder<CommandSourceStack> root = Commands.literal("kingdom")
                .requires(src -> src.hasPermission(0));

        root.then(Commands.literal("abdicate")
                .then(Commands.argument("target", EntityArgument.player())
                        .executes(ctx -> {
                            ServerPlayer from = ctx.getSource().getPlayerOrException();
                            ServerPlayer to = EntityArgument.getPlayer(ctx, "target");
                            KingdomAuthoritySavedData auth = KingdomAuthoritySavedData.get(from.serverLevel());
                            if (auth.abdicate(from.getUUID(), to.getUUID())) {
                                KingdomAuthorityManager.syncToAll(from.serverLevel());
                                ctx.getSource().sendSuccess(() -> Component.translatable(
                                        "kingdom.cocojenna.abdicated", to.getName()), true);
                                return 1;
                            }
                            return 0;
                        })));

        root.then(Commands.literal("assign")
                .then(Commands.argument("role", StringArgumentType.string())
                        .then(Commands.argument("target", EntityArgument.player())
                                .executes(ctx -> {
                                    ServerPlayer actor = ctx.getSource().getPlayerOrException();
                                    ServerPlayer target = EntityArgument.getPlayer(ctx, "target");
                                    String role = StringArgumentType.getString(ctx, "role");
                                    ModNetwork.CHANNEL.sendToServer(new RoleAssignPacket(
                                            target.getUUID(), role.toUpperCase(), false));
                                    return 1;
                                }))));

        root.then(Commands.literal("propose")
                .then(Commands.argument("text", StringArgumentType.greedyString())
                        .executes(ctx -> {
                            ServerPlayer player = ctx.getSource().getPlayerOrException();
                            ModNetwork.CHANNEL.sendToServer(new DecreeProposalPacket(
                                    StringArgumentType.getString(ctx, "text")));
                            return 1;
                        })));

        root.then(Commands.literal("vote")
                .then(Commands.argument("id", StringArgumentType.string())
                        .then(Commands.argument("yes", BoolArgumentType.bool())
                                .executes(ctx -> {
                                    ModNetwork.CHANNEL.sendToServer(new DecreeVotePacket(
                                            StringArgumentType.getString(ctx, "id"),
                                            BoolArgumentType.getBool(ctx, "yes")));
                                    return 1;
                                }))));

        root.then(Commands.literal("leaderboard")
                .executes(ctx -> {
                    ServerPlayer player = ctx.getSource().getPlayerOrException();
                    KingdomLeaderboardManager.show(player);
                    return 1;
                }));

        root.then(Commands.literal("trade")
                .then(Commands.argument("target", EntityArgument.player())
                        .executes(ctx -> {
                            ServerPlayer from = ctx.getSource().getPlayerOrException();
                            ServerPlayer to = EntityArgument.getPlayer(ctx, "target");
                            com.cocojenna.trade.PlayerTradeManager.request(from, to);
                            return 1;
                        })));

        event.getDispatcher().register(root);
    }
}
