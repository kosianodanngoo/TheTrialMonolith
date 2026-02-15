package io.github.kosianodangoo.trialmonolith.compat.tinker;

import io.github.kosianodangoo.trialmonolith.common.helper.EntityHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeHitModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.ranged.ProjectileHitModifierHook;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ModifierNBT;

public class SoulSlayerModifier extends Modifier implements ProjectileHitModifierHook, MeleeHitModifierHook {

    @Override
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.PROJECTILE_HIT, ModifierHooks.MELEE_HIT);
    }

    @Override
    public void afterMeleeHit(@NotNull IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damageDealt){
        Entity entity = context.getTarget();

        EntityHelper.addSoulDamage(entity, 0.01f * modifier.getEffectiveLevel());
    }

    @Override
    public boolean onProjectileHitEntity(@NotNull ModifierNBT modifiers, @NotNull ModDataNBT persistentData, ModifierEntry modifier, @NotNull Projectile projectile, @NotNull EntityHitResult hit, LivingEntity attacker, LivingEntity target, boolean notBlocked) {
        EntityHelper.addSoulDamage(target, 0.01f * modifier.getEffectiveLevel());
        return false;
    }
}