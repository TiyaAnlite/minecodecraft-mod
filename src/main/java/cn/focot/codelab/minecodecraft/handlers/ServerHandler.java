package cn.focot.codelab.minecodecraft.handlers;

import cn.focot.codelab.minecodecraft.MineCodeCraftMod;
import cn.focot.codelab.minecodecraft.helpers.ServerHelper;
import cn.focot.codelab.minecodecraft.helpers.StatusHelper;
import cn.focot.codelab.minecodecraft.helpers.TipsHelper;
import cn.focot.codelab.minecodecraft.utils.MessageUtil;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Util;

public class ServerHandler extends AbstractHandler {

    public static void onServerLoaded(MinecraftServer server) {
        LOGGER.info("MineCodeCraft %s loaded, %s".formatted(version, description));
        LOGGER.info("Lunch from %s, %d days lunched".formatted(ServerHelper.getLunchTime(), StatusHelper.lunchedTime()));
        MineCodeCraftMod.setMinecraftServer(server);
        TipsHelper.lunch();
    }

    public static void onServerTickEnd(MinecraftServer server, long lastTimeReference) {
        long l = Util.getMeasuringTimeMs() - server.getTimeReference();
        if (l > 2000L && server.getTimeReference() - lastTimeReference >= 15000L) {
            long m = l / 50L;
            LOGGER.warn("Server overload: skip %dtick(%dms) behind".formatted(m, l));
            MessageUtil.broadcastPrefixMessage("§c§o警告:§b§o检测到Tick运算延迟，跳过至§e§o%dTick (%dms)§b§o后§r".formatted(m, l), false, false);
        }
    }

    public static void onWorldTick(MinecraftServer server) {
        ServerHelper.tickServer();
        StatusHelper.tickServerStatus();
    }
}
