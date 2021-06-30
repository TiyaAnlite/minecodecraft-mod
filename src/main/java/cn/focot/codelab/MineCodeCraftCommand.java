package cn.focot.codelab;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class MineCodeCraftCommand {
    public static void registerCommand(CommandDispatcher<ServerCommandSource> dispatcher) {
        LiteralArgumentBuilder<ServerCommandSource> literalargumentbuilder = CommandManager.literal("minecodecraft").executes(source -> {
            ServerPlayerEntity player;
            try {
                player = source.getSource().getPlayer();
            } catch (CommandSyntaxException e) {
                source.getSource().sendFeedback(Text.of("no a player!"), false);
                return Command.SINGLE_SUCCESS;
            }
            player.sendMessage(Text.of(player.getName().asString() + " is testing!!\nIP: " + player.getIp()), false);
            return Command.SINGLE_SUCCESS;
        });

        dispatcher.register(literalargumentbuilder);
    }
}
