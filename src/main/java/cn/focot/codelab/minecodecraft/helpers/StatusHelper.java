package cn.focot.codelab.minecodecraft.helpers;

import cn.focot.codelab.minecodecraft.Config;
import cn.focot.codelab.minecodecraft.MineCodeCraftMod;
import org.apache.logging.log4j.Logger;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class StatusHelper {
    private static final Logger LOGGER = MineCodeCraftMod.getLogger();
    private static final Config config = MineCodeCraftMod.getConfig();

    public static int lunchTime() {
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
}
