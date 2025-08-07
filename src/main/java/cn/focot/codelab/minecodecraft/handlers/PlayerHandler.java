package cn.focot.codelab.minecodecraft.handlers;

import cn.focot.codelab.minecodecraft.event.PlayerAction;
import cn.focot.codelab.minecodecraft.helpers.PlayerData;
import cn.focot.codelab.minecodecraft.helpers.PlayerHelper;
import cn.focot.codelab.minecodecraft.helpers.StatusHelper;
import cn.focot.codelab.minecodecraft.utils.MessageUtil;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;

import java.util.Optional;

public class PlayerHandler extends AbstractHandler {

    public static void onPlayerJoin(ServerPlayNetworkHandler handler, PacketSender sender, MinecraftServer server) {
        ServerPlayerEntity player = handler.player;
        LOGGER.info("New player join: %s[%s]".formatted(player.getName().getString(), player.getIp()));
        PlayerData playerData = PlayerHelper.checkedPlayerData(player);
        playerData.login();
        PlayerHelper.joinMOTD(player);
        PlayerHelper.sendPlayerNotice(player);
        PlayerAction.of(player, playerData, "join").publish();
        player.sendMessage(MessageUtil.prefixMessage("Welcome! %s[%s]".formatted(player.getName().getString(), player.getIp())));
        if (!StatusHelper.hasPlayerPosHistory(player)) {
            StatusHelper.updatePlayerPosHistory(player);
        }
    }

    public static void onPlayerDisconnect(ServerPlayNetworkHandler handler, MinecraftServer server) {
        ServerPlayerEntity player = handler.player;
        PlayerData playerData = PlayerHelper.checkedPlayerData(player);
        playerData.logout();
        PlayerAction.of(player, playerData, "disconnect").publish();
        LOGGER.info("Player disconnect: %s[%s]".formatted(player.getName().getString(), player.getIp()));
    }

    public static boolean onPlayerDeath(LivingEntity entity, DamageSource damageSource, float damageAmount) {
        if (entity instanceof ServerPlayerEntity player) {
            MessageUtil.broadcastPrefixMessage("%s受到了致命剂量的伤害(%.2f)".formatted(player.getName().getString(), damageAmount), false, true);
            StatusHelper.updatePlayerPosHistory(player);
        }
        return true;
    }

    public static void onPlayerReadNbt(ServerPlayerEntity player, ReadView view) {
        LOGGER.info("Reading player data: %s".formatted(player.getName().getString()));
        Optional<ReadView> mccView = view.getOptionalReadView("minecodecraft");
        if (mccView.isPresent()) {
            StatusHelper.readPlayerData(player, mccView.get());
        } else {
            StatusHelper.newPlayerData(player);
        }
    }

    public static void onPlayerWriteNbt(ServerPlayerEntity player, WriteView view) {
        LOGGER.info("Saving player data: %s".formatted(player.getName().getString()));
        StatusHelper.writePlayerData(player, view.get("minecodecraft"));
    }
}
