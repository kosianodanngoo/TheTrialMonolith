package io.github.kosianodangoo.trialmonolith.client.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import io.github.kosianodangoo.trialmonolith.client.helper.RenderHelper;
import io.github.kosianodangoo.trialmonolith.common.entity.DamageCubeEntity;
import io.github.kosianodangoo.trialmonolith.common.entity.SmallBeamEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

public class DamageCubeRenderer extends EntityRenderer<DamageCubeEntity> {

    public DamageCubeRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
    }

    @Override
    public void render(DamageCubeEntity pEntity, float pEntityYaw, float pPartialTick, PoseStack pPose, MultiBufferSource pBuffer, int pPackedLight) {
        super.render(pEntity, pEntityYaw, pPartialTick, pPose, pBuffer, pPackedLight);

        float pastTicks = pEntity.getPastTicks() + pPartialTick;
        pPose.pushPose();

        pPose.translate(0, 0.5, 0);

        pPose.scale(4f, 4f, 4f);

        float progress = pastTicks/pEntity.getDelay();
        float closingTime = (pastTicks-pEntity.getDelay())/pEntity.getDuration();
        float size;
        if (pastTicks < pEntity.getDelay()) {
            size = progress * progress;
        } else {
            size = 1 - closingTime * closingTime;
        }

        if (size > 0) {
            size = Math.min(size, 1);
            pPose.scale(size, size, size);
            pPose.mulPose(Axis.YP.rotation(2*(1 - progress * progress)));
            VertexConsumer vertexConsumer = pBuffer.getBuffer(RenderType.lightning());
            float finalSize = size * size;
            RenderHelper.renderBox(pPose, vertexConsumer, true, (vertexConsumer1 -> vertexConsumer1.color(1, 0, 0, finalSize)));
        }

        pPose.popPose();
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull DamageCubeEntity damageCubeEntity) {
        return TextureManager.INTENTIONAL_MISSING_TEXTURE;
    }
}
