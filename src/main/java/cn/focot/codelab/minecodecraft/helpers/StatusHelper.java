package cn.focot.codelab.minecodecraft.helpers;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

public class StatusHelper extends AbstractHelper {
    private static final ConcurrentHashMap<String, PlayerPos> playerPosHistory = new ConcurrentHashMap<>();

    public static int lunchedTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            long lunch = sdf.parse(config.getConfigBean().lunchTime).getTime();
            long now = new Date().getTime();
            long between_time = (now - lunch) / (1000 * 3600 * 24);
            return Integer.parseInt(String.valueOf(between_time));
        } catch (ParseException e) {
            LOGGER.error("Cannot parse lunchTime");
            return 0;
        }
    }

    public static void updatePlayerPosHistory(ServerPlayerEntity player) {
        BlockPos pos = new BlockPos(player.getPos());
        ServerWorld world = player.getWorld();
        updatePlayerPosHistory(player.getName().asString(), pos, world);
    }

    public static void updatePlayerPosHistory(String name, BlockPos pos, ServerWorld world) {
        PlayerPos playerPos = new PlayerPos(pos, world);
        playerPosHistory.put(name, playerPos);
        LOGGER.info("Updated %s pos history: [%s]".formatted(name, pos.toShortString()));
    }

    public static PlayerPos getPlayerPosHistory(String name) {
        return playerPosHistory.get(name);
    }

    public static boolean hasPlayerPosHistory(String name) {
        return playerPosHistory.containsKey(name);
    }
}

