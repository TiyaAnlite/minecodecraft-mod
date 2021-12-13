package cn.focot.codelab.minecodecraft.helpers;

public class CreeperHelper extends AbstractHelper {

    public static boolean isCreeperExplode() {
        return config.getConfigBean().gameRule.creeperExplosion;
    }

    public static void setCreeperExplode(boolean b) {
        config.getConfigBean().gameRule.creeperExplosion = b;
    }
}
