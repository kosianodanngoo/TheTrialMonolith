package io.github.kosianodangoo.trialmonolith.client.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import io.github.kosianodangoo.trialmonolith.client.helper.RenderHelper;
import io.github.kosianodangoo.trialmonolith.common.entity.AreaAttackerEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class AreaAttackerRenderer extends EntityRenderer<AreaAttackerEntity> {
    public AreaAttackerRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
    }

    @Override
    public void render(@NotNull AreaAttackerEntity pEntity, float pEntityYaw, float pPartialTick, @NotNull PoseStack pPose, @NotNull MultiBufferSource pBuffer, int pPackedLight) {
        super.render(pEntity, pEntityYaw, pPartialTick, pPose, pBuffer, pPackedLight);

        double pastTicks = pEntity.getPastTicks() + (double) pPartialTick;

        double progress = Math.min(pastTicks / pEntity.getDelay(), 1);
        double closingTime = Math.min(-(pastTicks - pEntity.getLifeTime()) / 20, 1);

        double radius = 48 * progress * closingTime;

        pPose.pushPose();

        pPose.translate(0, 0.5, 0);

        pPose.mulPose(Axis.YP.rotation((float) pastTicks * 0.3f));
        pPose.mulPose(Axis.XN.rotation((float) pastTicks * 0.5f));
        pPose.mulPose(Axis.ZN.rotation((float) pastTicks * 0.2f));

        if (radius > 0) {
            RenderHelper.renderSphere(pPose, pBuffer.getBuffer(RenderType.debugFilledBox()), (float) radius, 24, 24, 255, 100, 100, 100);
            RenderHelper.renderSphere(pPose, pBuffer.getBuffer(RenderType.debugFilledBox()), (float) -radius, 24, 24, 255, 100, 100, 100);
        }
        pPose.popPose();
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull AreaAttackerEntity areaAttacker) {
        return TextureManager.INTENTIONAL_MISSING_TEXTURE;
    }
}
