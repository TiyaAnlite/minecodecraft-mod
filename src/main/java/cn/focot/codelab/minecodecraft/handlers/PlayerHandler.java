package cn.focot.codelab.minecodecraft.handlers;

import cn.focot.codelab.minecodecraft.helpers.PlayerData;
import cn.focot.codelab.minecodecraft.helpers.PlayerHelper;
import cn.focot.codelab.minecodecraft.helpers.StatusHelper;
import cn.focot.codelab.minecodecraft.utils.MessageUtil;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;

public class PlayerHandler extends AbstractHandler {

    public static void onPlayerJoin(ServerPlayNetworkHandler handler, PacketSender sender, MinecraftServer server) {
        ServerPlayerEntity player = handler.player;
        LOGGER.info("New player join: %s[%s]".formatted(player.getName().getString(), player.getIp()));
        PlayerData playerData = PlayerHelper.checkedPlayerData(player);
        playerData.login();
        PlayerHelper.joinMOTD(player);
        PlayerHelper.sendPlayerNotice(player);
        player.sendMessage(MessageUtil.prefixMessage("Welcome! %s[%s]".formatted(player.getName().getString(), player.getIp())));
        if (!StatusHelper.hasPlayerPosHistory(player)) {
            StatusHelper.updatePlayerPosHistory(player);
        }
    }

    public static void onPlayerDisconnect(ServerPlayNetworkHandler handler, MinecraftServer server) {
        ServerPlayerEntity player = handler.player;
        PlayerData playerData = PlayerHelper.checkedPlayerData(player);
        playerData.logout();
        LOGGER.info("Player disconnect: %s[%s]".formatted(player.getName().getString(), player.getIp()));
    }

    public static boolean onPlayerDeath(LivingEntity entity, DamageSource damageSource, float damageAmount) {
        if (entity instanceof ServerPlayerEntity player) {
            MessageUtil.broadcastPrefixMessage("%s受到了致命剂量的伤害(%.2f)".formatted(player.getName().getString(), damageAmount), false, true);
            StatusHelper.updatePlayerPosHistory(player);
        }
        return true;
    }

    public static void onPlayerReadNbt(ServerPlayerEntity player, NbtCompound nbt) {
        LOGGER.info("Reading player data: %s".formatted(player.getName().getString()));
        if (nbt.contains("minecodecraft", NbtElement.COMPOUND_TYPE)) {
            StatusHelper.readPlayerData(player, nbt.getCompound("minecodecraft"));
        } else {
            StatusHelper.newPlayerData(player);
        }
    }

    public static void onPlayerWriteNbt(ServerPlayerEntity player, NbtCompound nbt) {
        LOGGER.info("Saving player data: %s".formatted(player.getName().getString()));
        nbt.put("minecodecraft", StatusHelper.writePlayerData(player, new NbtCompound()));
    }
}
