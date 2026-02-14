package io.github.kosianodangoo.trialmonolith.common.item;

import io.github.kosianodangoo.trialmonolith.common.helper.EntityHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class BottleOfTheSoulItem extends Item {
    public BottleOfTheSoulItem(Properties properties) {
        super(properties);
    }

    public BottleOfTheSoulItem() {
        this(new Properties());
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level pLevel, @NotNull Player pPlayer, @NotNull InteractionHand pHand) {
        ItemStack stack = pPlayer.getItemInHand(pHand);
        pPlayer.startUsingItem(pHand);
        return InteractionResultHolder.success(stack);
    }

    @Override
    public @NotNull ItemStack finishUsingItem(@NotNull ItemStack pStack, @NotNull Level pLevel, @NotNull LivingEntity pEntity) {
        EntityHelper.setSoulDamage(pEntity, 0);

        Player player = pEntity instanceof Player ? (Player) pEntity : null;

        if (player == null || !player.getAbilities().instabuild) {
            pStack.shrink(1);
            if (pStack.isEmpty()) {
                return Items.GLASS_BOTTLE.getDefaultInstance();
            } else if (player != null) {
                player.getInventory().add(Items.GLASS_BOTTLE.getDefaultInstance());
            }
        }

        return pStack;
    }

    @Override
    public int getUseDuration(@NotNull ItemStack pStack) {
        return 32;
    }

    public @NotNull UseAnim getUseAnimation(@NotNull ItemStack pStack) {
        return UseAnim.DRINK;
    }
}
