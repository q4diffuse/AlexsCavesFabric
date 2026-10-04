package com.github.alexmodguy.alexscaves.fabric;

import com.github.alexmodguy.alexscaves.citadel.server.tick.ServerTickRateTracker;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.EntityEvent;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.event.village.VillagerTradesEvent;
import net.neoforged.neoforge.event.village.WandererTradesEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ACFabricEventBridge {

    private ACFabricEventBridge() {
    }

    public static void registerCommon() {
        UseEntityCallback.EVENT.register((player, level, hand, entity, hitResult) -> {
            if (level.isClientSide) {
                return InteractionResult.PASS;
            }
            PlayerInteractEvent.EntityInteract event = new PlayerInteractEvent.EntityInteract(
                player,
                level,
                hand,
                player.getItemInHand(hand),
                entity
            );
            NeoForge.EVENT_BUS.post(event);
            return event.isCanceled() ? event.getCancellationResult() : InteractionResult.PASS;
        });

        UseItemCallback.EVENT.register((player, level, hand) -> {
            if (level.isClientSide) {
                return InteractionResultHolder.pass(player.getItemInHand(hand));
            }
            PlayerInteractEvent.RightClickItem event = new PlayerInteractEvent.RightClickItem(
                player,
                level,
                hand,
                player.getItemInHand(hand)
            );
            NeoForge.EVENT_BUS.post(event);
            if (!event.isCanceled()) {
                return InteractionResultHolder.pass(event.getItemStack());
            }
            return new InteractionResultHolder<>(event.getCancellationResult(), event.getItemStack());
        });

        AttackEntityCallback.EVENT.register((player, level, hand, entity, hitResult) -> {
            if (level.isClientSide) {
                return InteractionResult.PASS;
            }
            AttackEntityEvent event = new AttackEntityEvent(player, entity);
            NeoForge.EVENT_BUS.post(event);
            return event.isCanceled() ? InteractionResult.FAIL : InteractionResult.PASS;
        });

        // Citadel's CitadelEvents.onServerTick: counts down tick rate modifiers (Sugar Rush) and syncs them to clients
        ServerTickEvents.START_SERVER_TICK.register(server -> {
            if (server.isRunning()) {
                ServerTickRateTracker.getForServer(server).masterTick();
            }
        });

        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (var player : server.getPlayerList().getPlayers()) {
                NeoForge.EVENT_BUS.post(new PlayerTickEvent.Post(player));
            }
        });

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) ->
            NeoForge.EVENT_BUS.post(new PlayerEvent.PlayerLoggedInEvent(handler.getPlayer()))
        );

        TradeOfferHelper.registerVillagerOffers(VillagerProfession.CARTOGRAPHER, 2, offers -> {
            Map<Integer, List<VillagerTrades.ItemListing>> tradeMap = new HashMap<>();
            tradeMap.put(2, offers);
            NeoForge.EVENT_BUS.post(new VillagerTradesEvent(VillagerProfession.CARTOGRAPHER, tradeMap));
        });

        TradeOfferHelper.registerWanderingTraderOffers(1, offers ->
            NeoForge.EVENT_BUS.post(new WandererTradesEvent(offers))
        );
    }
}
