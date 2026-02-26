package io.github.kosianodangoo.trialmonolith.transformer.method;

import io.github.kosianodangoo.trialmonolith.common.helper.EntityHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import java.util.function.Consumer;

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

    public static float getHealth(Object object1, Object object2) {
        float health = 0;
        if (object2 instanceof Float value) health = value;
        else if (object1 instanceof Float value) health = value;

        if (object1 instanceof LivingEntity livingEntity)
            return getHealth(health, livingEntity);
        else if (object2 instanceof LivingEntity livingEntity)
            return getHealth(health, livingEntity);
        return health;
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

    public static boolean isDeadOrDying(Object object1, Object object2) {
        boolean isDead = true;
        if (object2 instanceof Boolean bool) isDead = bool;
        else if (object1 instanceof Boolean bool) isDead = bool;

        if (object1 instanceof LivingEntity livingEntity)
            return isDeadOrDying(isDead, livingEntity);
        else if (object2 instanceof LivingEntity livingEntity)
            return isDeadOrDying(isDead, livingEntity);
        return isDead;
    }

    public static boolean isDeadOrDying(boolean deadOrDying, LivingEntity livingEntity) {
        return (!EntityHelper.isSoulProtected(livingEntity)) && (deadOrDying || EntityHelper.getSoulDamage(livingEntity) >= 1);
    }

    public static boolean replaceIsAlive(Entity entity) {
        return !replaceIsDeadOrDying(entity);
    }

    public static boolean isAlive(Object object1, Object object2) {
        boolean alive = false;
        if (object2 instanceof Boolean bool) alive = bool;
        else if (object1 instanceof Boolean bool) alive = bool;

        if (object1 instanceof Entity entity)
            return isAlive(alive, entity);
        else if (object2 instanceof Entity entity)
            return isAlive(alive, entity);
        return alive;
    }

    public static boolean isAlive(boolean alive, Entity entity) {
        return EntityHelper.isSoulProtected(entity) || alive && EntityHelper.getSoulDamage(entity) < 1;
    }

    public static Entity.RemovalReason getRemovalReason(Object object1, Object object2) {
        Entity.RemovalReason removalReason = null;
        if (object2 instanceof Entity.RemovalReason value) removalReason = value;
        else if (object1 instanceof Entity.RemovalReason value) removalReason = value;

        if (object1 instanceof Entity entity)
            return getRemovalReason(removalReason, entity);
        else if (object2 instanceof Entity entity)
            return getRemovalReason(removalReason, entity);
        return removalReason;
    }

    public static Entity.RemovalReason getRemovalReason(Entity.RemovalReason removalReason, Entity entity) {
        if (EntityHelper.isSoulProtected(entity) && !EntityHelper.shouldBypassProtection(entity)) {
            return null;
        }
        if (entity instanceof Player) {
            return removalReason;
        }
        if (EntityHelper.getSoulDamage(entity) >= 10) {
            return Entity.RemovalReason.KILLED;
        }
        return removalReason;
    }

    public static boolean isRemoved(Object object1, Object object2) {
        boolean isRemoved = false;
        if (object2 instanceof Boolean bool) isRemoved = bool;
        else if (object1 instanceof Boolean bool) isRemoved = bool;

        if (object1 instanceof Entity livingEntity)
            return isRemoved(isRemoved, livingEntity);
        else if (object2 instanceof Entity livingEntity)
            return isRemoved(isRemoved, livingEntity);
        return isRemoved;
    }

    public static boolean isRemoved(boolean removed, Entity entity) {
        if (EntityHelper.isSoulProtected(entity) && !EntityHelper.shouldBypassProtection(entity)) {
            return false;
        }
        if (entity instanceof Player) {
            return removed;
        }
        if (EntityHelper.getSoulDamage(entity) >= 10) {
            return true;
        }
        return removed;
    }

    public static boolean shouldReplaceIsPickable(Entity entity) {
        return EntityHelper.hasHighDimensionalBarrier(entity);
    }

    public static boolean replaceIsPickable(Entity entity) {
        return false;
    }

    public static void tickOverride(Consumer<Entity> consumer, Entity entity) {
        consumer.accept(entity);
    }
}
