package io.github.kosianodangoo.trialmonolith.common.entity;

import io.github.kosianodangoo.trialmonolith.TrialMonolithConfig;
import io.github.kosianodangoo.trialmonolith.common.helper.EntityHelper;
import io.github.kosianodangoo.trialmonolith.common.init.TrialMonolithDamageTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class HugeBeamEntity extends AbstractDelayedTraceableEntity {
    public HugeBeamEntity(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    public @NotNull Vec3 getDeltaMovement() {
        return Vec3.ZERO;
    }

    @Override
    public AABB getBoundingBoxForCulling() {
        return super.getBoundingBoxForCulling().inflate(128);
    }

    @Override
    public void tick() {
        if (getPastTicks() == 0) {
            level().playSound(null, this.blockPosition(), SoundEvents.WARDEN_SONIC_CHARGE, SoundSource.HOSTILE, 8, 0);
        }
        super.tick();
    }

    @Override
    public void activate() {
        Level level = this.level();

        if (level.isClientSide()) {
            return;
        }

        if (this.getPastTicks() == this.getDelay()) {
            level.playSound(null, this.blockPosition(), SoundEvents.WITHER_SPAWN, SoundSource.HOSTILE, 8, 0);
        }

        EntityHelper.rayTraceEntities(this, 128, 8, DEFAULT_PREDICATE, (entity) -> {
            EntityHelper.addSoulDamage(entity, TrialMonolithConfig.hugeBeamSoulDamage);

            entity.hurt(TrialMonolithDamageTypes.laserAttack(level, this.getOwner()), Float.MAX_VALUE);

            entity.setDeltaMovement(0, 0, 0);
        });
    }

    @Override
    public int getDelay() {
        return 100;
    }

    @Override
    public int getDuration() {
        return 100;
    }

    @Override
    public boolean shouldContinue() {
        return true;
    }
}
