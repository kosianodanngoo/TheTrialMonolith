package io.github.kosianodangoo.trialmonolith.client.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import io.github.kosianodangoo.trialmonolith.client.helper.RenderHelper;
import io.github.kosianodangoo.trialmonolith.common.entity.HugeBeamEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

public class HugeBeamRenderer extends EntityRenderer<HugeBeamEntity> {
    public static final int BEAM_STARTUP = 20;
    public static final int BEAM_END = 20;
    public static final int PORTAL_END = 10;

    public HugeBeamRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
    }

    @Override
    public void render(HugeBeamEntity pEntity, float pEntityYaw, float pPartialTick, PoseStack pPose, MultiBufferSource pBuffer, int pPackedLight) {
        super.render(pEntity, pEntityYaw, pPartialTick, pPose, pBuffer, pPackedLight);

        int pastTicks = pEntity.getPastTicks();
        pPose.pushPose();

        pPose.translate(0, 0.5, 0);

        pPose.mulPose(Axis.YP.rotationDegrees(-pEntityYaw));
        pPose.mulPose(Axis.XP.rotationDegrees(pEntity.getXRot()));

        float portalProgress = Mth.clamp((pastTicks + pPartialTick) / pEntity.getDelay(), 0, 1);
        portalProgress = Math.min(portalProgress, Mth.lerp((pastTicks - pEntity.getLifeTime() + PORTAL_END + pPartialTick) / (PORTAL_END), 1, 0));

        float portalSize = portalProgress * portalProgress * 16f;


        if (portalSize > 0) {
            renderPortal(pPose, pBuffer, portalSize);
        }

        float beamProgress = 0;

        if (pastTicks + pPartialTick >= pEntity.getLifeTime() - BEAM_END) {
            beamProgress = Mth.lerp((pastTicks + pPartialTick - pEntity.getLifeTime() + BEAM_END) / BEAM_END, 1f, 0f);
        } else if (pastTicks + pPartialTick >= pEntity.getDelay()) {
            beamProgress = 1;
        } else if (pastTicks + pPartialTick >= pEntity.getDelay() - BEAM_STARTUP) {
            beamProgress = Mth.lerp((pastTicks + pPartialTick - pEntity.getDelay() + BEAM_STARTUP) / BEAM_STARTUP, 0f, 1f);
        }

        if (beamProgress > 0) {
            beamProgress = Math.min(beamProgress, 1);
            renderBeam(pPose, pBuffer, beamProgress);
        }
        pPose.popPose();
    }

    public void renderPortal(PoseStack poseStack, MultiBufferSource buffer, float size) {
        VertexConsumer portalVertexConsumer = buffer.getBuffer(RenderType.endGateway());
        RenderHelper.renderHexagon(poseStack, portalVertexConsumer, size);

        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(180));
        RenderHelper.renderHexagon(poseStack, portalVertexConsumer, size);
        poseStack.popPose();
    }

    public void renderBeam(PoseStack poseStack, MultiBufferSource buffer, float progress) {
        float progressSquared = progress * progress;
        float size = progressSquared * 16f;

        VertexConsumer beamVertexConsumer = buffer.getBuffer(RenderType.lightning());

        RenderHelper.renderSquarePrism(poseStack, beamVertexConsumer, size, 128, (vertexConsumer -> vertexConsumer.color(1, 1, 0, 0.5f * progressSquared)));

        RenderHelper.renderSquarePrism(poseStack, beamVertexConsumer, size / 3, 128, (vertexConsumer -> vertexConsumer.color(1, 1, 1, Math.min(1.5f * progressSquared, 1))));
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull HugeBeamEntity smallBeamEntity) {
        return TextureManager.INTENTIONAL_MISSING_TEXTURE;
    }
}
