package cn.focot.codelab.minecodecraft.helpers;

import cn.focot.codelab.minecodecraft.MineCodeCraftMod;

public class EventTrigger extends AbstractHelper {
    public static void onConfigReload() {
        TipsHelper.reloadTips();
        Thread th = new Thread(MineCodeCraftMod::loadNatsConnection);
        th.setName("NatsAsyncConnection");
        th.setDaemon(true);
        th.start();
    }
}
