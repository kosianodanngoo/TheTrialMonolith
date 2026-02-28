package io.github.kosianodangoo.trialmonolith.transformer.method;

import com.mojang.logging.LogUtils;
import io.github.kosianodangoo.trialmonolith.api.mixin.ITickTracker;
import io.github.kosianodangoo.trialmonolith.common.entity.trialmonolith.TrialMonolithEntity;
import io.github.kosianodangoo.trialmonolith.common.helper.EntityHelper;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.ForgeConfig;
import net.minecraftforge.server.timings.TimeTracker;

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

    public static boolean isDeadOrDying(boolean deadOrDying, LivingEntity livingEntity) {
        return (!EntityHelper.isSoulProtected(livingEntity)) && (deadOrDying || EntityHelper.getSoulDamage(livingEntity) >= 1);
    }

    public static boolean replaceIsAlive(Entity entity) {
        return !replaceIsDeadOrDying(entity);
    }

    public static boolean isAlive(boolean alive, Entity entity) {
        return EntityHelper.isSoulProtected(entity) || alive && EntityHelper.getSoulDamage(entity) < 1;
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

    public static boolean shouldOverrideTick(Entity entity) {
        return EntityHelper.isSoulProtected(entity) || entity instanceof TrialMonolithEntity;
    }

    public static void updateLastTicks(ServerLevel serverLevel) {
        serverLevel.entityTickList.forEach(entity -> {
            if (entity instanceof ITickTracker tickTracker) {
                tickTracker.the_trial_monolith$updateLastTickCount();
            }
        });
    }

    public static boolean shouldForceTick(Entity entity) {
        return entity instanceof ITickTracker tickTracker && tickTracker.the_trial_monolith$getLastTickCount() == entity.tickCount;
    }

    public static void tickOverride(Consumer<Entity> consumer, Entity entity) {
        int lastTickCount = 0;
        if (entity instanceof ITickTracker tickTracker) {
            lastTickCount = tickTracker.the_trial_monolith$getLastTickCount();
        }
        consumer.accept(entity);
        if (!(entity instanceof Player) && !entity.isPassenger() && entity instanceof ITickTracker tickTracker && lastTickCount == tickTracker.the_trial_monolith$getLastTickCount() && shouldForceTick(entity)) {
            if (entity.level() instanceof ServerLevel serverLevel) {
                newGuardEntityTick(serverLevel::tickNonPassenger, entity);
            } else if (entity.level() instanceof ClientLevel clientLevel) {
                newGuardEntityTick(clientLevel::tickNonPassenger, entity);
            }
        }
    }

    public static void newGuardEntityTick(Consumer<Entity> consumer, Entity entity) {
        try {
            TimeTracker.ENTITY_UPDATE.trackStart(entity);
            consumer.accept(entity);
        } catch (Throwable throwable) {
            CrashReport crashreport = CrashReport.forThrowable(throwable, "Ticking entity");
            CrashReportCategory crashreportcategory = crashreport.addCategory("Entity being ticked");
            entity.fillCrashReportCategory(crashreportcategory);
            if (!(Boolean) ForgeConfig.SERVER.removeErroringEntities.get()) {
                throw new ReportedException(crashreport);
            }

            LogUtils.getLogger().error("{}", crashreport.getFriendlyReport());
            EntityHelper.setBypassProtection(entity, true);
            entity.discard();
        } finally {
            TimeTracker.ENTITY_UPDATE.trackEnd(entity);
        }
    }
}
