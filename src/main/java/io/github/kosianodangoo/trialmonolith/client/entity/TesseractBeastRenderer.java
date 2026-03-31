package io.github.kosianodangoo.trialmonolith.client.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.kosianodangoo.trialmonolith.client.helper.TesseractHelper;
import io.github.kosianodangoo.trialmonolith.common.entity.tesseractbeast.TesseractBeastProxyEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector4d;

import java.util.ArrayList;
import java.util.List;

public class TesseractBeastRenderer extends EntityRenderer<TesseractBeastProxyEntity> {
    public static double distance = 3;

    public TesseractBeastRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
    }

    @Override
    public void render(@NotNull TesseractBeastProxyEntity pEntity, float pEntityYaw, float pPartialTick, @NotNull PoseStack pPose, @NotNull MultiBufferSource pBuffer, int pPackedLight) {
        super.render(pEntity, pEntityYaw, pPartialTick, pPose, pBuffer, pPackedLight);

        float deathTime = (pEntity.deathTime + pPartialTick) * 0.05f;
        float time = (pEntity.tickCount + pPartialTick) * 0.06f + deathTime * deathTime + pEntity.id;

        List<Vector4d> rotatedVertices = new ArrayList<>();
        for (Vector4d v : TesseractHelper.DEFAULT_VERTICES) {
            Vector4d rotated = new Vector4d(v);
            // XY plane
            TesseractHelper.rotate4d(rotated, time, 0, 1);
            // XW plane
            TesseractHelper.rotate4d(rotated, time * 0.5, 0, 3);
            // ZW plane
            TesseractHelper.rotate4d(rotated, time * 0.7, 2, 3);
            rotatedVertices.add(rotated);
        }

        pPose.pushPose();
        pPose.translate(0, pEntity.getBbHeight() / 2, 0);
        pPose.scale(4, 4, 4);
        TesseractHelper.renderTesseractFromVertices(rotatedVertices, pPose, pBuffer.getBuffer(RenderType.debugQuads()), distance, 0.05f, 100, 200, 255, 255);
        pPose.popPose();
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull TesseractBeastProxyEntity tesseractBeastProxyEntity) {
        return TextureManager.INTENTIONAL_MISSING_TEXTURE;
    }
}
