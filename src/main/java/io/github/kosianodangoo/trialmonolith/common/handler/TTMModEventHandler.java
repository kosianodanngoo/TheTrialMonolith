package io.github.kosianodangoo.trialmonolith.common.handler;

import io.github.kosianodangoo.trialmonolith.TheTrialMonolith;
import io.github.kosianodangoo.trialmonolith.common.entity.TrialMonolithEntity;
import io.github.kosianodangoo.trialmonolith.common.init.TrialMonolithEntities;
import io.github.kosianodangoo.trialmonolith.common.init.TrialMonolithItems;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = TheTrialMonolith.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class TTMModEventHandler {
    @SubscribeEvent
    public static void onEntityAttributeCreation(EntityAttributeCreationEvent event) {
        event.put(TrialMonolithEntities.TRIAL_MONOLITH.get(), TrialMonolithEntity.createAttributes());
    }

    @SubscribeEvent
    public void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.COMBAT) {
            event.accept(TrialMonolithItems.BEAM_SUMMONER.get());
            event.accept(TrialMonolithItems.SOUL_PROTECTOR.get());
        }
        if (event.getTabKey() == CreativeModeTabs.SPAWN_EGGS) {
            event.accept(TrialMonolithItems.MONOLITH_SPAWN_EGG.get());
        }
    }
}
