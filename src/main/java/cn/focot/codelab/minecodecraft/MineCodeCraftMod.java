package cn.focot.codelab.minecodecraft;

import cn.focot.codelab.minecodecraft.handlers.PlayerHandler;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.minecraft.server.MinecraftServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MineCodeCraftMod implements ModInitializer {
    private static final String MOD_ID = "minecodecraft";
    private static final Logger LOGGER = LoggerFactory.getLogger("MineCodeCraft");
    private static final Config config = new Config(null);
    private static MinecraftServer minecraftServer;
    private static String version;
    private static String description;

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
}