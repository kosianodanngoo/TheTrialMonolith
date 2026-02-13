package io.github.kosianodangoo.trialmonolith.client.helper;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;

import java.util.function.Function;

public class RenderHelper {
    public static void renderHexagon(PoseStack poseStack, VertexConsumer vertexConsumer, float size) {
        renderHexagon(poseStack, vertexConsumer, size, (vertexConsumer1) -> vertexConsumer1);
    }

    public static void renderHexagon(PoseStack poseStack, VertexConsumer vertexConsumer, float size, Function<VertexConsumer, VertexConsumer> extraElementFiller) {
        float angle = (float) Math.toRadians(360d / 6d);

        poseStack.pushPose();
        poseStack.scale(size, size, size);

        Matrix4f matrix4f = poseStack.last().pose();

        quad(matrix4f, vertexConsumer,
                Mth.cos(0), Mth.sin(0), 0,
                Mth.cos(angle), Mth.sin(angle), 0,
                Mth.cos(angle * 2), Mth.sin(angle * 2), 0,
                Mth.cos(angle * 3), Mth.sin(angle * 3), 0,
                extraElementFiller);

        quad(matrix4f, vertexConsumer,
                Mth.cos(angle * 3), Mth.sin(angle * 3), 0,
                Mth.cos(angle * 4), Mth.sin(angle * 4), 0,
                Mth.cos(angle * 5), Mth.sin(angle * 5), 0,
                Mth.cos(0), Mth.sin(0), 0,
                extraElementFiller);

        poseStack.popPose();
    }

    public static void renderSquarePrism(PoseStack poseStack, VertexConsumer vertexConsumer, float size, float length) {
        renderSquarePrism(poseStack, vertexConsumer, size, length, (vertexConsumer1 -> vertexConsumer1));
    }

    public static void renderSquarePrism(PoseStack poseStack, VertexConsumer vertexConsumer, float size, float length, Function<VertexConsumer, VertexConsumer> extraElementFiller) {
        poseStack.pushPose();
        poseStack.scale(size, size, 1);

        extraElementFiller = extraElementFiller.andThen(vertexConsumer1 -> vertexConsumer1.normal(0, 1, 0));

        for (int i = 0; i < 4; i++) {
            Matrix4f matrix4f = poseStack.last().pose();
            quad(matrix4f, vertexConsumer, -0.5f, -0.5f, 0f,
                    0.5f, -0.5f, 0f,
                    0.5f, -0.5f, length,
                    -0.5f, -0.5f, length,
                    extraElementFiller);
            poseStack.mulPose(Axis.ZN.rotationDegrees(90));
        }

        poseStack.popPose();
    }

    public static void renderBox(PoseStack poseStack, VertexConsumer vertexConsumer, boolean renderInside, Function<VertexConsumer, VertexConsumer> extraElementFiller) {
        poseStack.pushPose();

        for (int i = 0; i < 4; i++) {
            Function<VertexConsumer, VertexConsumer> elementFiller = extraElementFiller.andThen(vertexConsumer1 -> vertexConsumer1.normal(0, 1, 0));

            Matrix4f matrix4f = poseStack.last().pose();
            quad(matrix4f, vertexConsumer, -0.5f, -0.5f, -0.5f,
                    0.5f, -0.5f, -0.5f,
                    0.5f, -0.5f, 0.5f,
                    -0.5f, -0.5f, 0.5f,
                    elementFiller);
            if(renderInside) {
                quad(matrix4f, vertexConsumer, -0.5f, 0.5f, -0.5f,
                        0.5f, 0.5f, -0.5f,
                        0.5f, 0.5f, 0.5f,
                        -0.5f, 0.5f, 0.5f,
                        elementFiller);
            }
            poseStack.mulPose(Axis.ZN.rotationDegrees(90));
        }
        for (int i = 0; i < 2; i++) {
            Matrix4f matrix4f = poseStack.last().pose();
            quad(matrix4f, vertexConsumer, -0.5f, 0.5f, -0.5f,
                    0.5f, 0.5f, -0.5f,
                    0.5f, -0.5f, -0.5f,
                    -0.5f, -0.5f, -0.5f,
                    extraElementFiller.andThen(vertexConsumer1 -> vertexConsumer1.normal(0, 0, -1)));
            if(renderInside) {
                quad(matrix4f, vertexConsumer, -0.5f, 0.5f, 0.5f,
                        0.5f, 0.5f, 0.5f,
                        0.5f, -0.5f, 0.5f,
                        -0.5f, -0.5f, 0.5f,
                        extraElementFiller.andThen(vertexConsumer1 -> vertexConsumer1.normal(0, 0, -1)));
            }
            poseStack.mulPose(Axis.YP.rotationDegrees(180));
        }

        poseStack.popPose();
    }

    public static void quad(Matrix4f matrix4f, VertexConsumer vertexConsumer, float x0, float y0, float z0, float x1, float y1, float z1, float x2, float y2, float z2, float x3, float y3, float z3, Function<VertexConsumer, VertexConsumer> extraElementFiller) {
        extraElementFiller.apply(vertexConsumer.vertex(matrix4f, x0, y0, z0)).endVertex();
        extraElementFiller.apply(vertexConsumer.vertex(matrix4f, x1, y1, z1)).endVertex();
        extraElementFiller.apply(vertexConsumer.vertex(matrix4f, x2, y2, z2)).endVertex();
        extraElementFiller.apply(vertexConsumer.vertex(matrix4f, x3, y3, z3)).endVertex();
    }
}
