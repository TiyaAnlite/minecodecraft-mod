package cn.focot.codelab.minecodecraft.helpers;

import cn.focot.codelab.minecodecraft.utils.MessageUtil;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.server.PlayerManager;

public class ServerHelper extends AbstractHelper {
    private static int latencyUpdateTimer = 0;

    public static String getLunchTime() {
        return config.getConfigBean().lunchTime;
    }

    public static boolean saveServer() {
        LOGGER.info("Server save start.");
        MessageUtil.broadcastPrefixMessage("§7§o正在进行存档，请耐心等待§r", false, false);
        boolean result = getServer().saveAll(false, true, false);
        if (result) {
            LOGGER.info("Server saved.");
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

}
