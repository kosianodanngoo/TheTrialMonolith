package io.github.kosianodangoo.trialmonolith.common.item;

import io.github.kosianodangoo.trialmonolith.api.mixin.ISoulProtection;
import io.github.kosianodangoo.trialmonolith.common.helper.EntityHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class HighDimensionalBarrierItem extends Item {
    public HighDimensionalBarrierItem(Properties properties) {
        super(properties);
    }

    public HighDimensionalBarrierItem() {
        this(new Properties());
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level pLevel, @NotNull Player pPlayer, @NotNull InteractionHand pHand) {
        ItemStack stack = pPlayer.getItemInHand(pHand);
        if (pPlayer instanceof ISoulProtection && !pLevel.isClientSide()) {
            EntityHelper.toggleHighDimensionalBarrier(pPlayer);
            boolean hasHighDimensionalBarrier = EntityHelper.hasHighDimensionalBarrier(pPlayer);
            pPlayer.sendSystemMessage(Component.translatable("item.the_trial_monolith.high_dimensional_barrier.".concat(hasHighDimensionalBarrier ? "enabled" : "disabled")));
            if (hasHighDimensionalBarrier) {
                pLevel.playSound(null, pPlayer.blockPosition(), SoundEvents.ANVIL_USE, SoundSource.PLAYERS, 1, 4);
            } else {
                pLevel.playSound(null, pPlayer.blockPosition(), SoundEvents.GRINDSTONE_USE, SoundSource.PLAYERS, 1, 0);
            }
            return InteractionResultHolder.success(stack);
        }
        return InteractionResultHolder.pass(stack);
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity) {
        if (entity instanceof ISoulProtection && player.isShiftKeyDown()) {
            EntityHelper.setHighDimensionalBarrier(entity, true);
            entity.level().playSound(null, entity.blockPosition(), SoundEvents.ANVIL_USE, SoundSource.PLAYERS, 1, 4);
            return true;
        }
        return super.onLeftClickEntity(stack, player, entity);
    }
}
