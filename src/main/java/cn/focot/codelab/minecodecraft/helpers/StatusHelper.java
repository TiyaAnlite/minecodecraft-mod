package cn.focot.codelab.minecodecraft.helpers;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class StatusHelper extends AbstractHelper {
    private static final ConcurrentHashMap<String, PlayerPos> playerPosHistory = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, PlayerWhereRequest> playerWhereRequest = new ConcurrentHashMap<>();
    private static int playerWhereRequestUpdateTimer = 0;

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
        ServerWorld world = player.getWorld();
        updatePlayerPosHistory(player.getName().getString(), player.getPos(), world);
    }

    public static void updatePlayerPosHistory(String name, Vec3d pos, ServerWorld world) {
        PlayerPos playerPos = new PlayerPos(pos, world);
        playerPosHistory.put(name, playerPos);
        LOGGER.info("Updated %s pos history: [%.2f, %.2f, %.2f]".formatted(name, pos.getX(), pos.getY(), pos.getZ()));
    }

    public static PlayerPos getPlayerPosHistory(String name) {
        return playerPosHistory.get(name);
    }

    public static boolean hasPlayerPosHistory(String name) {
        return playerPosHistory.containsKey(name);
    }

    public static void addPlayerWhereRequest(ServerPlayerEntity targetPlayer, ServerPlayerEntity requestPlayer) {
        playerWhereRequest.put(targetPlayer.getName().getString(), new PlayerWhereRequest(requestPlayer, targetPlayer));
    }

    public static void removePlayerWhereRequest(ServerPlayerEntity targetPlayer) {
        playerWhereRequest.remove(targetPlayer.getName().getString());
    }

    public static PlayerWhereRequest getPlayerWhereRequest(ServerPlayerEntity targetPlayer) {
        return playerWhereRequest.get(targetPlayer.getName().getString());
    }

    public static void tickPlayerWhereRequest() {
        if (++playerWhereRequestUpdateTimer >= 20) {
            LinkedHashMap<String, PlayerWhereRequest> removeKey = new LinkedHashMap<>();
            for (Map.Entry<String, PlayerWhereRequest> e : playerWhereRequest.entrySet()) {
                if (e.getValue().isExpired()) {
                    removeKey.put(e.getKey(), e.getValue());
                }
            }
            for (Map.Entry<String, PlayerWhereRequest> e : removeKey.entrySet()) {
                playerWhereRequest.remove(e.getKey());
                PlayerWhereRequest request = e.getValue();
                ServerPlayerEntity source = request.getSource();
                ServerPlayerEntity target = request.getTarget();
                LOGGER.info("Removing %s request".formatted(request.getSourceName()));
                if (!(Objects.isNull(source))) {
                    source.sendMessage(Text.of("§3%s§r超时未接受你的位置广播请求".formatted(request.getSourceName())));
                }
                if (!(Objects.isNull(target))) {
                    target.sendMessage(Text.of("已超时拒绝§3%s§r的位置广播请求".formatted(request.getTargetName())));
                }
            }
            playerWhereRequestUpdateTimer = 0;
        }
    }

    public static void tickServerStatus() {
        tickPlayerWhereRequest();
    }
}

