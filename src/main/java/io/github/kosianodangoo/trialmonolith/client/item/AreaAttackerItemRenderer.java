package io.github.kosianodangoo.trialmonolith.client.item;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.kosianodangoo.trialmonolith.client.helper.RenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class AreaAttackerItemRenderer extends BlockEntityWithoutLevelRenderer {
    public AreaAttackerItemRenderer() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
    }

    @Override
    public void renderByItem(@NotNull ItemStack pStack, @NotNull ItemDisplayContext pContext, @NotNull PoseStack pPose, @NotNull MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay) {
        pPose.pushPose();
        pPose.translate(0.5, 0.5, 0.5);
        RenderHelper.renderSphere(pPose, pBuffer.getBuffer(RenderType.debugFilledBox()), -0.5f, 24, 24, 255, 100, 100, 100);
        pPose.popPose();
    }
}
