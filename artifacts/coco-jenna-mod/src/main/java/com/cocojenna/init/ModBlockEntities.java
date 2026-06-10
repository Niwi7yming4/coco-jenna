package com.cocojenna.init;

import com.cocojenna.CocoJennaMod;
import com.cocojenna.block.entity.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, CocoJennaMod.MOD_ID);

    public static final RegistryObject<BlockEntityType<DistillerBlockEntity>> DISTILLER =
            BLOCK_ENTITIES.register("distiller",
                    () -> BlockEntityType.Builder.of(DistillerBlockEntity::new, ModBlocks.DISTILLER.get()).build(null));

    public static final RegistryObject<BlockEntityType<AromaDistillerBlockEntity>> AROMA_DISTILLER =
            BLOCK_ENTITIES.register("aroma_distiller",
                    () -> BlockEntityType.Builder.of(AromaDistillerBlockEntity::new, ModBlocks.AROMA_DISTILLER.get()).build(null));

    public static final RegistryObject<BlockEntityType<FoodBowlBlockEntity>> FOOD_BOWL =
            BLOCK_ENTITIES.register("food_bowl",
                    () -> BlockEntityType.Builder.of(FoodBowlBlockEntity::new, ModBlocks.FOOD_BOWL.get()).build(null));

    public static final RegistryObject<BlockEntityType<SealPedestalBlockEntity>> SEAL_PEDESTAL =
            BLOCK_ENTITIES.register("seal_pedestal",
                    () -> BlockEntityType.Builder.of(SealPedestalBlockEntity::new, ModBlocks.SEAL_PEDESTAL.get()).build(null));

    public static final RegistryObject<BlockEntityType<IronpawForgeBlockEntity>> IRONPAW_FORGE =
            BLOCK_ENTITIES.register("ironpaw_forge",
                    () -> BlockEntityType.Builder.of(IronpawForgeBlockEntity::new, ModBlocks.IRONPAW_FORGE.get()).build(null));

    public static final RegistryObject<BlockEntityType<SocketingTableBlockEntity>> SOCKETING_TABLE =
            BLOCK_ENTITIES.register("socketing_table",
                    () -> BlockEntityType.Builder.of(SocketingTableBlockEntity::new, ModBlocks.SOCKETING_TABLE.get()).build(null));
}
