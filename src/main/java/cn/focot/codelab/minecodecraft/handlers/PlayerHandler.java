package cn.focot.codelab.minecodecraft.handlers;

import cn.focot.codelab.minecodecraft.helpers.PlayerHelper;
import cn.focot.codelab.minecodecraft.helpers.StatusHelper;
import cn.focot.codelab.minecodecraft.utils.MessageUtil;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Util;

public class PlayerHandler extends AbstractHandler {

    public static void onPlayerJoin(ServerPlayNetworkHandler handler, PacketSender sender, MinecraftServer server) {
        ServerPlayerEntity player = handler.player;
        LOGGER.info("New player join: %s[%s]".formatted(player.getName().asString(), player.getIp()));
        PlayerHelper.joinMOTD(player);
        PlayerHelper.playerNotice(player);
        player.sendSystemMessage(MessageUtil.prefixMessage("Welcome! %s[%s]".formatted(player.getName().asString(), player.getIp())), Util.NIL_UUID);
        if (!StatusHelper.hasPlayerPosHistory(player.getName().asString())) {
            StatusHelper.updatePlayerPosHistory(player);
        }
    }

    public static void onPlayerDisconnect(ServerPlayNetworkHandler handler, MinecraftServer server) {
        ServerPlayerEntity player = handler.player;
        LOGGER.info("Player disconnect: %s[%s]".formatted(player.getName().asString(), player.getIp()));
    }

    public static boolean onPlayerDeath(ServerPlayerEntity player, DamageSource damageSource, float damageAmount) {
        MessageUtil.broadcastPrefixMessage("%s受到了致命剂量的伤害(%.2f)".formatted(player.getName().asString(), damageAmount), false, true);
        StatusHelper.updatePlayerPosHistory(player);
        return true;
    }


}
