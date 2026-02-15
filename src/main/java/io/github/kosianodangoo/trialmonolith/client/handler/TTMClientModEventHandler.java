package io.github.kosianodangoo.trialmonolith.client.handler;

import io.github.kosianodangoo.trialmonolith.TheTrialMonolith;
import io.github.kosianodangoo.trialmonolith.client.entity.*;
import io.github.kosianodangoo.trialmonolith.common.init.TrialMonolithEntities;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = TheTrialMonolith.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class TTMClientModEventHandler {
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        EntityRenderers.register(TrialMonolithEntities.TRIAL_MONOLITH.get(), TrialMonolithRenderer::new);
        EntityRenderers.register(TrialMonolithEntities.INVADER_MONOLITH.get(), InvaderMonolithRenderer::new);
        EntityRenderers.register(TrialMonolithEntities.SMALL_BEAM.get(), SmallBeamRenderer::new);
        EntityRenderers.register(TrialMonolithEntities.HUGE_BEAM.get(), HugeBeamRenderer::new);
        EntityRenderers.register(TrialMonolithEntities.DAMAGE_CUBE.get(), DamageCubeRenderer::new);
    }

    @SubscribeEvent
    public static void registerLayer(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(TrialMonolithRenderer.LAYER_LOCATION, TrialMonolithModel::createBodyLayer);
        event.registerLayerDefinition(InvaderMonolithRenderer.LAYER_LOCATION, InvaderMonolithModel::createBodyLayer);
    }
}
