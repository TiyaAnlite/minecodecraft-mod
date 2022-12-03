package cn.focot.codelab.minecodecraft.mixins;

import cn.focot.codelab.minecodecraft.handlers.PlayerHandler;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public class PlayerMixin {
    @Inject(
            method = "readCustomDataFromNbt",
            at = @At(
                    value = "INVOKE",
                    shift = At.Shift.AFTER,
                    ordinal = 0
            )
    )
    private void onPlayerReadNbt(NbtCompound nbt, CallbackInfo ci) {
        PlayerHandler.onPlayerReadNbt((ServerPlayerEntity)(Object)this, nbt);
    }

    @Inject(
            method = "writeCustomDataToNbt",
            at = @At(
                    value = "INVOKE",
                    shift = At.Shift.AFTER,
                    ordinal = 0
            )
    )
    private void onPlayerWriteNbt(NbtCompound nbt, CallbackInfo ci) {
        PlayerHandler.onPlayerWriteNbt((ServerPlayerEntity)(Object)this, nbt);
    }
}
