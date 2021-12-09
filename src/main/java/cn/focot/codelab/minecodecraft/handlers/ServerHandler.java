package cn.focot.codelab.minecodecraft.handlers;

import cn.focot.codelab.minecodecraft.Config;
import cn.focot.codelab.minecodecraft.MineCodeCraftMod;
import cn.focot.codelab.minecodecraft.helpers.StatusHelper;
import cn.focot.codelab.minecodecraft.helpers.TipsHelper;
import net.minecraft.server.MinecraftServer;
import org.apache.logging.log4j.Logger;

public class ServerHandler {
    private static final Logger LOGGER = MineCodeCraftMod.getLogger();
    private static final Config config = MineCodeCraftMod.getConfig();

    public static void onServerLoaded(MinecraftServer server) {
        LOGGER.info("MineCodeCraft %s loaded, %s".formatted(MineCodeCraftMod.getVersion(), MineCodeCraftMod.getDescription()));
        LOGGER.info("Lunch from %s, %d days lunched".formatted(config.getConfigBean().lunchTime, StatusHelper.lunchTime()));
        MineCodeCraftMod.setMinecraftServer(server);
        TipsHelper.lunch();
    }

    public static void onTick(MinecraftServer server) {
//        int now = (int) (System.currentTimeMillis() / 1000);
//        if (now - MineCodeCraftHelper.time >= 3) {
//            LOGGER.info("Tick at 3 sec");
//            MineCodeCraftHelper.time = now;
//        }
    }
}
