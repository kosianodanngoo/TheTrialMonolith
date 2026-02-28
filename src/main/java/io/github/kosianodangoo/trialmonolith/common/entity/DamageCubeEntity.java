package io.github.kosianodangoo.trialmonolith.common.entity;

import io.github.kosianodangoo.trialmonolith.TrialMonolithConfig;
import io.github.kosianodangoo.trialmonolith.common.helper.EntityHelper;
import io.github.kosianodangoo.trialmonolith.common.init.TrialMonolithDamageTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class DamageCubeEntity extends AbstractDelayedTraceableEntity {
    public DamageCubeEntity(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    public @NotNull Vec3 getDeltaMovement() {
        return Vec3.ZERO;
    }

    @Override
    public @NotNull AABB getBoundingBoxForCulling() {
        return super.getBoundingBoxForCulling().inflate(4);
    }

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    public void activate() {
        Level level = this.level();

        if (level.isClientSide()) {
            return;
        }

        level.playSound(null, this.blockPosition(), SoundEvents.BEACON_DEACTIVATE, SoundSource.HOSTILE, 1, 1);

        EntityHelper.getEntities(level(), AABB.ofSize(this.getPosition(0), 4, 4, 4), DEFAULT_PREDICATE).forEach(entity -> {
            float damage = 3f;

            EntityHelper.addSoulDamage(entity, TrialMonolithConfig.damageCubeSoulDamage);

            if (entity instanceof LivingEntity livingEntity) {
                damage = Math.max(damage, livingEntity.getMaxHealth() * 0.05f);
            }

            entity.hurt(TrialMonolithDamageTypes.cubeAttack(level, this.getOwner()), damage);

            entity.setDeltaMovement(0, 0, 0);
        });
    }

    @Override
    public int getDelay() {
        return 7;
    }

    @Override
    public int getDuration() {
        return 5;
    }
}
