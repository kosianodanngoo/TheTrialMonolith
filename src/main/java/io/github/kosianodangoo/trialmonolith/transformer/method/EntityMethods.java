package io.github.kosianodangoo.trialmonolith.transformer.method;

import io.github.kosianodangoo.trialmonolith.common.helper.EntityHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

@SuppressWarnings("unused")
public class EntityMethods {
    public static boolean shouldReplaceHealthMethod(Entity entity) {
        return EntityHelper.isSoulProtected(entity) || EntityHelper.getSoulDamage(entity) >= 1;
    }

    public static float replaceGetHealth(LivingEntity livingEntity) {
        if (EntityHelper.isSoulProtected(livingEntity)) {
            return Math.max(livingEntity.getMaxHealth(), 1);
        }
        if (EntityHelper.getSoulDamage(livingEntity) >= 1) {
            return 0;
        }
        return 20;
    }

    public static float getHealth(LivingEntity livingEntity) {
        return getHealth(livingEntity.getHealth(), livingEntity);
    }

    public static float getHealth(float health, LivingEntity livingEntity) {
        if (EntityHelper.isSoulProtected(livingEntity)) {
            return Math.max(livingEntity.getMaxHealth(), 1);
        }
        if (EntityHelper.getSoulDamage(livingEntity) > 0) {
            return Math.min(health, livingEntity.getMaxHealth() * (1 - EntityHelper.getSoulDamage(livingEntity)));
        }
        return health;
    }

    public static boolean replaceIsDeadOrDying(Entity entity) {
        return !EntityHelper.isSoulProtected(entity) && EntityHelper.getSoulDamage(entity) >= 1;
    }

    public static boolean isDeadOrDying(LivingEntity livingEntity) {
        return isDeadOrDying(livingEntity.isDeadOrDying(), livingEntity);
    }

    public static boolean isDeadOrDying(boolean deadOrDying, LivingEntity livingEntity) {
        return (!EntityHelper.isSoulProtected(livingEntity)) && (deadOrDying || EntityHelper.getSoulDamage(livingEntity) >= 1);
    }

    public static boolean replaceIsAlive(Entity entity) {
        return !replaceIsDeadOrDying(entity);
    }

    public static boolean isAlive(Entity entity) {
        return isAlive(entity.isAlive(), entity);
    }

    public static boolean isAlive(boolean alive, Entity entity) {
        return EntityHelper.isSoulProtected(entity) || alive && EntityHelper.getSoulDamage(entity) < 1;
    }

    public static Entity.RemovalReason getRemovalReason(Entity entity) {
        return getRemovalReason(entity.getRemovalReason(), entity);
    }

    public static Entity.RemovalReason getRemovalReason(Entity.RemovalReason removalReason, Entity entity) {
        if (removalReason != null || entity instanceof Player) {
            return removalReason;
        }
        if (EntityHelper.getSoulDamage(entity) >= 10) {
            return Entity.RemovalReason.KILLED;
        }
        return null;
    }

    public static boolean isRemoved(Entity entity) {
        return isRemoved(entity.isRemoved(), entity);
    }

    public static boolean isRemoved(boolean removed, Entity entity) {
        return removed || getRemovalReason(entity) != null;
    }
}
