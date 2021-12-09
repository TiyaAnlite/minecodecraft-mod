package cn.focot.codelab.minecodecraft.helpers;

import cn.focot.codelab.minecodecraft.*;
import cn.focot.codelab.minecodecraft.utils.MessageUtil;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.s2c.play.PlaySoundIdS2CPacket;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ChunkTicketType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class PlayerHelper {
    private static final Logger LOGGER = MineCodeCraftMod.getLogger();
    private static final Config config = MineCodeCraftMod.getConfig();


    public static void tpPlayer(ServerCommandSource source, ConfigBean.Pos targetPos) throws CommandSyntaxException {
        ServerWorld serverOverworld = source.getServer().getOverworld();
        if (targetPos.x == 0 && targetPos.y == 0 && targetPos.z == 0) {
            throw Command.HOME_NOT_SET_EXCEPTION.create();
        }
        BlockPos blockPos = new BlockPos(targetPos.x, targetPos.y, targetPos.z);
        if (!World.isValid(blockPos)) {
            throw Command.INVALID_POSITION_EXCEPTION.create();
        }
        Entity target = source.getEntityOrThrow();
        float f = MathHelper.wrapDegrees(target.getYaw());
        float g = MathHelper.wrapDegrees(target.getPitch());
        if (!(target instanceof ServerPlayerEntity player)) {
            throw Command.UNSUPPORTED_ENTITY_EXCEPTION.create();
        }
        Thread th = new Thread(() -> {
            try {
                MessageUtil.replyMessage(source, Text.of("§ePoint to§r[x:%d, y:%d, z:%d]§e, will be teleport in §4%d§e seconds".formatted(targetPos.x, targetPos.y, targetPos.z, config.getConfigBean().tpPlayer.interval)));
                int sec = 0;
                Identifier waitingSound = new Identifier("minecraft", "entity.experience_orb.pickup");
                Identifier teleportSound = new Identifier("minecraft", "entity.enderman.teleport");
                Vec3d targetVec3d = new Vec3d(targetPos.x, targetPos.y, targetPos.z);
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
                ChunkPos chunkPos = new ChunkPos(new BlockPos(targetPos.x, targetPos.y, targetPos.z));
                serverOverworld.getChunkManager().addTicket(ChunkTicketType.POST_TELEPORT, chunkPos, 1, target.getId());
                //Single player
                //serverOverworld.playSound(targetPos.x, targetPos.y, targetPos.z, new SoundEvent(teleportSound), SoundCategory.PLAYERS, 1.0F, 1.0F, true);
                player.stopRiding();
                if (player.isSleeping()) {
                    player.wakeUp(true, true);
                }
                player.teleport(serverOverworld, targetPos.x, targetPos.y, targetPos.z, f, g);
                player.refreshPositionAfterTeleport(targetVec3d);
                player.setHeadYaw(f);
                LOGGER.info("Teleported %s to %d, %d, %d".formatted(player.getName().asString(), targetPos.x, targetPos.y, targetPos.z));
                List<ServerPlayerEntity> serverPlayers = serverOverworld.getPlayers();
                PlaySoundIdS2CPacket packet = new PlaySoundIdS2CPacket(teleportSound, SoundCategory.PLAYERS, targetVec3d, 1.0F, 1.0F);
                for (ServerPlayerEntity p : serverPlayers) {
                    p.networkHandler.sendPacket(packet);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        th.setName("%s@tpPlayer".formatted(player.getName().asString()));
        th.setDaemon(true);
        th.start();
    }


    public static void joinMOTD(ServerPlayerEntity player) {
        String msg = "§7=======§r Welcome back to §e%s§7 =======§r".formatted(config.getConfigBean().serverName + " " + MineCodeCraftMod.getMinecraftServer().getVersion());
        msg += "\n今天是§e%s§r开服的第§e%d§r天".formatted(config.getConfigBean().serverName, StatusHelper.lunchTime());
        player.sendMessage(Text.of(msg), false);
    }
}
