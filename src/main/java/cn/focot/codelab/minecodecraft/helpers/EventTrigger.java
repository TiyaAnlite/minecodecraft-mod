package cn.focot.codelab.minecodecraft.helpers;

import cn.focot.codelab.minecodecraft.MineCodeCraftMod;

public class EventTrigger extends AbstractHelper {
    public static void onConfigReload() {
        TipsHelper.reloadTips();
        MineCodeCraftMod.loadNatsConnection();
    }
}
