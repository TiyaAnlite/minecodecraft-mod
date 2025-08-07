package cn.focot.codelab.minecodecraft.helpers;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.math.GlobalPos;

import java.text.SimpleDateFormat;
import java.util.Objects;
import java.util.Optional;

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

    protected PlayerData(int online, long lastOnline, int blockBreak, PlayerPos posHistory) {
        this.onlineTime = online;
        this.lastOnlineTime = lastOnline;
        this.blockBreak = blockBreak;
        this.posHistory = posHistory;
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

    public static PlayerData ofView(ReadView view) {
        int onlineTime = 0;
        long lastOnlineTime = 0;
        int blockBreak = 0;
        Optional<Integer> onlineView = view.getOptionalInt("OnlineTime");
        if (onlineView.isPresent()) {
            onlineTime = onlineView.get();
        } else {
            LOGGER.info("Player data not found:OnlineTime");
        }
        Optional<Long> lastOnlineTimeView =  view.getOptionalLong("LastOnlineTime");
        if (lastOnlineTimeView.isPresent()) {
            lastOnlineTime = lastOnlineTimeView.get();
        }
        Optional<Integer> blockBreakView = view.getOptionalInt("BlockBreak");
        if (blockBreakView.isPresent()) {
            blockBreak = blockBreakView.get();
        } else {
            LOGGER.info("Player data not found:BlockBreak");
        }
        Optional<GlobalPos> posHistoryView = view.read("PosHistory", GlobalPos.CODEC);
        if (posHistoryView.isPresent()) {
            GlobalPos pos = posHistoryView.get();
            ServerWorld world = getServer().getWorld(pos.dimension());
            if (Objects.isNull(world)) {
                LOGGER.error("World not found when load pos history: %s".formatted(pos.dimension().getValue().toString()));
                return new PlayerData(onlineTime, lastOnlineTime, blockBreak);
            }
            return new PlayerData(onlineTime, lastOnlineTime, blockBreak, new PlayerPos(pos.pos(), world));
        } else {
            return new PlayerData(onlineTime, lastOnlineTime, blockBreak);
        }
    }

    @Deprecated
    public static PlayerData ofNbt(NbtCompound nbt) {
        int onlineTime = 0;
        long lastOnlineTime = 0;
        int blockBreak = 0;
        Optional<Integer> onlineNbt = nbt.getInt("OnlineTime");
        if (onlineNbt.isPresent()) {
            onlineTime = onlineNbt.get();
        } else {
            LOGGER.info("Player data not found:OnlineTime");
        }
        Optional<Long> lastOnlineTimeNbt =  nbt.getLong("LastOnlineTime");
        if (lastOnlineTimeNbt.isPresent()) {
            lastOnlineTime = lastOnlineTimeNbt.get();
        } else {
            LOGGER.info("Player data not found:LastOnlineTime");
        }
        Optional<Integer> blockBreakNbt = nbt.getInt("BlockBreak");
        if (blockBreakNbt.isPresent()) {
            blockBreak = blockBreakNbt.get();
        } else {
            LOGGER.info("Player data not found:BlockBreak");
        }
        return new PlayerData(onlineTime, lastOnlineTime, blockBreak);
    }

    public void writeView(WriteView view) {
        view.putInt("OnlineTime", this.onlineTime);
        view.putLong("LastOnlineTime", this.lastOnlineTime);
        view.putInt("BlockBreak", this.blockBreak);
        if (!Objects.isNull(this.posHistory)) {
            GlobalPos pos = GlobalPos.create(this.posHistory.world.getRegistryKey(), this.posHistory.pos);
            view.put("PosHistory", GlobalPos.CODEC, pos);
        }
    }

    @Deprecated
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
