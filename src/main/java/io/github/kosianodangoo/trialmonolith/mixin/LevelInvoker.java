package io.github.kosianodangoo.trialmonolith.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.entity.LevelEntityGetter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Level.class)
public interface LevelInvoker {
    @Invoker(value = "getEntities")
    LevelEntityGetter<Entity> the_trial_monolith$getEntities();
}
