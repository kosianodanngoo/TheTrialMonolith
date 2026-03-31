package io.github.kosianodangoo.trialmonolith.client.item;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.kosianodangoo.trialmonolith.client.helper.TesseractHelper;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector4d;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class DimensionalCoreItemRenderer extends BlockEntityWithoutLevelRenderer {
    public DimensionalCoreItemRenderer() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
    }

    @Override
    public void renderByItem(@NotNull ItemStack pStack, @NotNull ItemDisplayContext pContext, @NotNull PoseStack pPose, @NotNull MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay) {
        float time = Util.getMillis() * 0.0012f;

        List<Vector4d> rotatedVertices = new ArrayList<>();
        for (Vector4d v : TesseractHelper.DEFAULT_VERTICES) {
            Vector4d rotated = new Vector4d(v);
            // XY plane
            TesseractHelper.rotate4d(rotated, time, 0, 1);
            // XW plane
            TesseractHelper.rotate4d(rotated, time * 1.2, 0, 3);
            // ZW plane
            TesseractHelper.rotate4d(rotated, time * 0.7, 2, 3);
            rotatedVertices.add(rotated);
        }

        pPose.pushPose();
        pPose.translate(0.5, 0.5, 0.5);
        int argb = Color.HSBtoRGB(time * 0.542f, 1, 1);
        int red = (argb % 0x1000000 / 0x10000);
        int green = (argb % 0x10000 / 0x100);
        int blue = (argb % 0x100);
        TesseractHelper.renderTesseractFromVertices(rotatedVertices, pPose, pBuffer.getBuffer(RenderType.debugQuads()), 3, 0.03f, red, blue, green, 255);
        pPose.popPose();
    }
}
