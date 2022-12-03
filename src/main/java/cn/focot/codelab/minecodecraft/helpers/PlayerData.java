package cn.focot.codelab.minecodecraft.helpers;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;

import java.text.SimpleDateFormat;
import java.util.Objects;

public class PlayerData extends AbstractHelper{
    protected int onlineTime;
    protected long lastOnlineTime;
    protected long thisOnlineTime;
    protected int blockBreak;
    protected PlayerPos posHistory;

    public PlayerData() {
        this.onlineTime = 0;
        this.lastOnlineTime = 0;
        this.blockBreak = 0;
    }

    protected PlayerData(int online, long lastOnline, int blockBreak) {
        this.onlineTime = online;
        this.lastOnlineTime = lastOnline;
        this.blockBreak = blockBreak;
    }

    public void login() {
        this.thisOnlineTime = System.currentTimeMillis() / 1000;
    }

    public void logout() {
        long now = System.currentTimeMillis() / 1000;
        this.lastOnlineTime = now;
        this.onlineTime += (now - this.thisOnlineTime);
    }
    public void breakBlock() {
        this.blockBreak++;
    }

    public int getOnlineTime() {
        return this.onlineTime;
    }

    public long getLastOnlineTime() {
        return this.lastOnlineTime;
    }

    public long getThisOnlineTime() {
        return this.thisOnlineTime;
    }
    public int getBlockBreak() {
        return this.blockBreak;
    }

    public PlayerPos getPosHistory() {
        return this.posHistory;
    }

    public void setPosHistory(PlayerPos pos) {
        this.posHistory = pos;
    }

    public boolean hasPosHistory() {
        return Objects.isNull(this.posHistory);
    }

    public static PlayerData ofNbt(NbtCompound nbt) {
        int onlineTime = 0;
        long lastOnlineTime = 0;
        int blockBreak = 0;
        if (nbt.contains("OnlineTime", NbtElement.INT_TYPE)) {
            onlineTime = nbt.getInt("OnlineTime");
        } else {
            LOGGER.warn("Player data not found:OnlineTime");
        }
        if (nbt.contains("LastOnlineTime", NbtElement.LONG_TYPE)) {
            lastOnlineTime = nbt.getLong("LastOnlineTime");
        } else {
            LOGGER.warn("Player data not found:LastOnlineTime");
        }
        if (nbt.contains("BlockBreak", NbtElement.INT_TYPE)) {
            blockBreak = nbt.getInt("BlockBreak");
        } else {
            LOGGER.warn("Player data not found:BlockBreak");
        }
        return new PlayerData(onlineTime, lastOnlineTime, blockBreak);
    }

    public NbtCompound writeNbt(NbtCompound nbt) {
        nbt.putInt("OnlineTime", this.onlineTime);
        nbt.putLong("LastOnlineTime", this.lastOnlineTime);
        nbt.putInt("BlockBreak", this.blockBreak);
        return nbt;
    }

    public String postPlayerInfo() {
        StringBuilder info = new StringBuilder();
        info.append("\n累计在线时间：%s".formatted(StatusHelper.onlineTime(getOnlineTime())));
        info.append("\n累计挖掘：%d".formatted(getBlockBreak()));
        if (getOnlineTime() != 0) {
            info.append("\n§7§o上次在线时间：%s§r".formatted(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(getLastOnlineTime() * 1000)));
        }
        return info.toString();
    }
}
