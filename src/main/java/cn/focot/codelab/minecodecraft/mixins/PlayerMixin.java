package cn.focot.codelab.minecodecraft.mixins;

import cn.focot.codelab.minecodecraft.handlers.PlayerHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public class PlayerMixin {
    @Inject(
            method = "readCustomData",
            at = @At(
                    value = "INVOKE",
                    shift = At.Shift.AFTER,
                    ordinal = 0
            )
    )
    private void onPlayerReadData(ReadView view, CallbackInfo ci) {
        PlayerHandler.onPlayerReadNbt((ServerPlayerEntity)(Object)this, view);
    }

    @Inject(
            method = "writeCustomData",
            at = @At(
                    value = "INVOKE",
                    shift = At.Shift.AFTER,
                    ordinal = 0
            )
    )
    private void onPlayerWriteData(WriteView view, CallbackInfo ci) {
        PlayerHandler.onPlayerWriteNbt((ServerPlayerEntity)(Object)this, view);
    }
}
