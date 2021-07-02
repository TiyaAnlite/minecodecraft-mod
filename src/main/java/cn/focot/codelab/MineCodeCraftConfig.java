package cn.focot.codelab;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public class MineCodeCraftConfig {
    String confPath;
    ConfigBean config;
    Logger LOGGER = LogManager.getLogger("MineCodeCraftConfig");
    static Gson gson = new Gson();
    static Gson gson_pretty = new GsonBuilder().setPrettyPrinting().create();

    MineCodeCraftConfig(@Nullable String path) {
        this.confPath = Objects.requireNonNullElse(path, "config/minecodecraft.json");
        this.loadConfig();
    }

    void loadConfig() {
        this.LOGGER.info("Reading config from %s".formatted(this.confPath));
        this.readConfigFromFile();
        this.writeConfigToFile();
    }

    void saveConfig() {
        this.writeConfigToFile();
        this.LOGGER.info("Saving config to %s".formatted(this.confPath));
    }

    boolean readConfigFromFile() {
        try {
            FileReader fileReader = new FileReader(this.confPath, StandardCharsets.UTF_8);
            int ch;
            StringBuilder sb = new StringBuilder();
            while ((ch = fileReader.read()) != -1) {
                sb.append((char) ch);
            }
            fileReader.close();
            this.config = gson.fromJson(sb.toString(), ConfigBean.class);
        } catch (IOException ignored) {
            this.LOGGER.info("Config file not found, create default config");
            this.config = new ConfigBean();
            return false;
        }
        return true;
    }

    boolean writeConfigToFile() {
        try {
            FileWriter fileWriter = new FileWriter(this.confPath, StandardCharsets.UTF_8);
            fileWriter.write(this.getConfigString(true));
            fileWriter.close();
        } catch (IOException e) {
            this.LOGGER.error("Write config file(%s) failed".formatted(this.confPath));
            MineCodeCraftHelper.getLogger().catching(e);
            return false;
        }
        return true;
    }

    ConfigBean getConfig() {
        return this.config;
    }

    public String getConfigString(boolean prettyFormat) {
        return prettyFormat ? gson_pretty.toJson(this.config) : gson.toJson(this.config);
    }
}

class ConfigBean {
    public GameRule gameRule = new GameRule();
    public TpPlayer tpPlayer = new TpPlayer();
    class GameRule {
        public boolean creeperExplosion = false;
    }
    class Pos {
        int x;
        int y;
        int z;
    }
    class TpPlayer {
        public int interval = 3;
        public Pos homePos = new Pos();
    }
}
