package cn.focot.codelab.minecodecraft.helpers;

import cn.focot.codelab.minecodecraft.Config;
import cn.focot.codelab.minecodecraft.MineCodeCraftMod;
import cn.focot.codelab.minecodecraft.utils.MessageUtil;
import net.minecraft.server.MinecraftServer;
import org.apache.logging.log4j.Logger;

import java.util.LinkedList;

public class TipsHelper {
    private static TipsThread th;

    static public boolean lunch() {
        if (!(th == null)) {
            return false;
        }
        th = new TipsThread();
        th.setName("TipsHelper");
        th.setDaemon(true);
        th.start();
        return true;
    }
}

class TipsThread extends Thread {
    private static final Logger LOGGER = MineCodeCraftMod.getLogger();
    private static final Config config = MineCodeCraftMod.getConfig();
    private static final int interval = config.getConfigBean().tips.interval;

    @Override
    public void run() {
        LinkedList<String> tips = new LinkedList<>(config.getConfigBean().tips.tips);
        LOGGER.info("TipsThread start, loaded %d tips, interval %d".formatted(tips.size(), interval));
        MinecraftServer server = MineCodeCraftMod.getMinecraftServer();
        while (server.isRunning()) {
            for (String t : tips) {
                int ref = 0;
                while (server.isRunning()) {
                    if (ref >= interval) {
                        MessageUtil.broadcastPrefixMessage(t);
                        break;
                    } else {
                        ref++;
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            LOGGER.error("TipsThread loop", e);
                        }
                    }
                }
            }

        }
        LOGGER.info("TipsThread exit");
    }
}
