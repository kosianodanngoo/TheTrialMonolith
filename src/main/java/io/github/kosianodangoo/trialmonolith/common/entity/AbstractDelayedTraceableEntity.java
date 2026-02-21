package io.github.kosianodangoo.trialmonolith.common.entity;

import io.github.kosianodangoo.trialmonolith.common.helper.EntityHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TraceableEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.UUID;
import java.util.function.Predicate;

public abstract class AbstractDelayedTraceableEntity extends Entity implements TraceableEntity {
    @Nullable
    private LivingEntity owner;
    @Nullable
    private UUID ownerUUID;

    public final Predicate<Entity> DEFAULT_PREDICATE = (entity -> entity != this.getOwner() &&
            !(entity instanceof AbstractDelayedTraceableEntity traceable && this.getOwner() == traceable.getOwner()) &&
            !(entity instanceof Projectile projectile && this.getOwner() == projectile.getOwner()) &&
            !(entity instanceof ItemEntity)
    );

    private static final EntityDataAccessor<Integer> DATA_PAST_TICKS_ID = SynchedEntityData.defineId(AbstractDelayedTraceableEntity.class, EntityDataSerializers.INT);

    public AbstractDelayedTraceableEntity(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(DATA_PAST_TICKS_ID, 0);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag pCompound) {
        if (pCompound.hasUUID("Owner")) {
            this.ownerUUID = pCompound.getUUID("Owner");
            owner = null;
        }
        setPastTicks(pCompound.getInt("PastTicks"));
    }

    @Override
    protected void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
        if (this.ownerUUID != null) {
            pCompound.putUUID("Owner", this.ownerUUID);
        }
        pCompound.putInt("PastTicks", getPastTicks());
    }

    public void setOwner(@Nullable LivingEntity p_36939_) {
        this.owner = p_36939_;
        this.ownerUUID = p_36939_ == null ? null : p_36939_.getUUID();
    }

    @Nullable
    public LivingEntity getOwner() {
        if (this.owner == null && this.ownerUUID != null && this.level() instanceof ServerLevel serverLevel) {
            Entity entity = serverLevel.getEntity(this.ownerUUID);
            if (entity instanceof LivingEntity livingEntity) {
                this.owner = livingEntity;
            }
        }

        return this.owner;
    }

    @Override
    public void tick() {
        super.tick();
        int pastTicks = this.getPastTicks();
        if ((!this.shouldContinue() && pastTicks == getDelay() || (this.shouldContinue() && pastTicks >= getDelay()))) {
            activate();
        }
        if (pastTicks >= this.getLifeTime()) {
            EntityHelper.setBypassProtection(this, true);
            this.discard();
        }
        this.setPastTicks(pastTicks + 1);
    }

    public abstract void activate();

    public abstract int getDelay();

    public abstract int getDuration();

    public int getLifeTime() {
        return getDelay() + getDuration();
    }

    public int getPastTicks() {
        return this.entityData.get(DATA_PAST_TICKS_ID);
    }

    public void setPastTicks(int pastTicks) {
        this.entityData.set(DATA_PAST_TICKS_ID, pastTicks);
    }

    public boolean shouldContinue() {
        return false;
    }
}
