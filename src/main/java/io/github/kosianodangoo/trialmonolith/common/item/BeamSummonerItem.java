package io.github.kosianodangoo.trialmonolith.common.item;

import io.github.kosianodangoo.trialmonolith.client.item.BeamSummonerItemRenderer;
import io.github.kosianodangoo.trialmonolith.common.entity.HugeBeamEntity;
import io.github.kosianodangoo.trialmonolith.common.entity.SmallBeamEntity;
import io.github.kosianodangoo.trialmonolith.common.init.TrialMonolithEntities;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class BeamSummonerItem extends Item {
    public BeamSummonerItem(Properties properties) {
        super(properties);
    }

    public BeamSummonerItem() {
        this(new Item.Properties());
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level pLevel, @NotNull Player pPlayer, @NotNull InteractionHand pHand) {
        ItemStack stack = pPlayer.getItemInHand(pHand);
        if (pPlayer.isShiftKeyDown()) {
            shootHugeBeam(pLevel, pPlayer);
            pPlayer.getCooldowns().addCooldown(this, 200);
        } else {
            shootSmallBeam(pLevel, pPlayer);
            pPlayer.startUsingItem(pHand);
        }
        return InteractionResultHolder.success(stack);
    }

    @Override
    public int getUseDuration(@NotNull ItemStack pStack) {
        return Integer.MAX_VALUE;
    }

    @Override
    public void onUseTick(@NotNull Level pLevel, @NotNull LivingEntity pLiving, @NotNull ItemStack pStack, int pTick) {
        shootSmallBeam(pLevel, pLiving);
    }

    @Override
    public void releaseUsing(@NotNull ItemStack pStack, @NotNull Level pLevel, @NotNull LivingEntity pLiving, int pTick) {
        shootSmallBeam(pLevel, pLiving);
        super.releaseUsing(pStack, pLevel, pLiving, pTick);
    }

    public void shootSmallBeam(@NotNull Level pLevel, @NotNull LivingEntity pLiving) {
        SmallBeamEntity smallBeam = new SmallBeamEntity(TrialMonolithEntities.SMALL_BEAM.get(), pLevel);
        smallBeam.setOwner(pLiving);
        RandomSource randomSource = pLevel.getRandom();
        smallBeam.setPos(pLiving.getX() + randomSource.nextDouble() - 0.5,
                pLiving.getY() + pLiving.getBbHeight() + 1.5 + randomSource.nextDouble(),
                pLiving.getZ() + randomSource.nextDouble() - 0.5);

        smallBeam.setXRot(pLiving.getXRot());
        smallBeam.setYRot(pLiving.getYRot());

        pLevel.addFreshEntity(smallBeam);
    }

    public void shootHugeBeam(@NotNull Level pLevel, @NotNull LivingEntity pLiving) {
        HugeBeamEntity hugeBeam = new HugeBeamEntity(TrialMonolithEntities.HUGE_BEAM.get(), pLevel);
        hugeBeam.setOwner(pLiving);

        hugeBeam.setPos(pLiving.getPosition(0).add(pLiving.getViewVector(0).multiply(8, 8, 8)));

        hugeBeam.setXRot(pLiving.getXRot());
        hugeBeam.setYRot(pLiving.getYRot());

        pLevel.addFreshEntity(hugeBeam);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private final BeamSummonerItemRenderer renderer = new BeamSummonerItemRenderer();

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return this.renderer;
            }
        });
    }
}
