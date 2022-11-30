package cn.focot.codelab.minecodecraft.mixins;

import cn.focot.codelab.minecodecraft.handlers.CreeperHandler;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CreeperEntity.class)
public class CreeperMixin {
    @Inject(method = "explode", at = @At("HEAD"))
    private void onCreeperexplode(CallbackInfo c) {
        CreeperHandler.onCreeperExplode((CreeperEntity) (Object) this);
    }

    @ModifyArg(
            method = "explode",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/World;createExplosion(Lnet/minecraft/entity/Entity;DDDFLnet/minecraft/world/World$ExplosionSourceType;)Lnet/minecraft/world/explosion/Explosion;"
            ),
            index = 5
    )
    private World.ExplosionSourceType onCreeperCreateExplosion(World.ExplosionSourceType t) {
        return CreeperHandler.onCreeperCreateExplosion(t);
    }
}
