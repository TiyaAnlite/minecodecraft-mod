package cn.focot.codelab.mixin;

import cn.focot.codelab.MineCodeCraftHelper;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BooleanSupplier;

@Mixin(MinecraftServer.class)
public class ServerMixin {
    //	@Inject(at = @At("HEAD"), method = "init()V")
//	private void init(CallbackInfo info) {
//		System.out.println("This line is printed by an example mod mixin!");
//	}
//
    @Inject(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/MinecraftServer;tickWorlds(Ljava/util/function/BooleanSupplier;)V",
                    shift = At.Shift.BEFORE,
                    ordinal = 0
            )
    )
    private void onTick(BooleanSupplier booleanSupplier_1, CallbackInfo ci) {
        MineCodeCraftHelper.onTick((MinecraftServer) (Object) this);
    }

    @Inject(method = "loadWorld", at = @At("HEAD"))
    private void serverLoaded(CallbackInfo c) {
        MineCodeCraftHelper.onServerLoaded((MinecraftServer) (Object) this);
    }
}
