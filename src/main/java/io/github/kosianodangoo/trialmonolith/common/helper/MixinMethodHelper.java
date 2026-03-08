package io.github.kosianodangoo.trialmonolith.common.helper;

import net.minecraft.world.entity.LivingEntity;

public class MixinMethodHelper {
    public static boolean isDeadOrDying(LivingEntity livingEntity) {
        return livingEntity.isDeadOrDying();
    }
}
