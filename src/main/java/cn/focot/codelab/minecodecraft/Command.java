package cn.focot.codelab.minecodecraft;

import cn.focot.codelab.minecodecraft.handlers.CreeperHandler;
import cn.focot.codelab.minecodecraft.helpers.PlayerHelper;
import cn.focot.codelab.minecodecraft.utils.MessageUtil;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static net.minecraft.command.CommandSource.suggestMatching;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class Command {
    final static String[] trueOrFalse = new String[]{"true", "false"};
    private static final Config config = MineCodeCraftMod.getConfig();
    public static final SimpleCommandExceptionType HOME_NOT_SET_EXCEPTION = new SimpleCommandExceptionType(Text.of("Home pos not set."));
    public static final SimpleCommandExceptionType INVALID_POSITION_EXCEPTION = new SimpleCommandExceptionType(new TranslatableText("commands.teleport.invalidPosition"));
    public static final SimpleCommandExceptionType UNSUPPORTED_ENTITY_EXCEPTION = new SimpleCommandExceptionType(Text.of("Unsupported entity type"));

    public static void registerCommand(CommandDispatcher<ServerCommandSource> dispatcher) {
        final LiteralArgumentBuilder<ServerCommandSource> literalArgumentBuilder = literal("minecodecraft")
                .then(literal("home").
                        executes((c) -> tpHome(c.getSource())))
                .then(literal("config").
                        requires(Command::needOp).
                        executes((c) -> showConfigString(c.getSource())).
                        then(argument("opt", StringArgumentType.word()).
                                suggests((c, b) -> suggestMatching(new String[]{"save", "reload"}, b)).
                                executes((c) -> configOpt(c.getSource(), getString(c, "opt")))))
                .then(literal("creeperExplosion").
                        requires(Command::needOp).
                        executes((c) -> showCreeperExplosion(c.getSource())).
                        then(argument("bool", StringArgumentType.word()).
                                suggests((c, b) -> suggestMatching(trueOrFalse, b)).
                                executes((c) -> setCreeperExplosion(c.getSource(), getString(c, "bool")))))
//                .then(literal("test").
//                        executes((c) -> testingFunc(c.getSource())))
                ;
        final LiteralArgumentBuilder<ServerCommandSource> tp_to_home = literal("home").
                executes((c) -> tpHome(c.getSource()));

        dispatcher.register(literalArgumentBuilder);
        dispatcher.register(tp_to_home);
    }

    static int testingFunc(ServerCommandSource source) {
        //MineCodeCraftHelper.LOGGER.info("ServerWorld: " + source.getMinecraftServer().getOverworld());
//        ServerPlayerEntity player;
//        try {
//            player = source.getPlayer();
//        } catch (CommandSyntaxException e) {
//            MineCodeCraftHelper.getLogger().error("Not a player");
//            return Command.SINGLE_SUCCESS;
//        }
//        Thread th = new Thread() {
//            @Override
//            public void run() {
//                MineCodeCraftHelper.getLogger().info("Sleep start");
//                try {
//                    Thread.sleep(2000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                MineCodeCraftHelper.getLogger().info("Sleep done");
//            }
//        };
//        th.setName("test@%s".formatted(player.getName().asString()));
//        th.start();
        return com.mojang.brigadier.Command.SINGLE_SUCCESS;
    }


    static boolean needOp(ServerCommandSource source) {
        return source.hasPermissionLevel(2);
    }

    static int tpHome(ServerCommandSource source) throws CommandSyntaxException {
        PlayerHelper.tpPlayer(source, config.getConfigBean().tpPlayer.homePos);
        return com.mojang.brigadier.Command.SINGLE_SUCCESS;
    }


    static int showCreeperExplosion(ServerCommandSource source) {
        Text text = Text.of("CreeperExplosion: %b".formatted(CreeperHandler.isCreeperExplode()));
        MessageUtil.replyMessage(source, text);
        return com.mojang.brigadier.Command.SINGLE_SUCCESS;
    }

    static int setCreeperExplosion(ServerCommandSource source, String newSet) {
        boolean s = Boolean.parseBoolean(newSet);
        CreeperHandler.setCreeperExplode(s);
        Text text = Text.of("CreeperExplosion set to %s".formatted(CreeperHandler.isCreeperExplode() ? "TRUE" : "FALSE"));
        MessageUtil.replyMessage(source, text);
        return com.mojang.brigadier.Command.SINGLE_SUCCESS;
    }

    static int showConfigString(ServerCommandSource source) {
        Text text = Text.of(config.getConfigString(false));
        MessageUtil.replyMessage(source, text);
        return com.mojang.brigadier.Command.SINGLE_SUCCESS;
    }

    static int configOpt(ServerCommandSource source, String opt) {
        Text reportText = Text.of("");
        switch (opt) {
            case "save" -> {
                config.saveConfig();
                reportText = Text.of("Config saved.");
            }
            case "reload" -> {
                config.loadConfig();
                reportText = Text.of("Config reload.");
            }
        }
        MessageUtil.replyMessage(source, reportText);
        return com.mojang.brigadier.Command.SINGLE_SUCCESS;
    }
}
