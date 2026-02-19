package io.github.kosianodangoo.trialmonolith.common.item;

import io.github.kosianodangoo.trialmonolith.api.mixin.ISoulProtection;
import io.github.kosianodangoo.trialmonolith.common.helper.EntityHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class OverClockerItem extends Item {
    public OverClockerItem(Properties properties) {
        super(properties);
    }

    public OverClockerItem() {
        this(new Properties());
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level pLevel, @NotNull Player pPlayer, @NotNull InteractionHand pHand) {
        ItemStack stack = pPlayer.getItemInHand(pHand);
        if (pPlayer instanceof ISoulProtection && !pLevel.isClientSide()) {
            EntityHelper.toggleOverClocked(pPlayer);
            boolean isOverClocked = EntityHelper.isOverClocked(pPlayer);
            pPlayer.sendSystemMessage(Component.translatable("item.the_trial_monolith.over_clocker.".concat(isOverClocked ? "enabled" : "disabled")));
            if (isOverClocked) {
                pLevel.playSound(null, pPlayer.blockPosition(), SoundEvents.BEACON_ACTIVATE, SoundSource.PLAYERS, 1, 4);
            } else {
                pLevel.playSound(null, pPlayer.blockPosition(), SoundEvents.BEACON_DEACTIVATE, SoundSource.PLAYERS, 1, 4);
            }
            return InteractionResultHolder.success(stack);
        }
        return InteractionResultHolder.pass(stack);
    }
}
