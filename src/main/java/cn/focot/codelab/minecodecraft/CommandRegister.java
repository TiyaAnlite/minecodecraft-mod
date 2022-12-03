package cn.focot.codelab.minecodecraft;

import cn.focot.codelab.minecodecraft.handlers.AbstractHandler;
import cn.focot.codelab.minecodecraft.helpers.*;
import cn.focot.codelab.minecodecraft.utils.MessageUtil;
import cn.focot.codelab.minecodecraft.utils.WorldUtil;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static net.minecraft.command.CommandSource.suggestMatching;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class CommandRegister {
    public final static String[] trueOrFalse = new String[]{"true", "false"};
    private static final Config config = MineCodeCraftMod.getConfig();
    public static final SimpleCommandExceptionType SERVER_SAVE_FAILED_EXCEPTION = new SimpleCommandExceptionType(Text.of("§c尝试存档时失败"));
    public static final SimpleCommandExceptionType HOME_NOT_SET_EXCEPTION = new SimpleCommandExceptionType(Text.of("§cHome pos not set.§r"));
    public static final SimpleCommandExceptionType INVALID_POSITION_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("commands.teleport.invalidPosition"));
    public static final SimpleCommandExceptionType UNSUPPORTED_ENTITY_EXCEPTION = new SimpleCommandExceptionType(Text.of("§c不支持的实体类型§r"));
    public static final SimpleCommandExceptionType TELEPORT_IN_PROGRESS_EXCEPTION = new SimpleCommandExceptionType(Text.of("§c传送进行中，请耐心等待上一个传送完成§r"));
    public static final SimpleCommandExceptionType TELEPORT_TO_VOID_EXCEPTION = new SimpleCommandExceptionType(Text.of("§c无法传送到虚空§r"));

    public static void registerCommand(CommandDispatcher<ServerCommandSource> dispatcher) {
        final LiteralArgumentBuilder<ServerCommandSource> tpToHome = literal("home")
                .executes((c) -> tpHome(c.getSource()));
        final LiteralArgumentBuilder<ServerCommandSource> tpToBack = literal("back")
                .executes((c) -> tpBack(c.getSource()));
        final LiteralArgumentBuilder<ServerCommandSource> atHere = literal("here")
                .executes((c) -> playerHere(c.getSource()));
        final LiteralArgumentBuilder<ServerCommandSource> atWhere = literal("where")
                .then(argument("player", EntityArgumentType.player())
                        .executes((c) -> playerWhere(c.getSource(), EntityArgumentType.getPlayer(c, "player"))));
        LiteralCommandNode<ServerCommandSource> homeCommand = dispatcher.register(tpToHome);
        LiteralCommandNode<ServerCommandSource> backCommand = dispatcher.register(tpToBack);
        LiteralCommandNode<ServerCommandSource> hereCommand = dispatcher.register(atHere);
        LiteralCommandNode<ServerCommandSource> whereCommand = dispatcher.register(atWhere);

        final LiteralArgumentBuilder<ServerCommandSource> literalArgumentBuilder = literal("minecodecraft")
                .then(literal("save")
                        .requires(CommandRegister::needOp)
                        .executes((c) -> saveServer(c.getSource())))
                .then(literal("home")
                        .redirect(homeCommand))
                .then(literal("back")
                        .redirect(backCommand))
                .then(literal("here")
                        .redirect(hereCommand))
                .then(literal("where")
                        .redirect(whereCommand))
                .then(literal("player")
                        .requires(CommandRegister::needOp)
                        .then(argument("player", EntityArgumentType.player())
                                .then(literal("info")
                                        .executes((c) -> playerInfo(c.getSource(), EntityArgumentType.getPlayer(c, "player"))))))
                .then(literal("config")
                        .requires(CommandRegister::needOp)
                        .executes((c) -> showConfigString(c.getSource()))
                        .then(argument("opt", StringArgumentType.string())
                                .suggests((c, b) -> suggestMatching(new String[]{"save", "reload"}, b))
                                .executes((c) -> configOpt(c.getSource(), getString(c, "opt")))))
                .then(literal("creeperExplosion")
                        .requires(CommandRegister::needOp)
                        .executes((c) -> showCreeperExplosion(c.getSource()))
                        .then(argument("bool", StringArgumentType.string())
                                .suggests((c, b) -> suggestMatching(trueOrFalse, b))
                                .executes((c) -> setCreeperExplosion(c.getSource(), getString(c, "bool")))))
//              .then(literal("test")
//                      .executes((c) -> testingFunc(c.getSource())))
                ;

        dispatcher.register(literalArgumentBuilder);
    }

/*
    static int testingFunc(ServerCommandSource source) {
        try {
            System.out.println("Sleep start.");
            Thread.sleep(5000);
            System.out.println("Sleep end.");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return Command.SINGLE_SUCCESS;
    }
*/


    static private boolean needOp(ServerCommandSource source) {
        return source.hasPermissionLevel(2);
    }

    static private int saveServer(ServerCommandSource source) throws CommandSyntaxException {
        if (ServerHelper.saveServer(false, true, true)) {
            return Command.SINGLE_SUCCESS;
        } else {
            throw SERVER_SAVE_FAILED_EXCEPTION.create();
        }
    }

    static private ServerPlayerEntity getPlayer(ServerCommandSource source) throws CommandSyntaxException {
        Entity target = source.getEntityOrThrow();
        return getPlayer(target);
    }

    static private ServerPlayerEntity getPlayer(Entity entity) throws CommandSyntaxException {
        if (!(entity instanceof ServerPlayerEntity player)) {
            throw UNSUPPORTED_ENTITY_EXCEPTION.create();
        }
        return player;
    }


    static private int tpHome(ServerCommandSource source) throws CommandSyntaxException {
        ConfigBean.Pos homePos = config.getConfigBean().tpPlayer.homePos;
        if (homePos.x == 0 && homePos.y == 0 && homePos.z == 0) {
            throw CommandRegister.HOME_NOT_SET_EXCEPTION.create();
        }
        Vec3d targetPos = new Vec3d(homePos.x, homePos.y, homePos.z);
        ServerWorld world = source.getServer().getOverworld();
        if (!World.isValid(new BlockPos(targetPos))) {
            throw INVALID_POSITION_EXCEPTION.create();
        }
        ServerPlayerEntity player = getPlayer(source);
        if (PlayerHelper.isTeleportPlayer(player)) {
            throw TELEPORT_IN_PROGRESS_EXCEPTION.create();
        }
        PlayerHelper.tpPlayer(player, world, targetPos);
        return Command.SINGLE_SUCCESS;
    }

    static private int tpBack(ServerCommandSource source) throws CommandSyntaxException {
        ServerPlayerEntity player = getPlayer(source);
        PlayerPos playerPos = StatusHelper.getPlayerPosHistory(player);
        if (playerPos == null) {
            throw INVALID_POSITION_EXCEPTION.create();
        }
        BlockPos intPos = new BlockPos(playerPos.getPos());
        if (!(World.isValid(intPos))) {
            throw INVALID_POSITION_EXCEPTION.create();
        }
        if (!(WorldUtil.isCanTeleportPos(playerPos.getWorld(), intPos))) {
            throw TELEPORT_TO_VOID_EXCEPTION.create();
        }
        if (PlayerHelper.isTeleportPlayer(player)) {
            throw TELEPORT_IN_PROGRESS_EXCEPTION.create();
        }
        PlayerHelper.tpPlayer(player, playerPos.getWorld(), playerPos.getPos());
        return Command.SINGLE_SUCCESS;
    }

    static private int playerHere(ServerCommandSource source) throws CommandSyntaxException {
        PlayerHelper.here(getPlayer(source));
        return Command.SINGLE_SUCCESS;
    }

    static private int playerWhere(ServerCommandSource source, Entity entity) throws CommandSyntaxException {
        ServerPlayerEntity targetPlayer = getPlayer(entity);
        ServerPlayerEntity sourcePlayer = getPlayer(source);
        if (needOp(source) || targetPlayer.equals(sourcePlayer)) {
            PlayerHelper.here(targetPlayer);
        } else {
            PlayerHelper.whereRequest(targetPlayer, sourcePlayer);
        }
        return Command.SINGLE_SUCCESS;
    }

    static private int playerInfo(ServerCommandSource source, Entity entity)throws CommandSyntaxException  {
        ServerPlayerEntity player = getPlayer(entity);
        StringBuilder replay = new StringBuilder();
        replay.append("\"§7=======§r PlayerInfo for §e%s§7 =======§r".formatted(player.getName().getString()));
        replay.append("\n§7§oUUID: %s§r".formatted(player.getUuidAsString()));
        replay.append(PlayerHelper.checkedPlayerData(player).postPlayerInfo());
        MessageUtil.replyCommandMessage(source, Text.of(replay.toString()));
        return Command.SINGLE_SUCCESS;
    }

    static private int showCreeperExplosion(ServerCommandSource source) {
        Text text = Text.of("CreeperExplosion: %b".formatted(CreeperHelper.isCreeperExplode()));
        MessageUtil.replyCommandMessage(source, text);
        return Command.SINGLE_SUCCESS;
    }

    static private int setCreeperExplosion(ServerCommandSource source, String newSet) {
        boolean s = Boolean.parseBoolean(newSet);
        CreeperHelper.setCreeperExplode(s);
        Text text = Text.of("CreeperExplosion set to %s".formatted(CreeperHelper.isCreeperExplode() ? "TRUE" : "FALSE"));
        MessageUtil.replyCommandMessage(source, text);
        return Command.SINGLE_SUCCESS;
    }

    static private int showConfigString(ServerCommandSource source) {
        Text text = Text.of(config.getConfigString(false));
        MessageUtil.replyCommandMessage(source, text);
        return Command.SINGLE_SUCCESS;
    }

    static private int configOpt(ServerCommandSource source, String opt) {
        Text reportText = Text.of("");
        switch (opt) {
            case "save" -> {
                config.saveConfig();
                reportText = Text.of("Config saved.");
            }
            case "reload" -> {
                config.loadConfig();
                EventTrigger.onConfigReload();
                reportText = Text.of("Config reload.");
            }
        }
        MessageUtil.replyCommandMessage(source, reportText);
        return Command.SINGLE_SUCCESS;
    }


}
