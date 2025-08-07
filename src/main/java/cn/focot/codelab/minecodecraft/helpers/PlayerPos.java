package cn.focot.codelab.minecodecraft.helpers;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class PlayerPos extends AbstractHelper {
    BlockPos pos;
    ServerWorld world;

    PlayerPos(BlockPos pos, ServerWorld world) {
        this.pos = pos;
        this.world = world;
    }

    public Vec3d getPos() {
        return pos.toCenterPos();
    }

    public BlockPos getIPos() {
        return pos;
    }

    public ServerWorld getWorld() {
        return world;
    }
}
