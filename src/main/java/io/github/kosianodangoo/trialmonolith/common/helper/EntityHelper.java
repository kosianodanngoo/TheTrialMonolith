package io.github.kosianodangoo.trialmonolith.common.helper;

import com.google.common.base.Predicates;
import io.github.kosianodangoo.trialmonolith.TheTrialMonolith;
import io.github.kosianodangoo.trialmonolith.api.mixin.ISoulDamage;
import io.github.kosianodangoo.trialmonolith.api.mixin.ISoulProtection;
import io.github.kosianodangoo.trialmonolith.common.init.TrialMonolithDamageTypes;
import io.github.kosianodangoo.trialmonolith.mixin.LivingEntityInvoker;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class EntityHelper {
    public static final String SOUL_PROTECTION_TAG = TheTrialMonolith.MOD_ID + ":SoulProtection";
    public static final String SOUL_DAMAGE_TAG = TheTrialMonolith.MOD_ID + ":SoulDamage";

    public static void rayTraceEntities(Entity sourceEntity, double reach, double width, Predicate<Entity> predicate, Consumer<Entity> consumer) {
        Vec3 view = sourceEntity.getViewVector(0);
        Vec3 vector = view.scale(reach);

        Vec3 start = sourceEntity.getEyePosition().subtract(view.scale(width));
        Vec3 end = start.add(vector).add(view.scale(width));
        AABB area = sourceEntity.getBoundingBox()
                .expandTowards(view.scale(-1))
                .expandTowards(vector)
                .inflate(width);


        for (Entity entity : sourceEntity.level().getEntities(sourceEntity, area, predicate)) {
            AABB aabb = entity.getBoundingBox().inflate(entity.getPickRadius()).inflate(width);
            Optional<Vec3> optional = aabb.clip(start, end);
            optional.ifPresent(hit -> consumer.accept(entity));
        }
    }


    public static float getSoulDamage(Entity entity) {
        if (entity instanceof ISoulDamage soulDamage) {
            return soulDamage.the_trial_monolith$getSoulDamage();
        }
        return 0;
    }

    public static void setSoulDamage(Entity entity, float damage) {
        if (entity instanceof ISoulDamage soulDamage) {
            if (isSoulProtected(entity)) {
                return;
            }
            float currentDamage = soulDamage.the_trial_monolith$getSoulDamage();
            soulDamage.the_trial_monolith$setSoulDamage(damage);
            if (currentDamage < 1 && soulDamage.the_trial_monolith$getSoulDamage() >= 1) {
                onSoulDeath(entity);
            }
            if (currentDamage < 10 && soulDamage.the_trial_monolith$getSoulDamage() >= 10) {
                onSoulRemove(entity);
            }
        }
    }

    public static void onSoulDeath(Entity entity) {
        DamageSource damageSource = TrialMonolithDamageTypes.soulDamage(entity.level());
        if (entity instanceof LivingEntity livingEntity) {
            livingEntity.setHealth(Float.NEGATIVE_INFINITY);
            livingEntity.getCombatTracker().recordDamage(damageSource, Float.MAX_VALUE);
            livingEntity.die(damageSource);
            if (!livingEntity.dead && entity instanceof LivingEntityInvoker livingEntityInvoker) {
                livingEntityInvoker.the_trial_monolith$dropAllDeathLoot(damageSource);
            }
            livingEntity.getBrain().clearMemories();
            if (entity instanceof Mob mob) {
                mob.goalSelector.removeAllGoals(Predicates.alwaysTrue());
                mob.targetSelector.removeAllGoals(Predicates.alwaysTrue());
            }
        }
        entity.kill();
    }

    public static void onSoulRemove(Entity entity) {
        if (entity instanceof Player) {
            return;
        }
        entity.remove(Entity.RemovalReason.KILLED);
        entity.setRemoved(Entity.RemovalReason.KILLED);

        entity.stopRiding();

        entity.getPassengers().forEach(Entity::stopRiding);
        entity.levelCallback.onRemove(Entity.RemovalReason.KILLED);

        entity.invalidateCaps();
    }

    public static void addSoulDamage(Entity entity, float damage) {
        if (entity instanceof ISoulDamage soulDamage) {
            setSoulDamage(entity, damage + soulDamage.the_trial_monolith$getSoulDamage());
        }
    }

    public static boolean isSoulProtected(Entity entity) {
        if (entity instanceof ISoulProtection soulProtection) {
            return soulProtection.the_trial_monolith$isSoulProtected();
        }
        return false;
    }

    public static void setSoulProtected(Entity entity, boolean protect) {
        if (entity instanceof ISoulProtection soulProtection) {
            soulProtection.the_trial_monolith$setSoulProtected(protect);
        }
    }

    public static void toggleSoulProtected(Entity entity) {
        if (entity instanceof ISoulProtection soulProtection) {
            setSoulProtected(entity, !soulProtection.the_trial_monolith$isSoulProtected());
        }
    }

    public static boolean shouldBypassProtection(Entity entity) {
        if (entity instanceof ISoulProtection soulProtection) {
            return soulProtection.the_trial_monolith$shouldBypass();
        }
        return false;
    }

    public static void setBypassProtection(Entity entity, boolean shouldBypass) {
        if (entity instanceof ISoulProtection soulProtection) {
            if (entity.isRemoved() && !shouldBypass) {
                return;
            }
            soulProtection.the_trial_monolith$setShouldBypass(shouldBypass);
        }
    }
}
