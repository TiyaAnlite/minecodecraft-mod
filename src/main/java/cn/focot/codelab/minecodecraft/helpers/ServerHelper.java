package cn.focot.codelab.minecodecraft.helpers;

import cn.focot.codelab.minecodecraft.utils.MessageUtil;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.world.ServerWorld;

import java.util.EnumSet;

public class ServerHelper extends AbstractHelper {
    private static int latencyUpdateTimer = 0;
    private static int autoSaveTimer = 0;

    public static String getLunchTime() {
        return config.getConfigBean().lunchTime;
    }

    public static boolean saveServer(boolean suppressLogs, boolean flush, boolean force) {
        LOGGER.debug("Server save start.");
        String savingTypeMsg;
        if (flush) {
            savingTypeMsg = "同步存档，需要较久时间，请耐心等待";
        } else {
            savingTypeMsg = "异步存档";
        }
        MessageUtil.broadcastPrefixMessage("§7§o正在进行%s§r".formatted(savingTypeMsg), false, false);
        boolean result = getServer().saveAll(suppressLogs, flush, force);
        if (result) {
            LOGGER.debug("Server saved.");
            MessageUtil.broadcastPrefixMessage("§7§o存档完成§r", false, false);
        } else {
            LOGGER.error("Server save failed.");
            MessageUtil.broadcastPrefixMessage("§c§o警告：§e存档没有成功§r", false, false);
        }
        return result;
    }

    public static boolean updatePlayerLatency() {
        //Skip timer when config == 30 secs
        int interval = config.getConfigBean().playerLatencyUpdateInterval;
        if (!(interval == 30) && ++latencyUpdateTimer > interval * 20) {
            PlayerManager manager = getServer().getPlayerManager();
            manager.sendToAll(new PlayerListS2CPacket(PlayerListS2CPacket.Action.UPDATE_LATENCY, manager.getPlayerList()));
            latencyUpdateTimer = 0;
            return true;
        }
        return false;
    }

    public static boolean autoSave() {
        //Skip timer when config <= 0 secs
        int interval = config.getConfigBean().worldAutoSaveInterval;
        if (interval > 0) {
            for (ServerWorld world : getServer().getWorlds()) {
                if (!world.isSavingDisabled()) {
                    LOGGER.info("Disable auto saving for %s".formatted(world.getRegistryKey().getValue().toString()));
                    world.savingDisabled = true;
                }
            }
            if (++autoSaveTimer > interval * 20) {
                autoSaveTimer = 0;
                LOGGER.debug("Auto saving start");
                //Silent, no flush, force save
                return saveServer(true, false, true);
            }
        }
        return false;
    }

    public static void tickServer() {
        updatePlayerLatency();
        autoSave();
    }

}
