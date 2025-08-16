package cn.focot.codelab.minecodecraft;

import cn.focot.codelab.minecodecraft.handlers.ApiHandler;
import cn.focot.codelab.minecodecraft.handlers.PlayerHandler;
import cn.focot.codelab.minecodecraft.handlers.ServerHandler;
import io.nats.client.Connection;
import io.nats.client.Nats;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.minecraft.server.MinecraftServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Objects;

public class MineCodeCraftMod implements ModInitializer {
    private static final String MOD_ID = "minecodecraft";
    private static final Logger LOGGER = LoggerFactory.getLogger("MineCodeCraft");
    private static final Config config = new Config(null);
    private static MinecraftServer minecraftServer;
    private static String version;
    private static String description;

    private static Connection nc = null;
    private static String lastNcServer = "";

    public static Logger getLogger() {
        return LOGGER;
    }

    public static Config getConfig() {
        return config;
    }

    public static void setMinecraftServer(MinecraftServer minecraftServer) {
        MineCodeCraftMod.minecraftServer = minecraftServer;
    }

    public static MinecraftServer getMinecraftServer() {
        return minecraftServer;
    }

    @Override
    public void onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.

        FabricLoader loader = FabricLoader.getInstance();
        ModMetadata modMetadata = loader.getModContainer(MOD_ID).orElseThrow(RuntimeException::new).getMetadata();
        version = modMetadata.getVersion().getFriendlyString();
        description = modMetadata.getDescription();
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> CommandRegister.registerCommand(dispatcher));
        ServerPlayConnectionEvents.JOIN.register(PlayerHandler::onPlayerJoin);
        ServerPlayConnectionEvents.DISCONNECT.register(PlayerHandler::onPlayerDisconnect);
        ServerLivingEntityEvents.ALLOW_DEATH.register(PlayerHandler::onPlayerDeath);
        ServerLifecycleEvents.SERVER_STARTING.register(ServerHandler::onServerLoaded);
        ServerLifecycleEvents.SERVER_STOPPING.register(ServerHandler::onServerStopping);
        ServerLifecycleEvents.SERVER_STOPPED.register(ServerHandler::onServerStopped);
        ServerTickEvents.START_WORLD_TICK.register(ServerHandler::onWorldTick);
        MineCodeCraftMod.loadNatsConnection();
    }

    public static void loadNatsConnection() {
        if (config.getConfigBean().nats.server != null && !Objects.equals(config.getConfigBean().nats.server, "") && !Objects.equals(lastNcServer, config.getConfigBean().nats.server)) {
            String natsServer = config.getConfigBean().nats.server;
            String prefix = config.getConfigBean().nats.prefix;
            if (nc != null) {
                try {
                    nc.close();
                    nc = null;
                } catch (InterruptedException ignored) {
                }
            }
            LOGGER.info("Connect to NATS: {}", natsServer);
            try {
                lastNcServer = natsServer;
                nc = Nats.connect(natsServer);
                nc.createDispatcher(ApiHandler.HANDLER).subscribe(prefix + ".api.>");
            } catch (IOException | InterruptedException e) {
                LOGGER.error("Failed to connect to nats server", e);
            }
        }
    }

    public static String getModId() {
        return MOD_ID;
    }

    public static String getVersion() {
        return version;
    }

    public static String getDescription() {
        return description;
    }

    public static boolean hasNatsConnection() {
        return nc != null;
    }

    public static Connection getNatsConnection() {
        return nc;
    }
}
