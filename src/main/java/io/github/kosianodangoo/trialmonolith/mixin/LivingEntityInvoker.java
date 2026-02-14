package io.github.kosianodangoo.trialmonolith.mixin;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(LivingEntity.class)
public interface LivingEntityInvoker {
    @Invoker(value = "dropAllDeathLoot")
    void the_trial_monolith$dropAllDeathLoot(DamageSource damageSource);
}
