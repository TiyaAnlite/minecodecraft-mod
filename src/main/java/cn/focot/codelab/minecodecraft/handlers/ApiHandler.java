package cn.focot.codelab.minecodecraft.handlers;

import cn.focot.codelab.minecodecraft.MineCodeCraftMod;
import cn.focot.codelab.minecodecraft.helpers.PlayerData;
import cn.focot.codelab.minecodecraft.helpers.StatusHelper;
import com.google.common.reflect.TypeToken;
import io.nats.client.Message;
import io.nats.client.MessageHandler;
import net.minecraft.server.network.ServerPlayerEntity;

import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class ApiHandler extends AbstractHandler implements MessageHandler {
    public static final ApiHandler HANDLER = new ApiHandler();

    @Override
    public void onMessage(Message msg) throws InterruptedException {
        final String apiPrefix = MineCodeCraftMod.getConfig().getConfigBean().nats.prefix + ".api";
        final String subject = msg.getSubject();
        final String[] apiSubject = subject.substring(subject.lastIndexOf(apiPrefix) + 1).split("\\.");
        if (apiSubject.length < 1) return;
        switch (apiSubject[0]) {
        case "player":
            playerHandler(msg, apiSubject);
            break;
        case "block":
            blockHandler(msg, apiSubject);
            break;
        case "entity":
            entityHandler(msg, apiSubject);
            break;
        }
    }

    private static void playerHandler(Message msg, String[] args) {
        if (args.length < 2) return;
        switch (args[1]) {
            case "info":
                Type type = new TypeToken<Map<String, Object>>(){}.getType();
                Map<String, Object> data = gson.fromJson(new String(msg.getData(), StandardCharsets.UTF_8), type);
                String name = data.get("name").toString();
                ServerPlayerEntity player = getServer().getPlayerManager().getPlayer(name);
                if (player == null) {
                    response(msg, ApiResponse.ofError(1, "Player not found"));
                    return;
                }
                PlayerData playerData = StatusHelper.getPlayerData(player);
                if (playerData == null) {
                    response(msg, ApiResponse.ofError(2, "Player data not found"));
                    return;
                }
                Map<String, Object> result = new HashMap<>();
                result.put("name", player.getName().getString());
                result.put("uuid", player.getUuidAsString());
                result.put("onlineTime", playerData.getOnlineTime());
                result.put("lastOnlineTime", playerData.getLastOnlineTime());
                result.put("blockBreak", playerData.getBlockBreak());
                result.put("pos", player.getPos());
                result.put("ip", player.getIp());
                result.put("health", player.getHealth());
                response(msg, ApiResponse.ofData(result));
                break;
        }
    }

    private static void blockHandler(Message msg, String[] args) {

    }

    private static void entityHandler(Message msg, String[] args) {

    }

    private static void response(Message msg, Object response) {
        byte[] data = gson.toJson(response).getBytes(StandardCharsets.UTF_8);
        if (MineCodeCraftMod.hasNatsConnection() && msg.getReplyTo() != null) {
            MineCodeCraftMod.getNatsConnection().publish(msg.getReplyTo(), data);
        }
    }
}

class ApiResponse {
    public int code;
    public String msg;
    public Object data;

    public ApiResponse() {
        this.code = 0;
        this.msg = "ok";
    }

    public ApiResponse(int code, String msg, Object data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public static ApiResponse ofSuccess() {
        return new ApiResponse();
    }

    public static ApiResponse ofError(int code, String msg) {
        return new ApiResponse(code, msg, null);
    }

    public static ApiResponse ofData(Object data) {
        return new ApiResponse(0, "ok", data);
    }
}
