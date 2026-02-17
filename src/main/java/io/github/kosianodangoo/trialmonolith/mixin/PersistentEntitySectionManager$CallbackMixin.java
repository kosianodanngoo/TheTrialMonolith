package io.github.kosianodangoo.trialmonolith.mixin;

import io.github.kosianodangoo.trialmonolith.common.helper.EntityHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.entity.EntityAccess;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net/minecraft/world/level/entity/PersistentEntitySectionManager$Callback")
public class PersistentEntitySectionManager$CallbackMixin<T extends EntityAccess> {
    @Shadow @Final private T entity;

    @Inject(method = "onRemove", at = @At("HEAD"), cancellable = true)
    public void onRemoveMixin(Entity.RemovalReason removalReason, CallbackInfo ci) {
        if (this.entity instanceof Entity target && EntityHelper.isSoulProtected(target) && !EntityHelper.shouldBypassProtection(target)) {
            ci.cancel();
        }
    }
}
