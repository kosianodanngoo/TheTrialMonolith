package io.github.kosianodangoo.trialmonolith.mixin;

import io.github.kosianodangoo.trialmonolith.common.helper.EntityHelper;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemCooldowns;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity {
    @Unique
    private static final ItemCooldowns the_trial_monolith$EMPTY_ITEM_COOLDOWNS = new ItemCooldowns() {
        @Override
        public boolean isOnCooldown(@NotNull Item item) {
            return false;
        }

        @Override
        public void tick() {
        }

        @Override
        public float getCooldownPercent(@NotNull Item item, float p_41523_) {
            return 0;
        }

        @Override
        public void addCooldown(@NotNull Item item, int p_41526_) {
        }

        @Override
        public void removeCooldown(@NotNull Item item) {
        }
    };

    protected PlayerMixin(EntityType<? extends LivingEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Inject(method = "getCooldowns", at = @At("HEAD"), cancellable = true)
    public void getCooldownMixin(CallbackInfoReturnable<ItemCooldowns> cir) {
        if (EntityHelper.isOverClocked(this)) {
            cir.setReturnValue(the_trial_monolith$EMPTY_ITEM_COOLDOWNS);
        }
    }

    @Inject(method = "getAttackStrengthScale", at = @At("HEAD"), cancellable = true)
    public void getAttackStrengthScaleMixin(float partialTick, CallbackInfoReturnable<Float> cir) {
        if (EntityHelper.isOverClocked(this)) {
            cir.setReturnValue(1.0F);
        }
    }

    @Inject(method = "getCurrentItemAttackStrengthDelay", at = @At("HEAD"), cancellable = true)
    public void getCurrentItemAttackStrengthDelayMixin(CallbackInfoReturnable<Float> cir) {
        if (EntityHelper.isOverClocked(this)) {
            cir.setReturnValue(0.0F);
        }
    }
}
