package cn.focot.codelab.minecodecraft.utils;

import cn.focot.codelab.minecodecraft.MineCodeCraftMod;
import net.minecraft.server.MinecraftServer;

public abstract class AbstractUtil {
    protected static MinecraftServer getServer() {
        return MineCodeCraftMod.getMinecraftServer();
    }
}
