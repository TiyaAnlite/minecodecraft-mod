package cn.focot.codelab.minecodecraft.handlers;

import cn.focot.codelab.minecodecraft.MineCodeCraftMod;
import cn.focot.codelab.minecodecraft.event.ServerAction;
import cn.focot.codelab.minecodecraft.helpers.ServerHelper;
import cn.focot.codelab.minecodecraft.helpers.StatusHelper;
import cn.focot.codelab.minecodecraft.helpers.TipsHelper;
import cn.focot.codelab.minecodecraft.utils.MessageUtil;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.TimeHelper;
import net.minecraft.util.Util;

import java.time.Duration;
import java.util.concurrent.TimeoutException;

public class ServerHandler extends AbstractHandler {

    public static void onServerLoaded(MinecraftServer server) {
        LOGGER.info("MineCodeCraft %s loaded, %s".formatted(version, description));
        LOGGER.info("Lunch from %s, %d days lunched".formatted(ServerHelper.getLunchTime(), StatusHelper.lunchedTime()));
        MineCodeCraftMod.setMinecraftServer(server);
        TipsHelper.lunch();
        ServerAction.of("lunch").publish();
    }

    public static void onServerStopping(MinecraftServer server) {
        ServerAction.of("stop").publish();
    }

    public static void onServerStopped(MinecraftServer server) {
        if (MineCodeCraftMod.hasNatsConnection()) {
            try {
                MineCodeCraftMod.getNatsConnection().flush(Duration.ZERO);
            } catch (TimeoutException | InterruptedException ignored) {
            }
        }
    }

    public static void onServerTickEnd(MinecraftServer server, long nanosPerTick, long lastOverloadWarningNanos, long OverloadThresholdNanos) {
        long m = Util.getMeasuringTimeMs() - server.getTimeReference();
        if (m > OverloadThresholdNanos + 20L * nanosPerTick && server.getTimeReference() - lastOverloadWarningNanos >= OverloadThresholdNanos + 100L * nanosPerTick) {
            long n = m / nanosPerTick;
            LOGGER.warn("Server overload: skip %dtick(%dms) behind".formatted(n, m / TimeHelper.MILLI_IN_NANOS));
            MessageUtil.broadcastPrefixMessage("§c§o警告:§b§o检测到Tick运算延迟，跳过至§e§o%dTick (%dms)§b§o后§r".formatted(n, m / TimeHelper.MILLI_IN_NANOS), false, false);
        }
    }

    public static void onWorldTick(ServerWorld world) {
        ServerHelper.tickServer();
        StatusHelper.tickServerStatus();
    }
}
