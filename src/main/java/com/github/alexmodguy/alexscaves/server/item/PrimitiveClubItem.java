package com.github.alexmodguy.alexscaves.server.item;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.server.enchantment.ACEnchantmentHelper;
import com.github.alexmodguy.alexscaves.server.enchantment.ACEnchantmentRegistry;
import com.github.alexmodguy.alexscaves.server.message.UpdateEffectVisualityEntityMessage;
import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import com.github.alexmodguy.alexscaves.server.potion.ACEffectRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;

public class PrimitiveClubItem extends Item implements StackSensitiveAttributesItem {

    public PrimitiveClubItem(Item.Properties properties) {
        super(properties);
    }

    private static ItemAttributeModifiers createDefaultAttributes() {
        return ItemAttributeModifiers.builder()
                .add(Attributes.ATTACK_DAMAGE, new AttributeModifier(ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "primitive_club_attack_damage"), 8.0D, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
                .add(Attributes.ATTACK_SPEED, new AttributeModifier(ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "primitive_club_attack_speed"), -3.75D, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
                .build();
    }

    private static ItemAttributeModifiers createAttributesForSwiftwoodLevel(int swiftwoodLevel) {
        double speedModifier = Math.min(0, -3.75D + 0.15D * swiftwoodLevel);
        return ItemAttributeModifiers.builder()
                .add(Attributes.ATTACK_DAMAGE, new AttributeModifier(ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "primitive_club_attack_damage"), 8.0D, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
                .add(Attributes.ATTACK_SPEED, new AttributeModifier(ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "primitive_club_attack_speed"), speedModifier, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
                .build();
    }

    @Override
    public int getEnchantmentValue() {
        return 1;
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return stack.getCount() == 1;
    }

    public boolean hurtEnemy(ItemStack stack, LivingEntity hurtEntity, LivingEntity player) {
        stack.hurtAndBreak(1, player, EquipmentSlot.MAINHAND);
        if (!hurtEntity.level().isClientSide) {
            SoundEvent soundEvent = ACSoundRegistry.PRIMITIVE_CLUB_MISS.get();
            if (hurtEntity.getRandom().nextFloat() < 0.8F) {
                int stunDuration = 150 + hurtEntity.getRandom().nextInt(150);
                // If already stunned, add to remaining duration to ensure effect always extends
                MobEffectInstance existingStun = hurtEntity.getEffect(ACEffectRegistry.STUNNED);
                if (existingStun != null) {
                    stunDuration += existingStun.getDuration();
                }
                MobEffectInstance instance = new MobEffectInstance(ACEffectRegistry.STUNNED, stunDuration, 0, false, false);
                if (hurtEntity.addEffect(instance)) {
                    AlexsCaves.sendMSGToAll(new UpdateEffectVisualityEntityMessage(hurtEntity.getId(), player.getId(), 3, instance.getDuration()));
                    soundEvent = ACSoundRegistry.PRIMITIVE_CLUB_HIT.get();
                    int dazingEdgeLevel = ACEnchantmentHelper.getEnchantmentLevel(hurtEntity.level(), ACEnchantmentRegistry.DAZING_SWEEP, stack);
                    if (dazingEdgeLevel > 0) {
                        float f = dazingEdgeLevel + 1.2F;
                        AABB aabb = AABB.ofSize(hurtEntity.position(), f, f, f);
                        for (Entity entity : hurtEntity.level().getEntities(player, aabb, Entity::canBeHitByProjectile)) {
                            if (!entity.is(hurtEntity) && !entity.isAlliedTo(player) && entity.distanceTo(hurtEntity) <= f && entity instanceof LivingEntity inflict) {
                                int aoeStunDuration = 80 + hurtEntity.getRandom().nextInt(80);
                                MobEffectInstance existingAoeStun = inflict.getEffect(ACEffectRegistry.STUNNED);
                                if (existingAoeStun != null) {
                                    aoeStunDuration += existingAoeStun.getDuration();
                                }
                                MobEffectInstance instance2 = new MobEffectInstance(ACEffectRegistry.STUNNED, aoeStunDuration, 0, false, false);
                                inflict.hurt(inflict.level().damageSources().mobAttack(player), 1.0F);
                                if (inflict.addEffect(instance2)) {
                                    AlexsCaves.sendMSGToAll(new UpdateEffectVisualityEntityMessage(inflict.getId(), player.getId(), 3, instance2.getDuration()));
                                }
                            }
                        }
                    }
                }
            }
            player.level().playSound((Player) null, player.getX(), player.getY(), player.getZ(), soundEvent, player.getSoundSource(), 1.0F, 1.0F);

        }
        return true;
    }

    public boolean mineBlock(ItemStack itemStack, Level level, BlockState state, BlockPos blockPos, LivingEntity
            livingEntity) {
        if ((double) state.getDestroySpeed(level, blockPos) != 0.0D) {
            itemStack.hurtAndBreak(2, livingEntity, EquipmentSlot.MAINHAND);
        }

        return true;
    }

    public boolean isValidRepairItem(ItemStack item, ItemStack repairItem) {
        return repairItem.is(ACItemRegistry.HEAVY_BONE.get()) || super.isValidRepairItem(item, repairItem);
    }

    @Override
    public ItemAttributeModifiers getDefaultAttributeModifiers(ItemStack stack) {
        int swift = ACEnchantmentHelper.getEnchantmentLevelFromStack(ACEnchantmentRegistry.SWIFTWOOD, stack);
        if (swift > 0) {
            return createAttributesForSwiftwoodLevel(Mth.clamp(swift, 0, 3));
        }
        return createDefaultAttributes();
    }

    public void initializeClient(java.util.function.Consumer<IClientItemExtensions> consumer) {
        consumer.accept((IClientItemExtensions) AlexsCaves.PROXY.getISTERProperties());
    }

    public boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity) {
        return player.getAttackStrengthScale(0) < 0.95 || player.attackAnim != 0;
    }

    public boolean onEntitySwing(ItemStack stack, LivingEntity entity) {
        if (entity instanceof Player player) {
            if (player.getAttackStrengthScale(0) < 1 && player.attackAnim > 0) {
                return true;
            } else {
                player.swingTime = -1;
            }
        }
        return false;
    }

    public void inventoryTick(ItemStack stack, Level level, Entity entity, int i, boolean held) {
        if (entity instanceof Player player && held) {
            if (player.getAttackStrengthScale(0) < 0.95 && player.attackAnim > 0) {
                player.swingTime--;
            }
        }
    }
}
