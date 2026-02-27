package io.github.kosianodangoo.trialmonolith.common.entity.invadermonolith;

import io.github.kosianodangoo.trialmonolith.TrialMonolithConfig;
import io.github.kosianodangoo.trialmonolith.api.mixin.IHighDimensionalBarrier;
import io.github.kosianodangoo.trialmonolith.api.mixin.ISoulDamage;
import io.github.kosianodangoo.trialmonolith.api.mixin.ISoulProtection;
import io.github.kosianodangoo.trialmonolith.common.entity.AbstractDelayedTraceableEntity;
import io.github.kosianodangoo.trialmonolith.common.entity.invadermonolith.ai.*;
import io.github.kosianodangoo.trialmonolith.common.helper.EntityHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.function.Predicate;

import static io.github.kosianodangoo.trialmonolith.TrialMonolithConfig.invaderMonolithAttackRange;

public class InvaderMonolithEntity extends Monster implements ISoulDamage, ISoulProtection, IHighDimensionalBarrier {
    private boolean initialized = false;

    private final ServerBossEvent bossEvent;

    public Predicate<Entity> DEFAULT_PREDICATE = (entity -> entity != this &&
            !(entity instanceof AbstractDelayedTraceableEntity traceable && this == traceable.getOwner()) &&
            !(entity instanceof Projectile projectile && this == projectile.getOwner()) &&
            !(entity instanceof ItemEntity) &&
            !(entity instanceof ExperienceOrb) &&
            entity.tickCount >= 100
    );

    public InvaderMonolithEntity(EntityType<? extends Monster> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        if (!TrialMonolithConfig.invaderMonolithAttacksCreative) {
            DEFAULT_PREDICATE = DEFAULT_PREDICATE.and(entity ->
                    !(entity instanceof Player player && (player.isCreative() || player.isSpectator()))
            );
        }

        this.bossEvent = new ServerBossEvent(this.getDisplayName(), BossEvent.BossBarColor.RED, BossEvent.BossBarOverlay.PROGRESS);
    }

    @Override
    public void startSeenByPlayer(@NotNull ServerPlayer player) {
        super.startSeenByPlayer(player);
        this.bossEvent.addPlayer(player);
    }

    @Override
    public void stopSeenByPlayer(@NotNull ServerPlayer player) {
        super.stopSeenByPlayer(player);
        this.bossEvent.removePlayer(player);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(4, new OPShootHugeBeamGoal(this));
        this.goalSelector.addGoal(3, new OPSummonDamageCubeGoal(this));
        this.goalSelector.addGoal(2, new OPShootSmallBeamAroundTargetGoal(this));
        this.goalSelector.addGoal(1, new OPShootRandomSmallBeamGoal(this));
        this.goalSelector.addGoal(0, new OPShootSmallBeamGoal(this));
    }

    @Override
    public boolean isNoAi() {
        return !TrialMonolithConfig.invaderMonolithBypassNoAI && super.isNoAi();
    }

    @Override
    public void setNoAi(boolean p_21558_) {
        super.setNoAi(!TrialMonolithConfig.invaderMonolithBypassNoAI && p_21558_);
    }

    public static AttributeSupplier createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 1000)
                .add(Attributes.FOLLOW_RANGE, invaderMonolithAttackRange)
                .build();
    }

    @Override
    public float getHealth() {
        return Float.POSITIVE_INFINITY;
    }

    @Override
    public boolean isAlive() {
        return true;
    }

    @Override
    public boolean isDeadOrDying() {
        return false;
    }

    @Override
    public boolean hurt(@NotNull DamageSource p_21016_, float p_21017_) {
        return false;
    }

    @Override
    public void tick() {
        initialized = true;
        super.tick();
        getTargets().forEach(entity -> EntityHelper.setSoulProtected(entity, false));
    }

    @Override
    public void setHealth(float pHealth) {
    }


    @Override
    public void load(@NotNull CompoundTag pCompound) {
        super.load(pCompound);
        initialized = true;
    }

    @Override
    public float getYRot() {
        return 0;
    }

    @Override
    protected void dropAllDeathLoot(@NotNull DamageSource pDamageSource) {
        if (super.isDeadOrDying()) {
            super.dropAllDeathLoot(pDamageSource);
        }
    }

    @Override
    protected void dropFromLootTable(@NotNull DamageSource pDamageSource, boolean pIsByPlayer) {
        if (this.shouldDropLoot()) {
            super.dropFromLootTable(pDamageSource, pIsByPlayer);
        }
    }

    @Override
    protected boolean shouldDropLoot() {
        return super.isDeadOrDying() && super.shouldDropLoot();
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
        return SoundEvent.createFixedRangeEvent(SoundEvents.STONE_STEP.getLocation(), 16);
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
    public void setPos(double x, double y, double z) {
        if (!initialized) {
            super.setPos(x, y, z);
        }
    }

    @Override
    public void kill() {
        super.kill();
        if (TrialMonolithConfig.INVADER_MONOLITH_SHOULD_DIE_FROM_KILL.get()) {
            EntityHelper.setBypassProtection(this, true);
            this.setRemoved(RemovalReason.DISCARDED);
        }
    }

    @Override
    public void die(@NotNull DamageSource p_21014_) {
        if (this.isDeadOrDying()) {
            super.die(p_21014_);
        }
    }

    @Override
    public boolean the_trial_monolith$isSoulProtected() {
        return true;
    }

    @Override
    public void the_trial_monolith$setSoulProtected(boolean soulProtected) {
    }

    @Override
    public float the_trial_monolith$getSoulDamage() {
        return 0;
    }

    @Override
    public void the_trial_monolith$setSoulDamage(float soulDamage) {
    }

    public Collection<Entity> getTargets() {
        return EntityHelper.getEntities(level(), AABB.ofSize(this.getPosition(0), invaderMonolithAttackRange * 2, invaderMonolithAttackRange * 2, invaderMonolithAttackRange * 2), DEFAULT_PREDICATE);
    }

    @Override
    public void remove(@NotNull RemovalReason p_276115_) {
    }

    @Override
    public void onRemovedFromWorld() {
    }

    @Override
    public boolean the_trial_monolith$hasHighDimensionalBarrier() {
        return TrialMonolithConfig.invaderMonolithHasDimensionalBarrier;
    }

    @Override
    public void the_trial_monolith$setHighDimensionalBarrier(boolean highDimensionalBarrier) {

    }
}
