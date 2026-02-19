package io.github.kosianodangoo.trialmonolith.common.entity.trialmonolith;

import io.github.kosianodangoo.trialmonolith.TrialMonolithConfig;
import io.github.kosianodangoo.trialmonolith.common.entity.trialmonolith.ai.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class TrialMonolithEntity extends Monster {
    private static final EntityDataAccessor<Float> DATA_MONOLITH_HEALTH_ID = SynchedEntityData.defineId(TrialMonolithEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> DATA_MONOLITH_ACTIVE_ID = SynchedEntityData.defineId(TrialMonolithEntity.class, EntityDataSerializers.INT);

    private static final String MONOLITH_ACTIVE_TIME_TAG = "MonolithActiveTime";

    private float lastHealth = 0;
    private boolean initialized = false;
    private boolean disableDamageCap = false;

    public TrialMonolithEntity(EntityType<? extends Monster> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        AttributeInstance attributeInstance = attributes.getInstance(Attributes.FOLLOW_RANGE);
        if (attributeInstance != null) {
            attributeInstance.setBaseValue(TrialMonolithConfig.TRIAL_MONOLITH_ATTACK_RANGE.get());
        }
        lastHealth = this.getHealth();
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();

        this.entityData.define(DATA_MONOLITH_HEALTH_ID, 0f);
        this.entityData.define(DATA_MONOLITH_ACTIVE_ID, 0);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(4, new ShootHugeBeamGoal(this));
        this.goalSelector.addGoal(3, new SummonDamageCubeGoal(this));
        this.goalSelector.addGoal(2, new ShootSmallBeamAroundTargetGoal(this));
        this.goalSelector.addGoal(1, new ShootRandomSmallBeamGoal(this));
        this.goalSelector.addGoal(0, new ShootSmallBeamGoal(this));

        this.targetSelector.addGoal(1, (new HurtByTargetGoal(this, TrialMonolithEntity.class)).setAlertOthers(TrialMonolithEntity.class));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, false));
    }

    public static AttributeSupplier createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 1000)
                .add(Attributes.FOLLOW_RANGE, 128)
                .build();
    }

    @Override
    public float getHealth() {
        return this.entityData.get(DATA_MONOLITH_HEALTH_ID);
    }

    @Override
    public boolean isAlive() {
        return this.getHealth() > 0;
    }

    @Override
    public boolean isDeadOrDying() {
        return this.getHealth() <= 0;
    }

    @Override
    public void tick() {
        initialized = true;
        this.lastHealth = this.getHealth();
        if (this.getTarget() != null) {
            this.incrementMonolithActiveTime();
        }
        super.tick();
    }

    @Override
    public void setHealth(float pHealth) {
        if (Float.isNaN(pHealth)) {
            pHealth = 0;
        }
        pHealth = disableDamageCap ? pHealth : Math.max(lastHealth - TrialMonolithConfig.trialMonolithDamageCap, pHealth);

        this.entityData.set(DATA_MONOLITH_HEALTH_ID, Mth.clamp(pHealth, 0, getMaxHealth()));
    }

    public void setMonolithActiveTime(int activeTime) {
        this.entityData.set(DATA_MONOLITH_ACTIVE_ID, activeTime);
    }

    public void incrementMonolithActiveTime() {
        setMonolithActiveTime(getMonolithActiveTime() + 1);
    }

    public int getMonolithActiveTime() {
        return this.entityData.get(DATA_MONOLITH_ACTIVE_ID);
    }

    public boolean isMonolithActive() {
        return getMonolithActiveTime() > 0;
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putInt(MONOLITH_ACTIVE_TIME_TAG, getMonolithActiveTime());
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        setMonolithActiveTime(pCompound.getInt(MONOLITH_ACTIVE_TIME_TAG));
    }

    @Override
    public void load(@NotNull CompoundTag pCompound) {
        disableDamageCap = !initialized;
        super.load(pCompound);
        initialized = true;
        disableDamageCap = false;
    }

    @Override
    public float getYRot() {
        return 0;
    }

    @Override
    public float getXRot() {
        return 0;
    }

    @Override
    public boolean requiresCustomPersistence() {
        return true;
    }

    @Override
    protected @NotNull SoundEvent getHurtSound(@NotNull DamageSource pDamageSource) {
        return SoundEvent.createFixedRangeEvent(SoundEvents.ANVIL_PLACE.getLocation(), 16);
    }

    @Override
    protected @NotNull SoundEvent getDeathSound() {
        return SoundEvents.WITHER_DEATH;
    }

    @Override
    public @NotNull Vec3 getDeltaMovement() {
        return Vec3.ZERO;
    }

    @Override
    public void kill() {
        if (TrialMonolithConfig.TRIAL_MONOLITH_SHOULD_DIE_FROM_KILL.get()) {
            this.lastHealth = 0;
            this.disableDamageCap = true;
        }
        super.kill();
    }

    @Override
    public void die(@NotNull DamageSource p_21014_) {
        if (this.isDeadOrDying()) {
            super.die(p_21014_);
        }
    }
}
