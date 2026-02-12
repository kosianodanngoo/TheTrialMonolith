package io.github.kosianodangoo.trialmonolith.mixin;

import io.github.kosianodangoo.trialmonolith.api.mixin.ISoulDamage;
import io.github.kosianodangoo.trialmonolith.api.mixin.ISoulProtection;
import io.github.kosianodangoo.trialmonolith.common.helper.EntityHelper;
import net.minecraft.commands.CommandSource;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.Nameable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.entity.EntityAccess;
import net.minecraftforge.common.capabilities.CapabilityProvider;
import net.minecraftforge.common.extensions.IForgeEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin extends CapabilityProvider<Entity> implements Nameable, EntityAccess, CommandSource, IForgeEntity, ISoulDamage, ISoulProtection {
    @Shadow
    public SynchedEntityData entityData;

    @Unique
    private static final EntityDataAccessor<Float> the_trial_monolith$DATA_SOUL_DAMAGE_ID = SynchedEntityData.defineId(Entity.class, EntityDataSerializers.FLOAT);
    @Unique
    private static final EntityDataAccessor<Boolean> the_trial_monolith$DATA_SOUL_PROTECTION_ID = SynchedEntityData.defineId(Entity.class, EntityDataSerializers.BOOLEAN);


    protected EntityMixin(Class<Entity> baseClass) {
        super(baseClass);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    public void onInit(EntityType p_19870_, Level p_19871_, CallbackInfo ci) {
        this.entityData.define(the_trial_monolith$DATA_SOUL_DAMAGE_ID, 0f);
        this.entityData.define(the_trial_monolith$DATA_SOUL_PROTECTION_ID, false);
    }

    @Override
    public float the_trial_monolith$getSoulDamage() {
        return this.entityData.get(the_trial_monolith$DATA_SOUL_DAMAGE_ID);
    }

    @Override
    public void the_trial_monolith$setSoulDamage(float soulDamage) {
        this.entityData.set(the_trial_monolith$DATA_SOUL_DAMAGE_ID, soulDamage);
    }

    @Override
    public boolean the_trial_monolith$isSoulProtected() {
        return this.entityData.get(the_trial_monolith$DATA_SOUL_PROTECTION_ID);
    }

    @Override
    public void the_trial_monolith$setSoulProtected(boolean soulDamage) {
        this.entityData.set(the_trial_monolith$DATA_SOUL_PROTECTION_ID, soulDamage);
    }

    @Inject(method = "load", at = @At("HEAD"))
    protected void loadMixin(CompoundTag pCompound, CallbackInfo ci) {
        try {
            the_trial_monolith$setSoulProtected(pCompound.getBoolean(EntityHelper.SOUL_PROTECTION_TAG));
            the_trial_monolith$setSoulDamage(pCompound.getFloat(EntityHelper.SOUL_DAMAGE_TAG));
        } catch (Exception ignored) {
        }
    }

    @Inject(method = "saveWithoutId", at = @At("HEAD"))
    protected void saveMixin(CompoundTag pCompound, CallbackInfoReturnable<CompoundTag> cir) {
        try {
            pCompound.putBoolean(EntityHelper.SOUL_PROTECTION_TAG, the_trial_monolith$isSoulProtected());
            pCompound.putFloat(EntityHelper.SOUL_DAMAGE_TAG, the_trial_monolith$getSoulDamage());
        } catch (Exception ignored) {
        }
    }
}
