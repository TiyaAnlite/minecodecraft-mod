package cn.focot.codelab.minecodecraft.helpers;

import cn.focot.codelab.minecodecraft.MineCodeCraftMod;
import net.minecraft.server.network.ServerPlayerEntity;

public class PlayerWhereRequest extends AbstractHelper {
    String source;
    String target;
    long expire;

    PlayerWhereRequest(ServerPlayerEntity source, ServerPlayerEntity target) {
        this.source = source.getName().getString();
        this.target = target.getName().getString();
        this.expire = System.currentTimeMillis() + (config.getConfigBean().playerWhereRequestExpire) * 1000L;
    }

    PlayerWhereRequest(ServerPlayerEntity source, ServerPlayerEntity target, long expired) {
        this.source = source.getName().getString();
        this.target = target.getName().getString();
        this.expire = expired;
    }

    public String getSourceName() {
        return this.source;
    }

    public String getTargetName() {
        return this.target;
    }

    public ServerPlayerEntity getSource() {
        return getServer().getPlayerManager().getPlayer(this.source);
    }

    public ServerPlayerEntity getTarget() {
        return getServer().getPlayerManager().getPlayer(this.target);
    }

    public boolean isExpired() {
        return (this.expire <= System.currentTimeMillis());
    }
}
