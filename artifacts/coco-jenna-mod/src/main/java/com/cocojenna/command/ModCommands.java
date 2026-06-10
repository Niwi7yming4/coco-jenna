package com.cocojenna.command;

import com.cocojenna.CocoJennaMod;
import com.cocojenna.capability.BondData;
import com.cocojenna.capability.ModCapabilities;
import com.cocojenna.endgame.BuildingBlueprintCatalog;
import com.cocojenna.endgame.BuildingManager;
import com.cocojenna.entity.AbstractCatEntity;
import com.cocojenna.entity.CocoEntity;
import com.cocojenna.entity.JennaEntity;
import com.cocojenna.init.ModItems;
import com.cocojenna.network.ModNetwork;
import com.cocojenna.network.OpenSkillSettingsPacket;
import com.cocojenna.village.VillageManager;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

@Mod.EventBusSubscriber(modid = CocoJennaMod.MOD_ID)
public final class ModCommands {

    private ModCommands() {}

    @SubscribeEvent
    public static void onRegister(RegisterCommandsEvent event) {
        LiteralArgumentBuilder<CommandSourceStack> skills = Commands.literal("skills")
                .requires(src -> src.isPlayer())
                .executes(ctx -> {
                    ServerPlayer player = ctx.getSource().getPlayerOrException();
                    ModNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player),
                            new OpenSkillSettingsPacket());
                    return 1;
                });
        event.getDispatcher().register(skills);

        LiteralArgumentBuilder<CommandSourceStack> build = Commands.literal("build")
                .requires(src -> src.isPlayer())
                .executes(ctx -> {
                    ServerPlayer player = ctx.getSource().getPlayerOrException();
                    player.displayClientMessage(Component.translatable("build.cocojenna.usage"), false);
                    for (var bp : BuildingBlueprintCatalog.all()) {
                        BondData bond = ModCapabilities.getOrDefault(player);
                        int prog = bond.getBuildingProgress(bp.id());
                        String status = bond.isBuildingPlaced(bp.id()) ? "✓" : prog + "/" + bp.requiredProgress();
                        player.displayClientMessage(Component.literal("  " + bp.id() + " — " + status), false);
                    }
                    return 1;
                })
                .then(Commands.argument("id", StringArgumentType.string())
                        .executes(ctx -> {
                            ServerPlayer player = ctx.getSource().getPlayerOrException();
                            String id = StringArgumentType.getString(ctx, "id");
                            BuildingBlueprintCatalog.Blueprint bp = BuildingBlueprintCatalog.get(id);
                            if (bp == null) {
                                player.displayClientMessage(Component.translatable("build.cocojenna.unknown", id), true);
                                return 0;
                            }
                            BondData bond = ModCapabilities.getOrDefault(player);
                            if (bond.getBuildingProgress(id) < bp.requiredProgress()) {
                                player.displayClientMessage(Component.translatable("build.cocojenna.not_ready",
                                        bond.getBuildingProgress(id), bp.requiredProgress()), true);
                                return 0;
                            }
                            if (bond.isBuildingPlaced(id)) {
                                player.displayClientMessage(Component.translatable("build.cocojenna.already"), true);
                                return 0;
                            }
                            BuildingManager.place(player, id);
                            return 1;
                        }));
        event.getDispatcher().register(build);

        event.getDispatcher().register(Commands.literal("village")
                .requires(src -> src.isPlayer())
                .executes(ctx -> {
                    VillageManager.showPanel(ctx.getSource().getPlayerOrException());
                    return 1;
                }));

        event.getDispatcher().register(Commands.literal("groom")
                .requires(src -> src.isPlayer())
                .then(Commands.argument("target", StringArgumentType.word())
                        .executes(ctx -> {
                            ServerPlayer player = ctx.getSource().getPlayerOrException();
                            String target = StringArgumentType.getString(ctx, "target");
                            return doGroom(player, target);
                        })));

        event.getDispatcher().register(Commands.literal("nap")
                .requires(src -> src.isPlayer())
                .executes(ctx -> {
                    ServerPlayer player = ctx.getSource().getPlayerOrException();
                    BondData bond = ModCapabilities.getOrDefault(player);
                    long now = player.level().getGameTime();
                    if (now < bond.getNapCooldownUntil()) {
                        player.displayClientMessage(net.minecraft.network.chat.Component
                                .translatable("cozy.cocojenna.nap_cooldown"), true);
                        return 0;
                    }
                    if (!player.level().getBlockState(player.blockPosition().below()).is(
                            com.cocojenna.init.ModBlocks.CAT_BED.get())) {
                        player.displayClientMessage(net.minecraft.network.chat.Component
                                .translatable("cozy.cocojenna.nap_need_bed"), true);
                        return 0;
                    }
                    bond.setNapCooldownUntil(now + 24000);
                    bond.modifyCocoEmotion(2f);
                    bond.modifyJennaEmotion(2f);
                    player.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                            net.minecraft.world.effect.MobEffects.REGENERATION, 6000, 2));
                    player.displayClientMessage(net.minecraft.network.chat.Component
                            .translatable("cozy.cocojenna.nap_ok"), true);
                    return 1;
                }));
    }

    private static int doGroom(ServerPlayer player, String target) {
        BondData bond = ModCapabilities.getOrDefault(player);
        long now = player.level().getGameTime();
        if (now < bond.getGroomCooldownUntil()) {
            player.displayClientMessage(net.minecraft.network.chat.Component
                    .translatable("cozy.cocojenna.groom_cooldown"), true);
            return 0;
        }
        AbstractCatEntity cat = null;
        if ("coco".equalsIgnoreCase(target) || "可可".equals(target)) {
            cat = player.level().getEntitiesOfClass(CocoEntity.class, player.getBoundingBox().inflate(6),
                    c -> player.getUUID().equals(c.getOwnerUUID())).stream().findFirst().orElse(null);
        } else if ("jenna".equalsIgnoreCase(target) || "珍奶".equals(target)) {
            cat = player.level().getEntitiesOfClass(JennaEntity.class, player.getBoundingBox().inflate(6),
                    c -> player.getUUID().equals(c.getOwnerUUID())).stream().findFirst().orElse(null);
        }
        if (cat == null) {
            player.displayClientMessage(net.minecraft.network.chat.Component
                    .translatable("cozy.cocojenna.groom_no_cat"), true);
            return 0;
        }
        var brush = ModItems.GROOMING_BRUSH.get();
        var stack = player.getInventory().items.stream()
                .filter(s -> s.getItem() == brush).findFirst().orElse(net.minecraft.world.item.ItemStack.EMPTY);
        if (stack.isEmpty()) {
            player.displayClientMessage(net.minecraft.network.chat.Component
                    .translatable("cozy.cocojenna.groom_need_brush"), true);
            return 0;
        }
        var gb = (com.cocojenna.item.GroomingBrushItem) ModItems.GROOMING_BRUSH.get();
        if (gb.groom(cat, player, stack)) {
            bond.setGroomCooldownUntil(now + 24000);
            if (!player.addItem(new net.minecraft.world.item.ItemStack(ModItems.VELVET_FUR.get(), 2))) {
                player.drop(new net.minecraft.world.item.ItemStack(ModItems.VELVET_FUR.get(), 2), false);
            }
            player.displayClientMessage(net.minecraft.network.chat.Component
                    .translatable("cozy.cocojenna.groom_ok", target), true);
            return 1;
        }
        player.displayClientMessage(net.minecraft.network.chat.Component
                .translatable("cozy.cocojenna.groom_fail"), true);
        return 0;
    }
}
