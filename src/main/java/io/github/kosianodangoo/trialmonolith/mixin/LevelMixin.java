package io.github.kosianodangoo.trialmonolith.mixin;

import io.github.kosianodangoo.trialmonolith.api.mixin.ITickTracker;
import io.github.kosianodangoo.trialmonolith.transformer.method.EntityMethods;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

@Mixin(Level.class)
public class LevelMixin {
    @Inject(method = "guardEntityTick", at = @At("HEAD"), cancellable = true)
    public <T extends Entity> void guardEntityTickMixin(Consumer<T> consumer, T entity, CallbackInfo ci) {
        if (!(entity instanceof Player) && EntityMethods.shouldOverrideTick(entity) && entity instanceof ITickTracker tickTracker) {
            if (tickTracker.the_trial_monolith$getLastTickCount() != entity.tickCount) {
                ci.cancel();
            }
        }
    }
}
