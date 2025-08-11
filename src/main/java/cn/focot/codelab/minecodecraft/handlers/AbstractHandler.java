package cn.focot.codelab.minecodecraft.handlers;

import cn.focot.codelab.minecodecraft.MineCodeCraftMod;
import com.google.gson.Gson;
import net.minecraft.server.MinecraftServer;
import org.slf4j.Logger;

public abstract class AbstractHandler {
    protected static final Logger LOGGER = MineCodeCraftMod.getLogger();
    protected static final String version = MineCodeCraftMod.getVersion();
    protected static final String description = MineCodeCraftMod.getDescription();
    protected static final Gson gson = new Gson();

    protected static MinecraftServer getServer() {
        return MineCodeCraftMod.getMinecraftServer();
    }
}
