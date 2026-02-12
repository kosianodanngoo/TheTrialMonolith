package io.github.kosianodangoo.trialmonolith.client.entity;

import io.github.kosianodangoo.trialmonolith.TheTrialMonolith;
import io.github.kosianodangoo.trialmonolith.common.entity.TrialMonolithEntity;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class TrialMonolithRenderer extends MobRenderer<TrialMonolithEntity, TrialMonolithModel> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(TheTrialMonolith.getResourceLocation("trial_monolith_layer"), "main");

    public static final ResourceLocation IDLE_TEXTURE_LOCATION = TheTrialMonolith.getResourceLocation("textures/entity/trial_monolith/trial_monolith.png");
    public static final ResourceLocation ACTIVE_TEXTURE_LOCATION = TheTrialMonolith.getResourceLocation("textures/entity/trial_monolith/active_trial_monolith.png");

    public TrialMonolithRenderer(EntityRendererProvider.Context pContext) {
        super(pContext, new TrialMonolithModel(pContext.bakeLayer(LAYER_LOCATION)), 1.5f);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(TrialMonolithEntity trialMonolithEntity) {
        return trialMonolithEntity.isMonolithActive() ? ACTIVE_TEXTURE_LOCATION : IDLE_TEXTURE_LOCATION;
    }
}
