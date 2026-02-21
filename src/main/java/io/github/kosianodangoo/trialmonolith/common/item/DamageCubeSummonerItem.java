package io.github.kosianodangoo.trialmonolith.common.item;

import io.github.kosianodangoo.trialmonolith.common.entity.AbstractDelayedTraceableEntity;
import io.github.kosianodangoo.trialmonolith.common.entity.DamageCubeEntity;
import io.github.kosianodangoo.trialmonolith.common.helper.EntityHelper;
import io.github.kosianodangoo.trialmonolith.common.init.TrialMonolithEntities;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class DamageCubeSummonerItem extends Item {
    public DamageCubeSummonerItem(Properties properties) {
        super(properties);
    }

    public DamageCubeSummonerItem() {
        this(new Item.Properties());
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level pLevel, @NotNull Player pPlayer, @NotNull InteractionHand pHand) {
        ItemStack stack = pPlayer.getItemInHand(pHand);
        pPlayer.startUsingItem(pHand);
        return InteractionResultHolder.success(stack);
    }

    @Override
    public int getUseDuration(@NotNull ItemStack pStack) {
        return Integer.MAX_VALUE;
    }

    @Override
    public void onUseTick(@NotNull Level pLevel, @NotNull LivingEntity pLiving, @NotNull ItemStack pStack, int pTick) {
        if (pTick % 2 == 0) {
            this.summonDamageCube(pLevel, pLiving);
        }
    }

    public void summonDamageCube(@NotNull Level pLevel, @NotNull LivingEntity pLiving) {
        Vec3 from = pLiving.getEyePosition();
        Vec3 to = from.add(pLiving.getViewVector(0).multiply(64, 64, 64));
        ClipContext rayTraceContext = new ClipContext(from, to,
                ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, pLiving);
        HitResult hitResult = pLevel.clip(rayTraceContext);
        Vec3 target = hitResult.getLocation();
        EntityHitResult entityHitResult = ProjectileUtil.getEntityHitResult(pLevel, pLiving, from, to, new AABB(from, to), (entity ->
                !(entity instanceof AbstractDelayedTraceableEntity traceable && pLiving == traceable.getOwner()) &&
                        !(entity instanceof Projectile projectile && pLiving == projectile.getOwner()) &&
                        !(entity instanceof ItemEntity)));
        if (entityHitResult != null) {
            target = entityHitResult.getLocation().add(0, entityHitResult.getEntity().getBbHeight() / 2, 0);
        }

        DamageCubeEntity damageCube = new DamageCubeEntity(TrialMonolithEntities.DAMAGE_CUBE.get(), pLevel);
        damageCube.setOwner(pLiving);
        damageCube.setPos(target);

        if(EntityHelper.isSoulProtected(pLiving)) {
            EntityHelper.setSoulProtected(damageCube, true);
        }

        pLevel.addFreshEntity(damageCube);
    }
}
