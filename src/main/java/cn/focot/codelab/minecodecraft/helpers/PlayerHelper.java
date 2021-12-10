package cn.focot.codelab.minecodecraft.helpers;

import cn.focot.codelab.minecodecraft.*;
import cn.focot.codelab.minecodecraft.utils.MessageUtil;
import net.minecraft.network.packet.s2c.play.PlaySoundIdS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ChunkTicketType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.apache.logging.log4j.Logger;

import java.util.HashSet;
import java.util.List;

public class PlayerHelper {
    private static final Logger LOGGER = MineCodeCraftMod.getLogger();
    private static final Config config = MineCodeCraftMod.getConfig();
    private static HashSet<String> teleportPlayer = new HashSet<>();


    public static void tpPlayer(ServerPlayerEntity player, ServerWorld world, BlockPos targetPos) {
        float f = MathHelper.wrapDegrees(player.getYaw());
        float g = MathHelper.wrapDegrees(player.getPitch());
        BlockPos playerPos = new BlockPos(player.getPos());
        ServerWorld playerWorld = player.getWorld();
        String playerName = player.getName().asString();
        Thread th = new Thread(() -> {
            teleportPlayer.add(playerName);
            try {
                player.sendMessage(Text.of("§ePoint to§r[x:%d, y:%d, z:%d]§e, will be teleport in §4%d§e seconds".formatted(targetPos.getX(), targetPos.getY(), targetPos.getZ(), config.getConfigBean().tpPlayer.interval)), true);
                int sec = 0;
                Identifier waitingSound = new Identifier("minecraft", "entity.experience_orb.pickup");
                Identifier teleportSound = new Identifier("minecraft", "entity.enderman.teleport");
                Vec3d targetVec3d = new Vec3d(targetPos.getX(), targetPos.getY(), targetPos.getZ());
                Vec3d playerVec3d;
                while (sec < config.getConfigBean().tpPlayer.interval) {
                    //Single player
                    //player.playSound(new SoundEvent(waitingSound), SoundCategory.MASTER, 1.0F, 1.0F);
                    playerVec3d = player.getEyePos();
                    player.networkHandler.sendPacket(new PlaySoundIdS2CPacket(waitingSound, SoundCategory.MASTER, playerVec3d, 1.0F, 1.0F));
                    //LOGGER.info("Waiting at: %d".formatted(sec));
                    Thread.sleep(1000);
                    sec++;
                }
                ChunkPos chunkPos = new ChunkPos(new BlockPos(targetPos.getX(), targetPos.getY(), targetPos.getZ()));
                world.getChunkManager().addTicket(ChunkTicketType.POST_TELEPORT, chunkPos, 1, player.getId());
                //Single player
                //serverOverworld.playSound(targetPos.x, targetPos.y, targetPos.z, new SoundEvent(teleportSound), SoundCategory.PLAYERS, 1.0F, 1.0F, true);
                player.stopRiding();
                if (player.isSleeping()) {
                    player.wakeUp(true, true);
                }
                player.teleport(world, targetPos.getX(), targetPos.getY(), targetPos.getZ(), f, g);
                player.refreshPositionAfterTeleport(targetVec3d);
                player.setHeadYaw(f);
                LOGGER.info("Teleported %s to %d, %d, %d".formatted(player.getName().asString(), targetPos.getX(), targetPos.getY(), targetPos.getZ()));
                List<ServerPlayerEntity> serverPlayers = world.getPlayers();
                PlaySoundIdS2CPacket packet = new PlaySoundIdS2CPacket(teleportSound, SoundCategory.PLAYERS, targetVec3d, 1.0F, 1.0F);
                for (ServerPlayerEntity p : serverPlayers) {
                    p.networkHandler.sendPacket(packet);
                }
                // History
                StatusHelper.updatePlayerPosHistory(playerName, playerPos, playerWorld);
            } catch (InterruptedException e) {
                LOGGER.error("At tpPlayer", e);
            } finally {
                teleportPlayer.remove(playerName);
            }
        });
        th.setName("%s@tpPlayer".formatted(playerName));
        th.setDaemon(true);
        th.start();
    }

    public static boolean isTeleportPlayer(String name) {
        return teleportPlayer.contains(name);
    }

    public static boolean isTeleportPlayer(ServerPlayerEntity player) {
        return isTeleportPlayer(player.getName().asString());
    }

    public static void joinMOTD(ServerPlayerEntity player) {
        String msg = "§7=======§r Welcome back to §e%s§7 =======§r".formatted(config.getConfigBean().serverName + " " + MineCodeCraftMod.getMinecraftServer().getVersion());
        msg += "\n今天是§e%s§r开服的第§e%d§r天".formatted(config.getConfigBean().serverName, StatusHelper.lunchTime());
        if (config.getConfigBean().copyRight) {
            msg += "\n§7§oPowered by MineCodeCraft MOD %s © TiyaAnlite@Codelab§r".formatted(MineCodeCraftMod.getVersion());
        }
        //player.sendMessage(Text.of(msg), false);
        player.sendSystemMessage(Text.of(msg), Util.NIL_UUID);
    }

    public static void playerNotice(ServerPlayerEntity player) {
        for (String notice : config.getConfigBean().notice) {
            player.sendSystemMessage(MessageUtil.prefixMessage(notice), Util.NIL_UUID);
        }
    }
}
