package cn.focot.codelab.minecodecraft.mixins;

import cn.focot.codelab.minecodecraft.MineCodeCraftMod;
import cn.focot.codelab.minecodecraft.handlers.ServerHandler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerTickManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftServer.class)
public class ServerMixin {
    private static MinecraftServerAccessor accessor;

//    @Inject(
//            method = "tick",
//            at = @At(
//                    value = "INVOKE",
//                    target = "Lnet/minecraft/server/MinecraftServer;tickWorlds(Ljava/util/function/BooleanSupplier;)V",
//                    shift = At.Shift.BEFORE,
//                    ordinal = 0
//            )
//    )
//    private void onWorldTick(BooleanSupplier booleanSupplier_1, CallbackInfo ci) {
//        ServerHandler.onWorldTick((MinecraftServer) (Object) this);
//    }

    @Inject(method = "loadWorld", at = @At("HEAD"))
    private void serverLoaded(CallbackInfo c) {
        // ServerHandler.onServerLoaded((MinecraftServer) (Object) this);
        // Get accessor
        accessor = (MinecraftServerAccessor) MineCodeCraftMod.getMinecraftServer();
    }


    @Inject(method = "endTickMetrics", at = @At("TAIL"))
    private void serverTickEnd(CallbackInfo c) {
        ServerHandler.onServerTickEnd((MinecraftServer) (Object) this, accessor.getTickManager().getNanosPerTick(), accessor.getLastOverloadWarningNanos(), MinecraftServerAccessor.getOverloadThresholdNanos());
    }
}

@Mixin(MinecraftServer.class)
interface MinecraftServerAccessor {
    @Accessor
    long getLastOverloadWarningNanos();

    @Accessor
    ServerTickManager getTickManager();

    @Accessor("OVERLOAD_THRESHOLD_NANOS")
    public static long getOverloadThresholdNanos() {
        throw new AssertionError();
    }
}
