package cn.focot.codelab.minecodecraft.handlers;

import cn.focot.codelab.minecodecraft.Config;
import cn.focot.codelab.minecodecraft.MineCodeCraftMod;
import cn.focot.codelab.minecodecraft.helpers.PlayerHelper;
import cn.focot.codelab.minecodecraft.utils.MessageUtil;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.apache.logging.log4j.Logger;

public class PlayerHandler {
    private static final Logger LOGGER = MineCodeCraftMod.getLogger();
    private static final Config config = MineCodeCraftMod.getConfig();

    public static void onPlayerJoin(ServerPlayNetworkHandler handler, PacketSender sender, MinecraftServer server) {
        ServerPlayerEntity player = handler.player;
        LOGGER.info("New player join: %s[%s]".formatted(player.getName().asString(), player.getIp()));
        PlayerHelper.joinMOTD(player);
        player.sendMessage(MessageUtil.prefixMessage("Welcome! %s[%s]".formatted(player.getName().asString(), player.getIp())), false);
    }

    public static void onPlayerDisconnect(ServerPlayNetworkHandler handler, MinecraftServer server) {
        ServerPlayerEntity player = handler.player;
        LOGGER.info("Player disconnect: %s[%s]".formatted(player.getName().asString(), player.getIp()));
    }

    public static boolean onPlayerDeath(ServerPlayerEntity player, DamageSource damageSource, float damageAmount) {
        MessageUtil.broadcastPrefixMessage("%s受到了致命剂量的伤害(%.2f)".formatted(player.getName().asString(), damageAmount));
        return true;
    }

}
