package io.github.kosianodangoo.trialmonolith.common.handler;

import io.github.kosianodangoo.trialmonolith.TheTrialMonolith;
import io.github.kosianodangoo.trialmonolith.common.helper.EntityHelper;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = TheTrialMonolith.MOD_ID)
public class TTMForgeEventHandler {
    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        if (EntityHelper.isSoulProtected(event.getOriginal())) {
            EntityHelper.setSoulProtected(event.getEntity(), true);
        }
        if (EntityHelper.isOverClocked(event.getOriginal())) {
            EntityHelper.setOverClocked(event.getEntity(), true);
        }
        if (EntityHelper.hasHighDimensionalBarrier(event.getOriginal())) {
            EntityHelper.setHighDimensionalBarrier(event.getEntity(), true);
        }
        if (EntityHelper.hasDimensionalCore(event.getOriginal())) {
            EntityHelper.setDimensionalCore(event.getEntity(), true);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onLivingDeath(LivingDeathEvent event) {
        if (event.getEntity().isAlive() && EntityHelper.isSoulProtected(event.getEntity())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
    public static void bypassLivingDeathCancelling(LivingDeathEvent event) {
        if (EntityHelper.getSoulDamage(event.getEntity()) >= 1) {
            event.setCanceled(false);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onLivingAttack(LivingAttackEvent event) {
        if (EntityHelper.hasHighDimensionalBarrier(event.getEntity())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onLivingHurt(LivingHurtEvent event) {
        if (EntityHelper.hasHighDimensionalBarrier(event.getEntity())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onLivingDamage(LivingDamageEvent event) {
        if (EntityHelper.hasHighDimensionalBarrier(event.getEntity())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onPlayerInteractedToEntity(PlayerInteractEvent.EntityInteract event) {
        if (EntityHelper.hasHighDimensionalBarrier(event.getTarget())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onAttackEntity(AttackEntityEvent event) {
        if (EntityHelper.hasHighDimensionalBarrier(event.getEntity())) {
            event.setCanceled(true);
        }
    }


    @SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
    public static void bypassInteractCancelling(PlayerInteractEvent event) {
        if (event.isCancelable() && EntityHelper.isOverClocked(event.getEntity())) {
            if (event instanceof PlayerInteractEvent.RightClickBlock rightClickBlock) {
                if (rightClickBlock.isCanceled()) {
                    rightClickBlock.setUseItem(Event.Result.DEFAULT);
                    rightClickBlock.setUseBlock(Event.Result.DEFAULT);
                }
            } else if (event instanceof PlayerInteractEvent.LeftClickBlock leftClickBlock) {
                if (leftClickBlock.isCanceled()) {
                    leftClickBlock.setUseItem(Event.Result.DEFAULT);
                    leftClickBlock.setUseBlock(Event.Result.DEFAULT);
                }
            }
            event.setCanceled(false);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
    public static void bypassBreakCancelling(BlockEvent.BreakEvent event) {
        if (event.isCancelable() && EntityHelper.isOverClocked(event.getPlayer())) {
            event.setCanceled(false);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
    public static void bypassBuildCancelling(BlockEvent.EntityPlaceEvent event) {
        if (event.isCancelable() && EntityHelper.isOverClocked(event.getEntity())) {
            event.setCanceled(false);
        }
    }


    @SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
    public static void bypassAttackCancelling(AttackEntityEvent event) {
        if (EntityHelper.isOverClocked(event.getEntity()))
            event.setCanceled(false);
    }
}
