package cn.focot.codelab.minecodecraft.helpers;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

public class PlayerPos {
    Vec3d pos;
    ServerWorld world;

    PlayerPos(Vec3d pos, ServerWorld world) {
        this.pos = pos;
        this.world = world;
    }

    public Vec3d getPos() {
        return pos;
    }

    public ServerWorld getWorld() {
        return world;
    }
}
