package io.github.kosianodangoo.trialmonolith.mixin;

import io.github.kosianodangoo.trialmonolith.api.mixin.IOverClocker;
import io.github.kosianodangoo.trialmonolith.api.mixin.ISoulBypass;
import io.github.kosianodangoo.trialmonolith.api.mixin.ISoulDamage;
import io.github.kosianodangoo.trialmonolith.api.mixin.ISoulProtection;
import io.github.kosianodangoo.trialmonolith.common.helper.EntityHelper;
import net.minecraft.commands.CommandSource;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Nameable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.entity.EntityAccess;
import net.minecraftforge.common.capabilities.CapabilityProvider;
import net.minecraftforge.common.extensions.IForgeEntity;
import net.minecraftforge.common.util.ITeleporter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = Entity.class, priority = Integer.MAX_VALUE)
public abstract class EntityMixin extends CapabilityProvider<Entity> implements Nameable, EntityAccess, CommandSource, IForgeEntity, ISoulDamage, ISoulProtection, ISoulBypass, IOverClocker {
    @Shadow
    public SynchedEntityData entityData;

    @Unique
    private static final EntityDataAccessor<Float> the_trial_monolith$DATA_SOUL_DAMAGE_ID = SynchedEntityData.defineId(Entity.class, EntityDataSerializers.FLOAT);
    @Unique
    private static final EntityDataAccessor<Boolean> the_trial_monolith$DATA_SOUL_PROTECTION_ID = SynchedEntityData.defineId(Entity.class, EntityDataSerializers.BOOLEAN);
    @Unique
    private static final EntityDataAccessor<Boolean> the_trial_monolith$DATA_OVER_CLOCKER_ID = SynchedEntityData.defineId(Entity.class, EntityDataSerializers.BOOLEAN);

    @Unique
    private boolean the_trial_monolith$shouldBypass = false;
    @Unique
    private boolean the_trial_monolith$removed = false;

    @Unique
    private boolean the_trial_monolith$initialized = false;

    protected EntityMixin(Class<Entity> baseClass) {
        super(baseClass);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    public void onInit(EntityType p_19870_, Level p_19871_, CallbackInfo ci) {
        this.entityData.define(the_trial_monolith$DATA_SOUL_DAMAGE_ID, 0f);
        this.entityData.define(the_trial_monolith$DATA_SOUL_PROTECTION_ID, false);
        this.entityData.define(the_trial_monolith$DATA_OVER_CLOCKER_ID, false);

        the_trial_monolith$initialized = true;
    }

    public boolean the_trial_monolith$isOverClocked() {
        if (!the_trial_monolith$initialized) {
            return false;
        }
        return this.entityData.get(the_trial_monolith$DATA_OVER_CLOCKER_ID);
    }

    public void the_trial_monolith$setOverClocked(boolean overClocked) {
        if (!the_trial_monolith$initialized) {
            return;
        }
        this.entityData.set(the_trial_monolith$DATA_OVER_CLOCKER_ID, overClocked);
    }

    @Override
    public float the_trial_monolith$getSoulDamage() {
        if (!the_trial_monolith$initialized) {
            return 0;
        }
        return this.entityData.get(the_trial_monolith$DATA_SOUL_DAMAGE_ID);
    }

    @Override
    public void the_trial_monolith$setSoulDamage(float soulDamage) {
        if (!the_trial_monolith$initialized) {
            return;
        }
        this.entityData.set(the_trial_monolith$DATA_SOUL_DAMAGE_ID, soulDamage);
    }

    @Override
    public boolean the_trial_monolith$isSoulProtected() {
        if (!the_trial_monolith$initialized) {
            return false;
        }
        return this.entityData.get(the_trial_monolith$DATA_SOUL_PROTECTION_ID);
    }

    @Override
    public void the_trial_monolith$setSoulProtected(boolean soulDamage) {
        if (!the_trial_monolith$initialized) {
            return;
        }
        this.entityData.set(the_trial_monolith$DATA_SOUL_PROTECTION_ID, soulDamage);
    }

    @Inject(method = "load", at = @At("HEAD"))
    protected void loadMixin(CompoundTag pCompound, CallbackInfo ci) {
        try {
            the_trial_monolith$setSoulProtected(pCompound.getBoolean(EntityHelper.SOUL_PROTECTION_TAG));
            the_trial_monolith$setSoulDamage(pCompound.getFloat(EntityHelper.SOUL_DAMAGE_TAG));
            the_trial_monolith$setOverClocked(pCompound.getBoolean(EntityHelper.OVER_CLOCKED_TAG));
        } catch (Exception ignored) {
        }
    }

    @Inject(method = "saveWithoutId", at = @At("HEAD"))
    protected void saveMixin(CompoundTag pCompound, CallbackInfoReturnable<CompoundTag> cir) {
        try {
            pCompound.putBoolean(EntityHelper.SOUL_PROTECTION_TAG, the_trial_monolith$isSoulProtected());
            pCompound.putFloat(EntityHelper.SOUL_DAMAGE_TAG, the_trial_monolith$getSoulDamage());
            pCompound.putBoolean(EntityHelper.OVER_CLOCKED_TAG, the_trial_monolith$isOverClocked());
        } catch (Exception ignored) {
        }
    }

    @Inject(method = "changeDimension(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraftforge/common/util/ITeleporter;)Lnet/minecraft/world/entity/Entity;", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;removeAfterChangingDimensions()V", remap = true), remap = false)
    protected void changeDimensionBeforeMixin(ServerLevel pLevel, ITeleporter teleporter, CallbackInfoReturnable<Entity> cir) {
        if (!((Object)this instanceof Entity entity)) {
            return;
        }
        EntityHelper.setBypassProtection(entity, true);
    }

    @Inject(method = "changeDimension(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraftforge/common/util/ITeleporter;)Lnet/minecraft/world/entity/Entity;", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;removeAfterChangingDimensions()V", remap = true, shift = At.Shift.AFTER), remap = false)
    protected void changeDimensionAfterMixin(ServerLevel pLevel, ITeleporter teleporter, CallbackInfoReturnable<Entity> cir) {
        if (!((Object)this instanceof Entity entity)) {
            return;
        }
        EntityHelper.setBypassProtection(entity, false);
    }

    @Override
    public boolean the_trial_monolith$shouldBypass() {
        return the_trial_monolith$shouldBypass || the_trial_monolith$removed;
    }

    @Override
    public void the_trial_monolith$setShouldBypass(boolean shouldBypass) {
        this.the_trial_monolith$shouldBypass = shouldBypass;
    }

    @Inject(method = "remove", at=@At("HEAD"), cancellable = true)
    public void removeMixin(Entity.RemovalReason pRemovalReason, CallbackInfo ci) {
        if (!((Object)this instanceof Entity entity)) {
            return;
        }
        if (EntityHelper.shouldBypassProtection(entity)) {
            this.the_trial_monolith$removed = true;
            return;
        }
        if (EntityHelper.isSoulProtected(entity) && !EntityHelper.shouldBypassProtection(entity)) {
            ci.cancel();
        }
    }

    @Inject(method = "setRemoved", at=@At("HEAD"), cancellable = true)
    public void setRemovedMixin(Entity.RemovalReason pRemovalReason, CallbackInfo ci) {
        if (!((Object)this instanceof Entity entity)) {
            return;
        }
        if (EntityHelper.shouldBypassProtection(entity)) {
            this.the_trial_monolith$removed = true;
            return;
        }
        if (EntityHelper.isSoulProtected(entity) && !EntityHelper.shouldBypassProtection(entity)) {
            ci.cancel();
        }
    }
}
