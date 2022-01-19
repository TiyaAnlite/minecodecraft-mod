package cn.focot.codelab.minecodecraft.helpers;

import cn.focot.codelab.minecodecraft.utils.MessageUtil;
import cn.focot.codelab.minecodecraft.utils.WorldUtil;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.network.MessageType;
import net.minecraft.network.packet.s2c.play.ExperienceBarUpdateS2CPacket;
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
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

import java.util.HashSet;
import java.util.List;

public class PlayerHelper extends AbstractHelper {
    private static final HashSet<String> teleportPlayer = new HashSet<>();

    public static void tpPlayer(ServerPlayerEntity player, ServerWorld world, Vec3d targetPos) {
        float f = MathHelper.wrapDegrees(player.getYaw());
        float g = MathHelper.wrapDegrees(player.getPitch());
        Vec3d playerPos = player.getPos();
        ServerWorld playerWorld = player.getWorld();
        String playerName = player.getName().asString();
        Thread th = new Thread(() -> {
            teleportPlayer.add(playerName);
            try {
                player.sendMessage(Text.of("§e已定位至§r[x:%.2f, y:%.2f, z:%.2f]§e, 将在§4%d§e秒后传送".formatted(targetPos.getX(), targetPos.getY(), targetPos.getZ(), config.getConfigBean().tpPlayer.interval)), true);
                int sec = 0;
                Identifier waitingSound = new Identifier("minecraft", "entity.experience_orb.pickup");
                Identifier teleportSound = new Identifier("minecraft", "entity.enderman.teleport");
                Vec3d playerEyePos;
                while (sec < config.getConfigBean().tpPlayer.interval) {
                    //Single player
                    //player.playSound(new SoundEvent(waitingSound), SoundCategory.MASTER, 1.0F, 1.0F);
                    if (player.isDead()) {
                        player.sendMessage(Text.of("§4检测到玩家死亡，传送计划被取消§r"), false);
                        return;
                    }
                    playerEyePos = player.getEyePos();
                    player.networkHandler.sendPacket(new PlaySoundIdS2CPacket(waitingSound, SoundCategory.MASTER, playerEyePos, 1.0F, 1.0F));
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
                if (player.isDead()) {
                    player.sendMessage(Text.of("§4检测到玩家死亡，传送计划被取消§r"), false);
                    return;
                }
                player.teleport(world, targetPos.getX(), targetPos.getY(), targetPos.getZ(), f, g);
                if (!world.equals(playerWorld)) {
                    // Fix experience bar when change world
                    LOGGER.info("Sync player experience bar");
                    player.networkHandler.sendPacket(new ExperienceBarUpdateS2CPacket(player.experienceProgress, player.totalExperience, player.experienceLevel));
                }
                //player.refreshPositionAfterTeleport(targetVec3d);
                player.setHeadYaw(f);
                LOGGER.info("Teleported %s to %.2f, %.2f, %.2f".formatted(player.getName().asString(), targetPos.getX(), targetPos.getY(), targetPos.getZ()));
                List<ServerPlayerEntity> serverPlayers = world.getPlayers();
                PlaySoundIdS2CPacket packet = new PlaySoundIdS2CPacket(teleportSound, SoundCategory.PLAYERS, targetPos, 1.0F, 1.0F);
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

    public static void here(ServerPlayerEntity player) {
        player.addStatusEffect(new StatusEffectInstance(StatusEffect.byRawId(24), config.getConfigBean().playerHereGlowingTime * 20));
        player.sendMessage(Text.of("§6你将会被高亮§5%d§6秒".formatted(config.getConfigBean().playerHereGlowingTime)), true);
        String playerName = player.getName().asString();
        ServerWorld world = player.getWorld();
        RegistryKey<World> worldKey = world.getRegistryKey();
        String worldName;
        if (worldKey.equals(World.OVERWORLD)) {
            worldName = "主世界";
        } else if (worldKey.equals(World.NETHER)) {
            worldName = "下界";
        } else if (worldKey.equals(World.END)) {
            worldName = "末地";
        } else {
            worldName = "未知世界";
        }
        BlockPos playerPos = new BlockPos(player.getPos());
        MessageUtil.broadcastPrefixMessage("§3%s§a在§6%s§r[x:%d, y:%d, z:%d]§a向大家打招呼".formatted(playerName, worldName, playerPos.getX(), playerPos.getY(), playerPos.getZ()), false, false);
        for (ServerPlayerEntity tellPlayer : getServer().getPlayerManager().getPlayerList()) {
            if (!(tellPlayer.equals(player))) {
                tellPlayer.sendMessage(MessageUtil.prefixMessage("§3%s§7与你".formatted(playerName) + (world.equals(tellPlayer.getWorld()) ? "相距约§a%.2f米".formatted(WorldUtil.distance(player.getPos(), tellPlayer.getPos())) : "不在同一个世界")), MessageType.SYSTEM, Util.NIL_UUID);
            }
        }
    }

    public static boolean isTeleportPlayer(String name) {
        return teleportPlayer.contains(name);
    }

    public static boolean isTeleportPlayer(ServerPlayerEntity player) {
        return isTeleportPlayer(player.getName().asString());
    }

    public static void joinMOTD(ServerPlayerEntity player) {
        String msg = "§7=======§r Welcome back to §e%s§7 =======§r".formatted(config.getConfigBean().serverName + " " + getServer().getVersion());
        msg += "\n今天是§e%s§r开服的第§e%d§r天".formatted(config.getConfigBean().serverName, StatusHelper.lunchedTime());
        if (config.getConfigBean().copyRight) {
            msg += "\n§7§oPowered by MineCodeCraft MOD %s © TiyaAnlite@Codelab§r".formatted(version);
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
