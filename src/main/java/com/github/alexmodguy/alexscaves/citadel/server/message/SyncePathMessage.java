package com.github.alexmodguy.alexscaves.citadel.server.message;

import com.github.alexmodguy.alexscaves.citadel.Citadel;
import com.github.alexmodguy.alexscaves.citadel.server.entity.pathfinding.raycoms.MNode;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.Set;

public class SyncePathMessage implements CustomPacketPayload {
    public static final Type<SyncePathMessage> TYPE =
        new Type<>(ResourceLocation.fromNamespaceAndPath("alexscaves", "citadel_sync_path"));

    private final Set<MNode> visited;
    private final Set<MNode> notVisited;
    private final Set<MNode> path;

    public SyncePathMessage(Set<MNode> visited, Set<MNode> notVisited, Set<MNode> path) {
        this.visited = visited;
        this.notVisited = notVisited;
        this.path = path;
    }

    public Set<MNode> getVisited() {
        return visited;
    }

    public Set<MNode> getNotVisited() {
        return notVisited;
    }

    public Set<MNode> getPath() {
        return path;
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
