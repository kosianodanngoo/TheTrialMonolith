package io.github.kosianodangoo.trialmonolith.common.handler;

import io.github.kosianodangoo.trialmonolith.TheTrialMonolith;
import io.github.kosianodangoo.trialmonolith.common.helper.EntityHelper;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
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
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onLivingDeath(LivingDeathEvent event) {
        if (EntityHelper.isSoulProtected(event.getEntity())) {
            event.setCanceled(true);
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
}
