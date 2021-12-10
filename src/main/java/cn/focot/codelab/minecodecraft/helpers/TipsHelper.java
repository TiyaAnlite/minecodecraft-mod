package cn.focot.codelab.minecodecraft.helpers;

import cn.focot.codelab.minecodecraft.Config;
import cn.focot.codelab.minecodecraft.MineCodeCraftMod;
import cn.focot.codelab.minecodecraft.utils.MessageUtil;
import net.minecraft.server.MinecraftServer;
import org.apache.logging.log4j.Logger;

import java.util.LinkedList;

public class TipsHelper {
    private static TipsThread th;
    private static boolean isLunch = false;

    public static boolean lunch() {
        if (isLunch) {
            return false;
        }
        th = new TipsThread();
        th.setName("TipsHelper");
        th.setDaemon(true);
        th.start();
        isLunch = true;
        return true;
    }

    public static void reloadTips() {
        if (isLunch) {
            th.setDirty(true);
        }
    }

}

class TipsThread extends Thread {
    private static final Logger LOGGER = MineCodeCraftMod.getLogger();
    private static final Config config = MineCodeCraftMod.getConfig();
    private int interval;
    private LinkedList<String> tips;
    private boolean dirty;

    @Override
    public void run() {
        MinecraftServer server = MineCodeCraftMod.getMinecraftServer();
        LOGGER.info("TipsThread start");
        this.getConfig();
        while (server.isRunning()) {
            for (String t : this.tips) {
                int ref = 0;
                while (server.isRunning()) {
                    if (ref >= this.interval) {
                        MessageUtil.broadcastPrefixMessage(t, false, false);
                        break;
                    } else {
                        ref++;
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            LOGGER.error("TipsThread loop", e);
                        }
                    }
                    if (this.isDirty()) {
                        break;  //Update at upper
                    }
                }
                if (this.isDirty()) {
                    LOGGER.info("Reload tips config");
                    this.getConfig();
                    break;  //Update and restart
                }
            }

        }
        LOGGER.info("TipsThread exit");
    }

    private void getConfig() {
        this.tips = new LinkedList<>(config.getConfigBean().tips.tips);
        this.interval = config.getConfigBean().tips.interval;
        this.setDirty(false);
        LOGGER.info("Loaded %d tips, interval %d".formatted(this.tips.size(), this.interval));
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    public boolean isDirty() {
        return dirty;
    }
}
