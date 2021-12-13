package cn.focot.codelab.minecodecraft.utils;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.network.MessageType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Util;

public class MessageUtil extends AbstractUtil {
    private static final String msgPrefix = "§7[MineCodeCraft]§r ";

    public static String getMsgPrefix() {
        return msgPrefix;
    }

    public static void replyCommandMessage(ServerCommandSource source, Text text) {
        try {
            source.getPlayer().sendMessage(text, false);
        } catch (CommandSyntaxException e) {
            source.sendFeedback(text, false);
        }
    }


    public static void broadcastMessage(String text, boolean actionBar, boolean console) {
        broadcastTextMessage(Text.of(text), actionBar, console);
    }

    public static void broadcastTextMessage(Text text, boolean actionBar, boolean console) {
        if (console) {
            getServer().getPlayerManager().broadcast(text, actionBar ? MessageType.GAME_INFO : MessageType.SYSTEM, Util.NIL_UUID);
        } else {
            //No console message
            for (ServerPlayerEntity player : getServer().getPlayerManager().getPlayerList()) {
                player.sendMessage(text, actionBar ? MessageType.GAME_INFO : MessageType.SYSTEM, Util.NIL_UUID);
            }
        }
    }

    public static Text prefixMessage(String text) {
        return Text.of(msgPrefix + text);
    }

    public static void broadcastPrefixMessage(String text, boolean actionBar, boolean console) {
        broadcastTextMessage(prefixMessage(text), actionBar, console);
    }
}
