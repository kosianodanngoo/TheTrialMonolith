package io.github.kosianodangoo.trialmonolith.common.entity.tesseractbeast;

import io.github.kosianodangoo.trialmonolith.api.IHighDimensionalMob;
import io.github.kosianodangoo.trialmonolith.api.mixin.IHighDimensionalBarrier;
import io.github.kosianodangoo.trialmonolith.api.mixin.ISoulDamage;
import io.github.kosianodangoo.trialmonolith.api.mixin.ISoulProtection;
import io.github.kosianodangoo.trialmonolith.common.helper.EntityHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class TesseractBeastProxyEntity extends Monster implements ISoulProtection, ISoulDamage, IHighDimensionalBarrier, IHighDimensionalMob {
    public TesseractBeastController controller;
    public boolean initialized = true;

    public TesseractBeastProxyEntity(EntityType<? extends Monster> pEntityType, Level pLevel, TesseractBeastController controller) {
        super(pEntityType, pLevel);
        if (controller == null) {
            initialized = false;
        }
        this.controller = controller;
        this.noPhysics = true;
        this.setNoAi(true);
        this.setNoGravity(true);
    }

    public static TesseractBeastProxyEntity of(EntityType<? extends Monster> pEntityType, Level pLevel) {
        return new TesseractBeastProxyEntity(pEntityType, pLevel, null);
    }

    @Override
    public boolean isNoGravity() {
        return true;
    }

    @Override
    public boolean isNoAi() {
        return true;
    }

    @Override
    public void aiStep() {
        this.deltaMovement = Vec3.ZERO;
        super.aiStep();
    }

    @Override
    public float the_trial_monolith$getSoulDamage() {
        if (level.isClientSide || controller == null) {
            if (EntityHelper.isInitialized(this)) {
                return this.entityData.get(EntityHelper.DATA_SOUL_DAMAGE_ID);
            }
            return 0;
        }
        return controller.soulDamage;
    }

    @Override
    public void the_trial_monolith$setSoulDamage(float soulDamage) {
        float oldDamage = the_trial_monolith$getSoulDamage();
        float newDamage = oldDamage + (soulDamage - oldDamage) / 10;
        if (initialized) {
            soulDamage = newDamage;
        } else {
            initialized = true;
        }
        if (controller != null) {
            controller.soulDamage = soulDamage;
        }
        if (EntityHelper.isInitialized(this)) {
            this.entityData.set(EntityHelper.DATA_SOUL_DAMAGE_ID, soulDamage);
        }
    }

    public static AttributeSupplier createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 1000)
                .add(Attributes.FOLLOW_RANGE, 128)
                .build();
    }

    @Override
    public boolean the_trial_monolith$isSoulProtected() {
        return true;
    }

    @Override
    public void the_trial_monolith$setSoulProtected(boolean soulProtected) {
    }

    @Override
    public boolean the_trial_monolith$hasHighDimensionalBarrier() {
        return true;
    }

    @Override
    public void the_trial_monolith$setHighDimensionalBarrier(boolean highDimensionalBarrier) {
    }

    @Override
    public void stopSeenByPlayer(@NotNull ServerPlayer player) {
        super.stopSeenByPlayer(player);
        controller.bossEvent.removePlayer(player);
    }

    @Override
    public void startSeenByPlayer(@NotNull ServerPlayer player) {
        super.startSeenByPlayer(player);
        controller.bossEvent.addPlayer(player);
    }
}
