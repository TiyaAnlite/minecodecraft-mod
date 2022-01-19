package cn.focot.codelab.minecodecraft;

import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;

public class ConfigBean {
    public GameRule gameRule = new GameRule();
    public TpPlayer tpPlayer = new TpPlayer();
    public String serverName = "MineCodeCraft";
    public String lunchTime = new SimpleDateFormat("yyyy-MM-dd").format(new Date().getTime());
    public Tips tips = new Tips();
    public List<String> notice = new LinkedList<>();
    public int worldAutoSaveInterval = 0;
    public int playerHereGlowingTime = 30;
    public int playerLatencyUpdateInterval = 30;
    public boolean copyRight = true;

    public class GameRule {
        public boolean creeperExplosion = false;
    }

    public class Pos {
        public int x;
        public int y;
        public int z;
    }

    public class TpPlayer {
        public int interval = 3;
        public Pos homePos = new Pos();
    }

    public class Tips {
        public int interval = 20 * 60;
        public List<String> tips = new LinkedList<>();
    }
}
