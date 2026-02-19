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

public class SoulProtectorItem extends Item {
    public SoulProtectorItem(Properties properties) {
        super(properties);
    }

    public SoulProtectorItem() {
        this(new Item.Properties());
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level pLevel, @NotNull Player pPlayer, @NotNull InteractionHand pHand) {
        ItemStack stack = pPlayer.getItemInHand(pHand);
        if (pPlayer instanceof ISoulProtection && !pLevel.isClientSide()) {
            EntityHelper.setSoulDamage(pPlayer, 0);
            EntityHelper.toggleSoulProtected(pPlayer);
            boolean isSoulProtected = EntityHelper.isSoulProtected(pPlayer);
            pPlayer.sendSystemMessage(Component.translatable("item.the_trial_monolith.soul_protector.".concat(isSoulProtected ? "enabled" : "disabled")));
            if (isSoulProtected) {
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
            EntityHelper.setSoulDamage(entity, 0);
            EntityHelper.setSoulProtected(entity, true);
            entity.level().playSound(null, entity.blockPosition(), SoundEvents.ANVIL_USE, SoundSource.PLAYERS, 1, 4);
            return true;
        }
        return super.onLeftClickEntity(stack, player, entity);
    }
}
