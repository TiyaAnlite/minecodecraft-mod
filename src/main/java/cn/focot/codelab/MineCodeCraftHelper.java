package cn.focot.codelab;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.network.MessageType;
import net.minecraft.network.packet.s2c.play.PlaySoundIdS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ChunkTicketType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class MineCodeCraftHelper {
    private static final Logger LOGGER = LogManager.getLogger("MineCodeCraft");
    public static MinecraftServer server;
    private static final MineCodeCraftConfig config = new MineCodeCraftConfig(null);
    private static final SimpleCommandExceptionType HOME_NOT_SET_EXCEPTION = new SimpleCommandExceptionType(Text.of("Home pos not set."));
    private static final SimpleCommandExceptionType INVALID_POSITION_EXCEPTION = new SimpleCommandExceptionType(new TranslatableText("commands.teleport.invalidPosition"));
    private static final SimpleCommandExceptionType UNSUPPORTED_ENTITY_EXCEPTION = new SimpleCommandExceptionType(Text.of("Unsupported entity type"));

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
        server.getPlayerManager().broadcast(Text.of("Creeper[%d, %d, %d] explode!".formatted((int) creeper.getX(), (int) creeper.getY(), (int) creeper.getZ())), MessageType.SYSTEM, Util.NIL_UUID);
    }

    public static Explosion.DestructionType onCreeperCreateExplosion(Explosion.DestructionType t) {
        //return t;
        return config.getConfig().gameRule.creeperExplosion ? t : Explosion.DestructionType.NONE;
    }

    public static boolean isCreeperExplode() {
        return config.getConfig().gameRule.creeperExplosion;
    }

    public static void setCreeperExplode(boolean b) {
        config.getConfig().gameRule.creeperExplosion = b;
    }

    public static void replyMessage(ServerCommandSource source, Text text) {
        try {
            source.getPlayer().sendMessage(text, false);
        } catch (CommandSyntaxException e) {
            source.sendFeedback(text, false);
        }
    }

    public static void broadcastMessage(ServerCommandSource source, Text text) {
        source.getServer().getPlayerManager().broadcast(text, MessageType.SYSTEM, Util.NIL_UUID);
    }

    public static void tpPlayer(ServerCommandSource source, ConfigBean.Pos targetPos) throws CommandSyntaxException {
        ServerWorld serverOverworld = source.getServer().getOverworld();
        if (targetPos.x == 0 && targetPos.y == 0 && targetPos.z == 0) {
            throw HOME_NOT_SET_EXCEPTION.create();
        }
        BlockPos blockPos = new BlockPos(targetPos.x, targetPos.y, targetPos.z);
        if (!World.isValid(blockPos)) {
            throw INVALID_POSITION_EXCEPTION.create();
        }
        Entity target = source.getEntityOrThrow();
        float f = MathHelper.wrapDegrees(target.getYaw());
        float g = MathHelper.wrapDegrees(target.getPitch());
        if (!(target instanceof ServerPlayerEntity player)) {
            throw UNSUPPORTED_ENTITY_EXCEPTION.create();
        }
        Thread th = new Thread(() -> {
            try {
                replyMessage(source, Text.of("§ePoint to§r[x:%d, y:%d, z:%d]§e, will be teleport in §4%d§e seconds".formatted(targetPos.x, targetPos.y, targetPos.z, config.getConfig().tpPlayer.interval)));
                int sec = 0;
                Identifier waitingSound = new Identifier("minecraft", "entity.experience_orb.pickup");
                Identifier teleportSound = new Identifier("minecraft", "entity.enderman.teleport");
                Vec3d targetVec3d = new Vec3d(targetPos.x, targetPos.y, targetPos.z);
                Vec3d playerVec3d;
                while (sec < config.getConfig().tpPlayer.interval) {
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
        th.setName("%s@tpHome".formatted(player.getName().asString()));
        th.start();
    }
}
