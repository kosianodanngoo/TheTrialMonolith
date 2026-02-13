package io.github.kosianodangoo.trialmonolith.common.init;

import io.github.kosianodangoo.trialmonolith.TheTrialMonolith;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

public class TrialMonolithDamageTypes {
    public static final ResourceKey<DamageType> LASER_ATTACK = ResourceKey.create
            (Registries.DAMAGE_TYPE, TheTrialMonolith.getResourceLocation("laser_attack"));
    public static final ResourceKey<DamageType> CUBE_ATTACK = ResourceKey.create
            (Registries.DAMAGE_TYPE, TheTrialMonolith.getResourceLocation("cube_attack"));

    public static final ResourceKey<DamageType> SOUL_DAMAGE = ResourceKey.create
            (Registries.DAMAGE_TYPE, TheTrialMonolith.getResourceLocation("soul_damage")); // This Damage Type Do not anything special. Registered to show custom death message.

    public static DamageSource laserAttack(Level level, Entity entity) {
        try {
            return new DamageSource(level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(LASER_ATTACK), entity);
        } catch (Exception e) {
            return entity.damageSources().lightningBolt();
        }
    }

    public static DamageSource cubeAttack(Level level, Entity entity) {
        try {
            return new DamageSource(level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(CUBE_ATTACK), entity);
        } catch (Exception e) {
            return entity.damageSources().lightningBolt();
        }
    }

    public static DamageSource soulDamage(Level level) {
        try {
            return new DamageSource(level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(SOUL_DAMAGE));
        } catch (Exception e) {
            return level.damageSources().genericKill();
        }
    }
}
