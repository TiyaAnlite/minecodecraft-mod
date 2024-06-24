package cn.focot.codelab.minecodecraft.event;

import cn.focot.codelab.minecodecraft.MineCodeCraftMod;
import com.google.gson.Gson;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.StringJoiner;

public abstract class EventMsg {
    public static final Gson gson = new Gson();
    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");  // RFC3339

    protected String msgSubject;
    public String time = dateFormat.format(new Date());
    public byte[] toBytes() {
        return EventMsg.gson.toJson(this).getBytes(StandardCharsets.UTF_8);
    }
    public void publish() {
        if (MineCodeCraftMod.hasNatsConnection()) {
            MineCodeCraftMod.getNatsConnection().publish(
                    new StringJoiner(MineCodeCraftMod.getConfig().getConfigBean().nats.prefix, ".", this.msgSubject).toString(),
                    this.toBytes());
        }
    };

    protected EventMsg(String msgSubject) {
        this.msgSubject = msgSubject;
    }
}
