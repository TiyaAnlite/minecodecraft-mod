package cn.focot.codelab.minecodecraft.helpers;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

public class PlayerPos extends AbstractHelper {
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

    public void readNbt(NbtCompound nbt) {

    }

    public void writeNbt(NbtCompound nbt) {

    }
}
