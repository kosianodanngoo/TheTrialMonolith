package io.github.kosianodangoo.trialmonolith.common.handler;

import io.github.kosianodangoo.trialmonolith.TheTrialMonolith;
import io.github.kosianodangoo.trialmonolith.common.helper.EntityHelper;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
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
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onLivingDeath(LivingDeathEvent event) {
        if (EntityHelper.isSoulProtected(event.getEntity())) {
            event.setCanceled(true);
        }
    }
}
