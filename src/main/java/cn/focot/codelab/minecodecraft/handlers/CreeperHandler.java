package cn.focot.codelab.minecodecraft.handlers;

import cn.focot.codelab.minecodecraft.Config;
import cn.focot.codelab.minecodecraft.MineCodeCraftMod;
import cn.focot.codelab.minecodecraft.utils.MessageUtil;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.world.explosion.Explosion;
import org.apache.logging.log4j.Logger;

public class CreeperHandler {
    private static final Logger LOGGER = MineCodeCraftMod.getLogger();
    private static final Config config = MineCodeCraftMod.getConfig();

    public static void onCreeperExplode(CreeperEntity creeper) {
        //LOGGER.info("Creeper %f %f %f explode!".formatted(creeper.getX(), creeper.getY(), creeper.getZ()));
        String broadcastString = "Creeper[%d, %d, %d] explode!".formatted((int) creeper.getX(), (int) creeper.getY(), (int) creeper.getZ());
        if (!(creeper.getTarget() == null)) {
            broadcastString += " Target is %s".formatted(creeper.getTarget().getName().asString());
        }
        MessageUtil.broadcastPrefixMessage(broadcastString, false, true);
    }

    public static Explosion.DestructionType onCreeperCreateExplosion(Explosion.DestructionType t) {
        //return t;
        return config.getConfigBean().gameRule.creeperExplosion ? t : Explosion.DestructionType.NONE;
    }

    public static boolean isCreeperExplode() {
        return config.getConfigBean().gameRule.creeperExplosion;
    }

    public static void setCreeperExplode(boolean b) {
        config.getConfigBean().gameRule.creeperExplosion = b;
    }
}
