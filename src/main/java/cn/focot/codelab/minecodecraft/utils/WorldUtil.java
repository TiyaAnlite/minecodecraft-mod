package cn.focot.codelab.minecodecraft.utils;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class WorldUtil extends AbstractUtil {
    public static boolean isCanTeleportPos(World world, BlockPos pos) {
        if (world.getRegistryKey().equals(World.OVERWORLD)) {
            return pos.getY() > -64;
        } else {
            return pos.getY() > 0;
        }
    }

    public static double distance(Vec3d a, Vec3d b) {
        double x = a.getX() - b.getX();
        double y = a.getY() - b.getY();
        double z = a.getZ() - b.getZ();
        return Math.sqrt(x * x + y * y + z * z);
    }
}
