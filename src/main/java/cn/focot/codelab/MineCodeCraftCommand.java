package cn.focot.codelab;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static net.minecraft.command.CommandSource.suggestMatching;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class MineCodeCraftCommand {
    final static String[] trueOrFalse = new String[]{"true", "false"};

    public static void registerCommand(CommandDispatcher<ServerCommandSource> dispatcher) {
        final LiteralArgumentBuilder<ServerCommandSource> literalArgumentBuilder = literal("minecodecraft")
                .then(literal("home").
                        executes((c) -> tpHome(c.getSource())))
                .then(literal("config").
                        requires(MineCodeCraftCommand::needOp).
                        executes((c) -> showConfigString(c.getSource())).
                        then(argument("opt", StringArgumentType.word()).
                                suggests((c, b) -> suggestMatching(new String[]{"save", "reload"}, b)).
                                executes((c) -> configOpt(c.getSource(), getString(c, "opt")))))
                .then(literal("creeperExplosion").
                        requires(MineCodeCraftCommand::needOp).
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
        return Command.SINGLE_SUCCESS;
    }


    static boolean needOp(ServerCommandSource source) {
        return source.hasPermissionLevel(2);
    }

    static int tpHome(ServerCommandSource source) throws CommandSyntaxException {
        MineCodeCraftHelper.tpPlayer(source, MineCodeCraftHelper.getConfig().getConfig().tpPlayer.homePos);
        return Command.SINGLE_SUCCESS;
    }


    static int showCreeperExplosion(ServerCommandSource source) {
        Text text = Text.of("CreeperExplosion: %b".formatted(MineCodeCraftHelper.isCreeperExplode()));
        MineCodeCraftHelper.replyMessage(source, text);
        return Command.SINGLE_SUCCESS;
    }

    static int setCreeperExplosion(ServerCommandSource source, String newSet) {
        boolean s = Boolean.parseBoolean(newSet);
        MineCodeCraftHelper.setCreeperExplode(s);
        Text text = Text.of("CreeperExplosion set to %s".formatted(MineCodeCraftHelper.isCreeperExplode() ? "TRUE" : "FALSE"));
        MineCodeCraftHelper.replyMessage(source, text);
        return Command.SINGLE_SUCCESS;
    }

    static int showConfigString(ServerCommandSource source) {
        Text text = Text.of(MineCodeCraftHelper.getConfig().getConfigString(false));
        MineCodeCraftHelper.replyMessage(source, text);
        return Command.SINGLE_SUCCESS;
    }

    static int configOpt(ServerCommandSource source, String opt) {
        Text reportText = Text.of("");
        switch (opt) {
            case "save" -> {
                MineCodeCraftHelper.getConfig().saveConfig();
                reportText = Text.of("Config saved.");
            }
            case "reload" -> {
                MineCodeCraftHelper.getConfig().loadConfig();
                reportText = Text.of("Config reload.");
            }
        }
        MineCodeCraftHelper.replyMessage(source, reportText);
        return Command.SINGLE_SUCCESS;
    }
}
