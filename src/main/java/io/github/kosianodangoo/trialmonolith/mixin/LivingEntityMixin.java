package io.github.kosianodangoo.trialmonolith.mixin;

import io.github.kosianodangoo.trialmonolith.TrialMonolithConfig;
import io.github.kosianodangoo.trialmonolith.common.entity.invadermonolith.InvaderMonolithEntity;
import io.github.kosianodangoo.trialmonolith.common.entity.trialmonolith.TrialMonolithEntity;
import io.github.kosianodangoo.trialmonolith.common.helper.EntityHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = LivingEntity.class, priority = Integer.MAX_VALUE)
public abstract class LivingEntityMixin extends Entity {

    @Shadow public abstract boolean isDeadOrDying();

    protected LivingEntityMixin(EntityType<? extends LivingEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Inject(method = "getMaxHealth", at = @At("HEAD"), cancellable = true)
    public void getMaxHealthMixin(CallbackInfoReturnable<Float> cir) {
        if ((Object) this instanceof TrialMonolithEntity) {
            cir.setReturnValue(TrialMonolithConfig.trialMonolithHealth);
        } else if ((Object) this instanceof InvaderMonolithEntity) {
            cir.setReturnValue(Float.POSITIVE_INFINITY);
        }
    }

    @Inject(method = "tickDeath", at = @At("HEAD"), cancellable = true)
    public void tickDeathMixin(CallbackInfo ci) {
        if (EntityHelper.isSoulProtected(this) && !EntityHelper.shouldBypassProtection(this) && !this.isDeadOrDying()) {
            ci.cancel();
        }
    }
}
