package io.github.kosianodangoo.trialmonolith.mixin;

import io.github.kosianodangoo.trialmonolith.common.helper.EntityHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ServerLevel.class, priority = Integer.MAX_VALUE)
public abstract class ServerLevelMixin {
    @Inject(method = "removePlayerImmediately", at = @At("HEAD"))
    public void removePlayerImmediatelyBeforeMixin(ServerPlayer pPlayer, Entity.RemovalReason pRemovalReason, CallbackInfo ci) {
        EntityHelper.setBypassProtection(pPlayer, true);
    }

    @Inject(method = "removePlayerImmediately", at = @At("RETURN"))
    public void removePlayerImmediatelyAfterMixin(ServerPlayer pPlayer, Entity.RemovalReason pRemovalReason, CallbackInfo ci) {
        EntityHelper.setBypassProtection(pPlayer, false);
    }
}
