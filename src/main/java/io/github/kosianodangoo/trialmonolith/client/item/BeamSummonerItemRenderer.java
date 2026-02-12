package io.github.kosianodangoo.trialmonolith.client.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import io.github.kosianodangoo.trialmonolith.client.helper.RenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class BeamSummonerItemRenderer extends BlockEntityWithoutLevelRenderer {
    public BeamSummonerItemRenderer() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
    }

    @Override
    public void renderByItem(@NotNull ItemStack pStack, @NotNull ItemDisplayContext pContext, @NotNull PoseStack pPose, @NotNull MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay) {
        VertexConsumer portalVertexConsumer = pBuffer.getBuffer(RenderType.endGateway());
        pPose.pushPose();
        pPose.translate(0.5, 0.5, 0.5);
        RenderHelper.renderHexagon(pPose, portalVertexConsumer, 0.5f);

        pPose.mulPose(Axis.YP.rotationDegrees(180));
        RenderHelper.renderHexagon(pPose, portalVertexConsumer, 0.5f);
        pPose.popPose();
    }

}
