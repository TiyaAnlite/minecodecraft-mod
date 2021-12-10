package cn.focot.codelab.minecodecraft.helpers;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

public class PlayerPos {
    BlockPos pos;
    ServerWorld world;

    PlayerPos(BlockPos pos, ServerWorld world) {
        this.pos = pos;
        this.world = world;
    }

    public BlockPos getPos() {
        return pos;
    }

    public ServerWorld getWorld() {
        return world;
    }
}
