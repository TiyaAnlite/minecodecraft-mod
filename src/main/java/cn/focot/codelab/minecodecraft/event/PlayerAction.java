package cn.focot.codelab.minecodecraft.event;

import cn.focot.codelab.minecodecraft.helpers.PlayerData;
import net.minecraft.server.network.ServerPlayerEntity;

public class PlayerAction extends EventMsg {
    public String action; // join, disconnect
    public String name;
    public String uuid;
    public String ip;
    public int onlineTime;
    public int blockBreak;

    protected PlayerAction(String msgSubject) {
        super(msgSubject);
    }

    public static PlayerAction of(ServerPlayerEntity player, PlayerData data, String action) {
        PlayerAction e = new PlayerAction("playerAction");
        e.action = action;
        e.name = player.getName().getString();
        e.uuid = player.getUuidAsString();
        e.ip = player.getIp();
        e.onlineTime = data.getOnlineTime();
        e.blockBreak = data.getBlockBreak();
        return e;
    }
}
