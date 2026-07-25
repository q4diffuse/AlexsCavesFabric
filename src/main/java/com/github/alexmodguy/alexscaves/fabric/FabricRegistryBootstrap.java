package com.github.alexmodguy.alexscaves.fabric;

import com.github.alexmodguy.alexscaves.client.ClientProxy;
import com.github.alexmodguy.alexscaves.client.model.layered.ACModelLayers;
import com.github.alexmodguy.alexscaves.client.particle.ACParticleRegistry;
import com.github.alexmodguy.alexscaves.client.render.entity.layer.ClientLayerRegistry;
import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import com.github.alexmodguy.alexscaves.server.block.blockentity.ACBlockEntityRegistry;
import com.github.alexmodguy.alexscaves.server.block.fluid.ACFluidRegistry;
import com.github.alexmodguy.alexscaves.server.block.poi.ACPOIRegistry;
import com.github.alexmodguy.alexscaves.server.entity.ACEntityDataRegistry;
import com.github.alexmodguy.alexscaves.server.entity.ACEntityRegistry;
import com.github.alexmodguy.alexscaves.server.entity.ACFrogRegistry;
import com.github.alexmodguy.alexscaves.server.inventory.ACMenuRegistry;
import com.github.alexmodguy.alexscaves.server.item.ACArmorMaterial;
import com.github.alexmodguy.alexscaves.server.item.ACItemRegistry;
import com.github.alexmodguy.alexscaves.server.level.carver.ACCarverRegistry;
import com.github.alexmodguy.alexscaves.server.level.feature.ACFeatureRegistry;
import com.github.alexmodguy.alexscaves.server.level.structure.ACStructureRegistry;
import com.github.alexmodguy.alexscaves.server.level.structure.piece.ACStructurePieceRegistry;
import com.github.alexmodguy.alexscaves.server.level.structure.processor.ACStructureProcessorRegistry;
import com.github.alexmodguy.alexscaves.server.level.surface.ACSurfaceRuleConditionRegistry;
import com.github.alexmodguy.alexscaves.server.misc.ACAdvancementTriggerRegistry;
import com.github.alexmodguy.alexscaves.server.misc.ACCreativeTabRegistry;
import com.github.alexmodguy.alexscaves.server.misc.ACDataComponentRegistry;
import com.github.alexmodguy.alexscaves.server.misc.ACLootTableRegistry;
import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import com.github.alexmodguy.alexscaves.server.potion.ACEffectRegistry;
import com.github.alexmodguy.alexscaves.server.recipe.ACRecipeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

final class FabricRegistryBootstrap {

    private FabricRegistryBootstrap() {
    }

    static void bootstrapCommon() {
        register(
            ACBlockRegistry.DEF_REG,
            ACFluidRegistry.FLUID_DEF_REG,
            ACEntityRegistry.DEF_REG,
            ACParticleRegistry.DEF_REG,
            ACBlockEntityRegistry.DEF_REG,
            ACMenuRegistry.DEF_REG,
            ACSoundRegistry.DEF_REG,
            ACEffectRegistry.DEF_REG,
            ACEffectRegistry.POTION_DEF_REG,
            ACItemRegistry.DEF_REG,
            ACCreativeTabRegistry.DEF_REG,
            ACRecipeRegistry.TYPE_DEF_REG,
            ACRecipeRegistry.DEF_REG,
            ACPOIRegistry.DEF_REG,
            ACFrogRegistry.DEF_REG,
            ACAdvancementTriggerRegistry.DEF_REG,
            ACLootTableRegistry.LOOT_FUNCTION_DEF_REG,
            ACFeatureRegistry.DEF_REG,
            ACCarverRegistry.DEF_REG,
            ACStructureRegistry.DEF_REG,
            ACStructurePieceRegistry.DEF_REG,
            ACStructureProcessorRegistry.DEF_REG,
            ACSurfaceRuleConditionRegistry.DEF_REG,
            ACArmorMaterial.ARMOR_MATERIALS,
            ACEntityDataRegistry.DEF_REG
        );
        ACEntityDataRegistry.registerFabricSerializers();
        ACPOIRegistry.registerFabricBlockStates();
        ACDataComponentRegistry.init(null);
        ACFluidRegistry.postInit();
        bootstrapEntities();
    }

    static void bootstrapClient() {
        ACModelLayers.registerFabric();
        ClientLayerRegistry.registerFabric();
        ClientProxy.registerFabricClientLifecycle();
        ClientProxy.registerFabricShaders();
        ClientProxy.registerFabricBuiltinItemRenderers();
        ClientProxy.registerFabricFluidRendering();
    }

    @SafeVarargs
    private static void register(DeferredRegister<?>... registers) {
        for (DeferredRegister<?> register : registers) {
            register.register(null);
        }
    }

    private static void bootstrapEntities() {
        EntityAttributeCreationEvent attributeEvent = new EntityAttributeCreationEvent();
        ACEntityRegistry.initializeAttributes(attributeEvent);
        for (var entry : attributeEvent.getAttributes().entrySet()) {
            FabricDefaultAttributeRegistry.register(entry.getKey(), entry.getValue());
        }
        ACEntityRegistry.spawnPlacements(new RegisterSpawnPlacementsEvent());
    }
}
