package io.github.kosianodangoo.trialmonolith.common.item;

import io.github.kosianodangoo.trialmonolith.client.item.AreaAttackerItemRenderer;
import io.github.kosianodangoo.trialmonolith.common.entity.AreaAttackerEntity;
import io.github.kosianodangoo.trialmonolith.common.helper.EntityHelper;
import io.github.kosianodangoo.trialmonolith.common.init.TrialMonolithEntities;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class AreaAttackerItem extends Item {
    public AreaAttackerItem(Properties properties) {
        super(properties);
    }

    public AreaAttackerItem() {
        this(new Item.Properties());
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level pLevel, @NotNull Player pPlayer, @NotNull InteractionHand pHand) {
        ItemStack stack = pPlayer.getItemInHand(pHand);
        pPlayer.startUsingItem(pHand);

        AreaAttackerEntity areaAttacker = new AreaAttackerEntity(TrialMonolithEntities.AREA_ATTACKER.get(), pLevel);
        areaAttacker.setOwner(pPlayer);

        areaAttacker.setPos(pPlayer.getPosition(0));

        if (EntityHelper.isSoulProtected(pPlayer)) {
            EntityHelper.setSoulProtected(areaAttacker, true);
        }
        if (EntityHelper.hasDimensionalCore(pPlayer)) {
            areaAttacker.setHighDimensional(true);
        }

        pLevel.addFreshEntity(areaAttacker);

        return InteractionResultHolder.success(stack);
    }

    @Override
    public int getUseDuration(@NotNull ItemStack pStack) {
        return Integer.MAX_VALUE;
    }

    @Override
    public void releaseUsing(@NotNull ItemStack pStack, @NotNull Level pLevel, @NotNull LivingEntity pLiving, int pTick) {
        removeArea(pLevel, pLiving);
        super.releaseUsing(pStack, pLevel, pLiving, pTick);
    }

    @Override
    public void onStopUsing(ItemStack stack, LivingEntity entity, int count) {
        removeArea(entity.level, entity);
        super.onStopUsing(stack, entity, count);
    }

    public void removeArea(Level level, LivingEntity livingEntity) {
        level.getEntities(EntityTypeTest.forClass(AreaAttackerEntity.class), livingEntity.getBoundingBox().inflate(10), (areaAttackerEntity -> areaAttackerEntity.getOwner() == livingEntity)).forEach((areaAttackerEntity ->
            areaAttackerEntity.setPastTicks(areaAttackerEntity.getLifeTime() - 20)
        ));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private final AreaAttackerItemRenderer renderer = new AreaAttackerItemRenderer();

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return this.renderer;
            }
        });
    }
}
