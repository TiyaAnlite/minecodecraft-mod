package cn.focot.codelab.minecodecraft.helpers;

public class EventTrigger extends AbstractHelper {
    public static void onConfigReload() {
        TipsHelper.reloadTips();
    }
}
