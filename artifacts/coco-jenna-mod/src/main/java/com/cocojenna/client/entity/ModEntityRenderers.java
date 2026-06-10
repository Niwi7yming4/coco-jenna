package com.cocojenna.client.entity;

import com.cocojenna.CocoJennaMod;
import com.cocojenna.entity.*;
import com.cocojenna.init.ModEntities;
import com.cocojenna.init.ModItems;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = CocoJennaMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModEntityRenderers {

    public static final ResourceLocation TEX_ALL_BLACK = cat("coco");
    public static final ResourceLocation TEX_CALICO = cat("jenna");

    private static ResourceLocation cat(String name) {
        return new ResourceLocation(CocoJennaMod.MOD_ID, "textures/entity/cat/" + name + ".png");
    }

    private static EnhancedCatRenderer.CatStyle layered(String tex, float scale, float hr, float hg, float hb) {
        return EnhancedCatRenderer.CatStyle.layered(cat(tex), scale, hr, hg, hb);
    }

    @SubscribeEvent
    public static void register(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.COCO.get(),
                ctx -> new EnhancedCatRenderer<>(ctx, layered("coco", 0.94f, 1f, 0.92f, 0.75f)));
        event.registerEntityRenderer(ModEntities.JENNA.get(),
                ctx -> new EnhancedCatRenderer<>(ctx, layered("jenna", 0.84f, 1f, 0.98f, 0.7f)));

        event.registerEntityRenderer(ModEntities.SAMURAI_CAT.get(),
                ctx -> new SamuraiCatRenderer(ctx, layered("samurai", 1.02f, 0.9f, 0.88f, 0.85f)));
        event.registerEntityRenderer(ModEntities.SUMO_CAT.get(),
                ctx -> new EnhancedCatRenderer<>(ctx, layered("big_orange", 1.38f, 1f, 0.95f, 0.8f)));
        event.registerEntityRenderer(ModEntities.COURT_LADY_CAT.get(),
                ctx -> new EnhancedCatRenderer<>(ctx, layered("court_lady", 0.92f, 1f, 0.9f, 0.95f)));
        event.registerEntityRenderer(ModEntities.MONK_CAT.get(),
                ctx -> new EnhancedCatRenderer<>(ctx, layered("monk", 0.96f, 0.95f, 0.92f, 0.85f)));
        event.registerEntityRenderer(ModEntities.GENERAL_CAT.get(),
                ctx -> new EnhancedCatRenderer<>(ctx, layered("general", 1.18f, 0.92f, 0.88f, 0.82f)));
        event.registerEntityRenderer(ModEntities.SANHUA_WEAVER.get(),
                ctx -> new EnhancedCatRenderer<>(ctx, layered("sanhua", 0.96f, 1f, 0.94f, 0.88f)));
        event.registerEntityRenderer(ModEntities.CHESHIRE.get(),
                ctx -> new EnhancedCatRenderer<>(ctx, layered("cheshire", 1.0f, 0.85f, 0.7f, 0.95f)));
        event.registerEntityRenderer(ModEntities.WHITE_GLOVE.get(),
                ctx -> new EnhancedCatRenderer<>(ctx, layered("white_glove", 1.06f, 1f, 1f, 1f)));
        event.registerEntityRenderer(ModEntities.ALPHA.get(),
                ctx -> new EnhancedCatRenderer<>(ctx, EnhancedCatRenderer.CatStyle.hologram(cat("alpha"), 0.98f)));
        event.registerEntityRenderer(ModEntities.BLACKJACK_DEALER.get(),
                ctx -> new EnhancedCatRenderer<>(ctx, layered("blackjack", 1.05f, 0.95f, 0.9f, 0.88f)));

        event.registerEntityRenderer(ModEntities.FUR_BALL_SPIRIT.get(),
                ctx -> new EnhancedCatRenderer<>(ctx, layered("fur_ball", 0.52f, 1f, 1f, 1f)));
        event.registerEntityRenderer(ModEntities.VELVET_MOTH.get(),
                ctx -> new BlackMudSlimeRenderer<>(ctx));

        event.registerEntityRenderer(ModEntities.MIMIC_CAT.get(),
                ctx -> new EnhancedCatRenderer<>(ctx, EnhancedCatRenderer.CatStyle.mimic(cat("mimic_cat"), 0.88f)));

        event.registerEntityRenderer(ModEntities.HEAT_LEECH.get(),
                ctx -> new BlackMudSlimeRenderer<>(ctx));
        event.registerEntityRenderer(ModEntities.FORGOTTEN_WISP.get(),
                ctx -> new BlackMudSlimeRenderer<>(ctx));
        event.registerEntityRenderer(ModEntities.WHISPERING_DOLL.get(),
                ctx -> new BlackMudSlimeRenderer<>(ctx));
        event.registerEntityRenderer(ModEntities.MEMORY_MOTH.get(),
                ctx -> new BlackMudSlimeRenderer<>(ctx));
        event.registerEntityRenderer(ModEntities.GLITCH_CAT.get(),
                ctx -> new BlackMudSlimeRenderer<>(ctx, 0.85f));
        event.registerEntityRenderer(ModEntities.ORIGAMI_CROW.get(),
                ctx -> new BlackMudSlimeRenderer<>(ctx));

        event.registerEntityRenderer(ModEntities.SHADOW_CLAW.get(),
                ctx -> new EnhancedCatRenderer<>(ctx,
                        EnhancedCatRenderer.CatStyle.shadowClaw(cat("shadow_claw"), 1.55f)));
        registerCatBoss(event, ModEntities.THOUSAND_FACE_STITCHER, 1.58f);
        registerCatBoss(event, ModEntities.PRIMAL_CHAOS, 1.52f);
        registerCatBoss(event, ModEntities.GRIEF_AMALGAM, 1.28f);
        registerCatBoss(event, ModEntities.BLIND_WATER_LORD, 1.45f);
        registerCatBoss(event, ModEntities.FALLEN_VELVET, 1.12f);
        registerCatBoss(event, ModEntities.FALLEN_GENERAL, 1.1f);
        registerCatBoss(event, ModEntities.HOWLING_SQUALL, 1.28f);
        registerCatBoss(event, ModEntities.ASHURA_PHANTOM, 1.22f);
        registerCatBoss(event, ModEntities.GEAR_OVERLORD, 1.48f);
        registerCatBoss(event, ModEntities.MOON_ALLEY_WRAITH, 1.15f);
        registerCatBoss(event, ModEntities.MOON_GUARDIAN, 1.2f);
        registerCatBoss(event, ModEntities.PLAZA_SENTINEL, 1.22f);
        registerCatBoss(event, ModEntities.FIRST_CRY_WARDEN, 1.0f);

        event.registerEntityRenderer(ModEntities.SEALED_ENTITY.get(),
                ctx -> new SealedEntityRenderer(ctx));

        event.registerEntityRenderer(ModEntities.CORRUGATA_QUEEN.get(),
                ctx -> new EnhancedCatRenderer<>(ctx, layered("court_lady", 0.88f, 0.92f, 0.92f, 0.95f)));
        event.registerEntityRenderer(ModEntities.UNDERCAT_HUB_NPC.get(),
                ctx -> new EnhancedCatRenderer<>(ctx, layered("cheshire", 0.95f, 0.9f, 0.85f, 0.9f)));
        registerSlimeBoss(event, ModEntities.TAPE_COLOSSUS, 2.2f);
        registerSlimeBoss(event, ModEntities.CATNIP_DRAGON, 1.8f);
        registerSlimeBoss(event, ModEntities.SILENCED_ONE, 1.3f);
        registerSlimeBoss(event, ModEntities.ARENA_GLADIATOR, 1.0f);
        registerSlimeBoss(event, ModEntities.BOX_GHOST, 0.85f);
        registerSlimeBoss(event, ModEntities.BLIND_WATER_LEECH, 0.5f);
        event.registerEntityRenderer(ModEntities.WILD_CAT.get(), WildCatRenderer::new);
        event.registerEntityRenderer(ModEntities.TREASURE_HUNTER.get(),
                ctx -> new EnhancedCatRenderer<>(ctx, layered("samurai", 1.0f, 0.85f, 0.7f, 0.65f)));
        registerSlimeBoss(event, ModEntities.WANDERING_SLUDGE, 0.55f);
        registerSlimeBoss(event, ModEntities.MUD_FARMER, 0.85f);
        registerSlimeBoss(event, ModEntities.MUD_GUARD, 0.95f);
        registerSlimeBoss(event, ModEntities.MUD_PRIEST, 0.9f);
        event.registerEntityRenderer(ModEntities.TOWN_NPC_COMPANION.get(),
                TownNpcCompanionRenderer::new);
        event.registerEntityRenderer(ModEntities.QIN_KEMU.get(), QinKemuCompositeRenderer::new);
        event.registerEntityRenderer(ModEntities.A_FANG.get(),
                ctx -> new EnhancedCatRenderer<>(ctx, layered("a_fang", 0.7f, 1f, 0.9f, 0.85f)));
        event.registerEntityRenderer(ModEntities.LI_JIANG.get(),
                ctx -> new EnhancedCatRenderer<>(ctx, layered("li_jiang", 0.7f, 0.9f, 0.95f, 0.9f)));
        event.registerEntityRenderer(ModEntities.GRAY_WHISKER.get(),
                ctx -> new EnhancedCatRenderer<>(ctx, layered("monk", 0.92f, 0.75f, 0.78f, 0.72f)));
        event.registerEntityRenderer(ModEntities.OVERWORLD_CAT.get(),
                ctx -> new EnhancedCatRenderer<>(ctx, layered("jenna", 0.82f, 0.9f, 0.88f, 0.8f)));
        registerSlimeBoss(event, ModEntities.PRACTICE_SCARECROW, 0.75f);
        registerSlimeBoss(event, ModEntities.GHOST_TARGET, 0.35f);
        registerSlimeBoss(event, ModEntities.TRIAL_BALLOON, 0.45f);
        event.registerEntityRenderer(ModEntities.LI_QINGZHAO_CAT.get(),
                ctx -> new EnhancedCatRenderer<>(ctx, layered("court_lady", 0.75f, 0.9f, 0.85f, 0.95f)));
    }

    private static <T extends Mob> void registerCatBoss(EntityRenderersEvent.RegisterRenderers event,
            net.minecraftforge.registries.RegistryObject<? extends net.minecraft.world.entity.EntityType<? extends T>> type,
            float scale) {
        event.registerEntityRenderer(type.get(), ctx -> new BlackMudBossRenderer<>(ctx, scale));
    }

    private static <T extends Mob> void registerSlimeBoss(EntityRenderersEvent.RegisterRenderers event,
            net.minecraftforge.registries.RegistryObject<? extends net.minecraft.world.entity.EntityType<? extends T>> type,
            float scale) {
        event.registerEntityRenderer(type.get(), ctx -> new BlackMudSlimeRenderer<>(ctx, scale));
    }

    public static class SealedEntityRenderer extends EntityRenderer<SealedEntity> {
        private final ItemRenderer itemRenderer;

        public SealedEntityRenderer(EntityRendererProvider.Context ctx) {
            super(ctx);
            this.itemRenderer = ctx.getItemRenderer();
        }

        @Override
        public void render(SealedEntity entity, float entityYaw, float partialTicks,
                PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
            poseStack.pushPose();
            poseStack.translate(0.0, 0.35, 0.0);
            poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
            poseStack.scale(0.6f, 0.6f, 0.6f);
            ItemStack stack = new ItemStack(ModItems.SEAL_ORB.get());
            itemRenderer.renderStatic(stack, ItemDisplayContext.GROUND, packedLight,
                    net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY,
                    poseStack, buffer, entity.level(), entity.getId());
            poseStack.popPose();
            super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
        }

        @Override
        public ResourceLocation getTextureLocation(SealedEntity entity) {
            return new ResourceLocation(CocoJennaMod.MOD_ID, "textures/item/seal_orb.png");
        }
    }
}
