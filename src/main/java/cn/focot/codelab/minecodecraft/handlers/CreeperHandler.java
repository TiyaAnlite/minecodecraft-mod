package cn.focot.codelab.minecodecraft.handlers;

import cn.focot.codelab.minecodecraft.helpers.CreeperHelper;
import cn.focot.codelab.minecodecraft.utils.MessageUtil;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.world.explosion.Explosion;

public class CreeperHandler extends AbstractHandler {

    public static void onCreeperExplode(CreeperEntity creeper) {
        //LOGGER.info("Creeper %f %f %f explode!".formatted(creeper.getX(), creeper.getY(), creeper.getZ()));
        String broadcastString = "Creeper[%d, %d, %d] explode!".formatted((int) creeper.getX(), (int) creeper.getY(), (int) creeper.getZ());
        if (!(creeper.getTarget() == null)) {
            broadcastString += " Target is %s".formatted(creeper.getTarget().getName().asString());
        }
        MessageUtil.broadcastPrefixMessage(broadcastString, false, true);
    }

    public static Explosion.DestructionType onCreeperCreateExplosion(Explosion.DestructionType t) {
        return CreeperHelper.isCreeperExplode() ? t : Explosion.DestructionType.NONE;
    }

}
