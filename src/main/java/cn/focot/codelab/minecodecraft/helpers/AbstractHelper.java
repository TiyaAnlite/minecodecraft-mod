package cn.focot.codelab.minecodecraft.helpers;

import cn.focot.codelab.minecodecraft.Config;
import cn.focot.codelab.minecodecraft.MineCodeCraftMod;
import net.minecraft.server.MinecraftServer;
import org.apache.logging.log4j.Logger;

public abstract class AbstractHelper {
    protected static final Logger LOGGER = MineCodeCraftMod.getLogger();
    protected static final Config config = MineCodeCraftMod.getConfig();
    protected static final String version = MineCodeCraftMod.getVersion();

    protected static MinecraftServer getServer() {
        return MineCodeCraftMod.getMinecraftServer();
    }
}
