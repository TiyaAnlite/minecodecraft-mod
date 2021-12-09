package cn.focot.codelab.minecodecraft.utils;

import cn.focot.codelab.minecodecraft.MineCodeCraftMod;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.network.MessageType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Util;

public class MessageUtil {
    private static final String msgPrefix = "§7[MineCodeCraft]§r ";

    public static String getMsgPrefix() {
        return msgPrefix;
    }

    public static void replyMessage(ServerCommandSource source, Text text) {
        try {
            source.getPlayer().sendMessage(text, false);
        } catch (CommandSyntaxException e) {
            source.sendFeedback(text, false);
        }
    }

    public static void broadcastMessage(String text) {
        broadcastTextMessage(Text.of(text));
    }

    public static void broadcastTextMessage(Text text) {
        MineCodeCraftMod.getMinecraftServer().getPlayerManager().broadcast(text, MessageType.SYSTEM, Util.NIL_UUID);
    }

    public static Text prefixMessage(String text) {
        return Text.of(msgPrefix + text);
    }

    public static void broadcastPrefixMessage(String text) {
        broadcastTextMessage(prefixMessage(text));
    }
}
