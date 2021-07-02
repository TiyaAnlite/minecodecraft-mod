package cn.focot.codelab;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.network.MessageType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.minecraft.world.explosion.Explosion;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MineCodeCraftHelper {
    private static final Logger LOGGER = LogManager.getLogger("MineCodeCraft");
    public static MinecraftServer server;
    private static final MineCodeCraftConfig config = new MineCodeCraftConfig(null);

    public static Logger getLogger() {
        return LOGGER;
    }

    public static MineCodeCraftConfig getConfig() {
        return config;
    }

    public static void onServerLoaded(MinecraftServer server) {
        LOGGER.info("Server loaded mixin");
        MineCodeCraftHelper.server = server;
        LOGGER.info("Minecraft server started: " + server.getVersion());
    }

    public static void onPlayerEvent(ServerPlayNetworkHandler handler, PacketSender sender, MinecraftServer server) {
        ServerPlayerEntity player = handler.player;
        LOGGER.info("new player join: %s[%s]".formatted(player.getName().asString(), player.getIp()));
        player.sendMessage(Text.of("Welcome! %s[%s]".formatted(player.getName().asString(), player.getIp())), false);
    }

    public static void onTick(MinecraftServer server) {
//        int now = (int) (System.currentTimeMillis() / 1000);
//        if (now - MineCodeCraftHelper.time >= 3) {
//            LOGGER.info("Tick at 3 sec");
//            MineCodeCraftHelper.time = now;
//        }
    }

    public static void onCreeperExplode(CreeperEntity creeper) {
        //LOGGER.info("Creeper %f %f %f explode!".formatted(creeper.getX(), creeper.getY(), creeper.getZ()));
        server.getPlayerManager().broadcastChatMessage(Text.of("Creeper[%d, %d, %d] explode!".formatted((int) creeper.getX(), (int) creeper.getY(), (int) creeper.getZ())), MessageType.SYSTEM, Util.NIL_UUID);
    }

    public static Explosion.DestructionType onCreeperCreateExplosion(Explosion.DestructionType t) {
        //return t;
        return config.getConfig().creeperExplosion ? t : Explosion.DestructionType.NONE;
    }

    public static boolean isCreeperExplode() {
        return config.getConfig().creeperExplosion;
    }

    public static void setCreeperExplode(boolean b) {
        config.getConfig().creeperExplosion = b;
    }

    public static void replyMessage(ServerCommandSource source, Text text) {
        try {
            source.getPlayer().sendMessage(text, false);
        } catch (CommandSyntaxException e) {
            source.sendFeedback(text, false);
        }
    }

    public static void broadcastMessage(ServerCommandSource source, Text text) {
        source.getMinecraftServer().getPlayerManager().broadcastChatMessage(text, MessageType.SYSTEM, Util.NIL_UUID);
    }
}
