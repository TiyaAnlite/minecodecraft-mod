package cn.focot.codelab.minecodecraft;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public class Config {
    String confPath;
    ConfigBean configBean;
    Logger LOGGER = LogManager.getLogger("MineCodeCraftConfig");
    public static Gson gson = new Gson();
    public static Gson gson_pretty = new GsonBuilder().setPrettyPrinting().create();

    Config(@Nullable String path) {
        this.confPath = Objects.requireNonNullElse(path, "config/minecodecraft.json");
        this.loadConfig();
    }

    public void loadConfig() {
        this.LOGGER.info("Reading config from %s".formatted(this.confPath));
        this.readConfigFromFile();
        this.writeConfigToFile();
    }

    public void saveConfig() {
        this.writeConfigToFile();
        this.LOGGER.info("Saving config to %s".formatted(this.confPath));
    }

    private boolean readConfigFromFile() {
        try {
            FileReader fileReader = new FileReader(this.confPath, StandardCharsets.UTF_8);
            int ch;
            StringBuilder sb = new StringBuilder();
            while ((ch = fileReader.read()) != -1) {
                sb.append((char) ch);
            }
            fileReader.close();
            this.configBean = gson.fromJson(sb.toString(), ConfigBean.class);
        } catch (IOException ignored) {
            this.LOGGER.info("Config file not found, create default config");
            this.configBean = new ConfigBean();
            return false;
        }
        return true;
    }

    private boolean writeConfigToFile() {
        try {
            FileWriter fileWriter = new FileWriter(this.confPath, StandardCharsets.UTF_8);
            fileWriter.write(this.getConfigString(true));
            fileWriter.close();
        } catch (IOException e) {
            this.LOGGER.error("Write config file(%s) failed".formatted(this.confPath));
            MineCodeCraftMod.getLogger().catching(e);
            return false;
        }
        return true;
    }

    public ConfigBean getConfigBean() {
        return this.configBean;
    }

    public String getConfigString(boolean prettyFormat) {
        return prettyFormat ? gson_pretty.toJson(this.configBean) : gson.toJson(this.configBean);
    }
}

