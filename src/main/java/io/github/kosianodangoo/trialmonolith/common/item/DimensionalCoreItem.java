package io.github.kosianodangoo.trialmonolith.common.item;

import io.github.kosianodangoo.trialmonolith.api.mixin.ISoulProtection;
import io.github.kosianodangoo.trialmonolith.client.item.DimensionalCoreItemRenderer;
import io.github.kosianodangoo.trialmonolith.common.helper.EntityHelper;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class DimensionalCoreItem extends Item {
    public DimensionalCoreItem(Item.Properties properties) {
        super(properties);
    }

    public DimensionalCoreItem() {
        this(new Properties());
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level pLevel, @NotNull Player pPlayer, @NotNull InteractionHand pHand) {
        ItemStack stack = pPlayer.getItemInHand(pHand);
        if (pPlayer instanceof ISoulProtection && !pLevel.isClientSide()) {
            EntityHelper.toggleDimensionalCore(pPlayer);
            boolean hasDimensionalCore = EntityHelper.hasDimensionalCore(pPlayer);
            pPlayer.sendSystemMessage(Component.translatable("item.the_trial_monolith.dimensional_core.".concat(hasDimensionalCore ? "enabled" : "disabled")));
            if (hasDimensionalCore) {
                pLevel.playSound(null, pPlayer.blockPosition(), SoundEvents.WITHER_SPAWN, SoundSource.PLAYERS, 1, 0);
            } else {
                pLevel.playSound(null, pPlayer.blockPosition(), SoundEvents.GLASS_BREAK, SoundSource.PLAYERS, 1, 2);
            }
            return InteractionResultHolder.success(stack);
        }
        return InteractionResultHolder.pass(stack);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private final DimensionalCoreItemRenderer renderer = new DimensionalCoreItemRenderer();

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return this.renderer;
            }
        });
    }
}
