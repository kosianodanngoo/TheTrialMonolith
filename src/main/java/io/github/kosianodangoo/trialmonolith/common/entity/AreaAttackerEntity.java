package io.github.kosianodangoo.trialmonolith.common.entity;

import io.github.kosianodangoo.trialmonolith.common.helper.EntityHelper;
import io.github.kosianodangoo.trialmonolith.common.init.TrialMonolithDamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class AreaAttackerEntity extends AbstractDelayedTraceableEntity {
    private Entity internalOwner;

    public AreaAttackerEntity(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    public void tick() {
        super.tick();

        internalOwner = getOwner();
        if (internalOwner != null && internalOwner.isAlive() && !internalOwner.isRemoved()) {
            this.moveTo(internalOwner.position);
        } else if (this.ownerController != null) {
            this.moveTo(ownerController.getPosition());
            internalOwner = ownerController.getProxyEntity();
        } else if (this.getPastTicks() + 20 <= this.getLifeTime()) {
            this.setPastTicks(this.getLifeTime() - 20);
        }
    }

    @Override
    public void activate() {
        Level level = this.level();

        if (level.isClientSide()) {
            return;
        }
        EntityHelper.getEntities(level(), AABB.ofSize(this.getPosition(0), 96, 96, 96), DEFAULT_PREDICATE.and(entity -> entity.getBoundingBox().distanceToSqr(getPosition(0)) <= 2304)).forEach(entity -> {
            if (isHighDimensional() && EntityHelper.isImmuneToSoulDamage(entity)) {
                EntityHelper.addSoulDamageForce(entity, 0.02f);
            } else {
                EntityHelper.addSoulDamage(entity, 0.05f);
            }
            entity.hurt(TrialMonolithDamageTypes.dimensionalAttack(level, internalOwner), 100);
            entity.setDeltaMovement(Vec3.ZERO);
        });
    }

    @Override
    public int getDelay() {
        return 30;
    }

    @Override
    public boolean shouldContinue() {
        return true;
    }

    @Override
    public int getDuration() {
        return Integer.MAX_VALUE - 100;
    }

    @Override
    public @NotNull AABB getBoundingBoxForCulling() {
        return super.getBoundingBoxForCulling().inflate(48);
    }
}
