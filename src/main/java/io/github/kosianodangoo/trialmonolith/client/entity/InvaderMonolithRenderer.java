package io.github.kosianodangoo.trialmonolith.client.entity;

import io.github.kosianodangoo.trialmonolith.TheTrialMonolith;
import io.github.kosianodangoo.trialmonolith.common.entity.invadermonolith.InvaderMonolithEntity;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class InvaderMonolithRenderer extends MobRenderer<InvaderMonolithEntity, InvaderMonolithModel> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(TheTrialMonolith.getResourceLocation("invader_monolith_layer"), "main");

    public static final ResourceLocation TEXTURE_LOCATION = TheTrialMonolith.getResourceLocation("textures/entity/invader_monolith.png");

    public InvaderMonolithRenderer(EntityRendererProvider.Context pContext) {
        super(pContext, new InvaderMonolithModel(pContext.bakeLayer(LAYER_LOCATION)), 1.5f);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(InvaderMonolithEntity trialMonolithEntity) {
        return TEXTURE_LOCATION;
    }
}
