package cn.focot.codelab;

import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;

public class MineCodeCraftMod implements ModInitializer {
    @Override
    public void onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.

        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> MineCodeCraftCommand.registerCommand(dispatcher));
        ServerPlayConnectionEvents.JOIN.register(MineCodeCraftHelper::onPlayerEvent);
    }


}