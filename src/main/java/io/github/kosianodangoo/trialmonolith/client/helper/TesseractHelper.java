package io.github.kosianodangoo.trialmonolith.client.helper;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import org.joml.Matrix4f;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.joml.Vector4d;

import java.util.ArrayList;
import java.util.List;

public class TesseractHelper {
    public static final List<Vector4d> DEFAULT_VERTICES;


    static {
        ArrayList<Vector4d> vertices = new ArrayList<>(16);
        for (int i = 0; i < 16; i++) {
            vertices.add(new Vector4d((i & 1) == 1 ? -1 : 1, (i >> 1 & 1) == 1 ? -1 : 1, (i >> 2 & 1) == 1 ? -1 : 1, (i >> 3 & 1) == 1 ? -1 : 1));
        }

        DEFAULT_VERTICES = List.copyOf(vertices);
    }

    public static void rotate4d(Vector4d v, double angle, int axis1, int axis2) {
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        double a = getValue(v, axis1);
        double b = getValue(v, axis2);

        setValue(v, axis1, a * cos - b * sin);
        setValue(v, axis2, a * sin + b * cos);
    }

    public static double getValue(Vector4d v, int axis) {
        return switch (axis) {
            case 0 -> v.x;
            case 1 -> v.y;
            case 2 -> v.z;
            case 3 -> v.w;
            default -> 0;
        };
    }

    public static void setValue(Vector4d v, int axis, double val) {
        switch (axis) {
            case 0 -> v.x = val;
            case 1 -> v.y = val;
            case 2 -> v.z = val;
            case 3 -> v.w = val;
        }
    }

    public static void renderTesseractFromVertices(List<Vector4d> vertices, PoseStack poseStack, VertexConsumer vertexConsumer, double distance, float thickness, int r, int g, int b, int a) {
        List<Vector3d> verticesIn3d = vertices.stream().map(v -> {
            double wScale = 1.0 / (distance - v.w);
            return new Vector3d(v.x * wScale, v.y * wScale, v.z * wScale);
        }).toList();

        Matrix4f matrix = poseStack.last().pose();

        for (int i = 0; i < verticesIn3d.size(); i++) {
            for (int j = i + 1; j < verticesIn3d.size(); j++) {
                int diff = i ^ j;
                if ((diff & (diff - 1)) == 0) {
                    drawEdge(verticesIn3d.get(i), verticesIn3d.get(j), matrix, vertexConsumer, thickness, r, g, b, a);
                }
            }
        }
    }

    private static void drawEdge(Vector3d start, Vector3d end, Matrix4f matrix, VertexConsumer consumer, float thickness, int r, int g, int b, int a) {
        float h = thickness / 2.0f;

        float dx = (float) (end.x - start.x);
        float dy = (float) (end.y - start.y);
        float dz = (float) (end.z - start.z);

        Vector3f dir = new Vector3f(dx, dy, dz);
        dir.normalize();

        Vector3f up = new Vector3f(0, 1, 0);
        if (Math.abs(dir.y) > 0.9f) {
            up = new Vector3f(1, 0, 0);
        }
        Vector3f right = new Vector3f();
        dir.cross(up, right);
        right.normalize();
        right.mul(h);

        dir.cross(right, up);
        up.normalize();
        up.mul(h);

        float[][] offsets = {
                {right.x + up.x, right.y + up.y, right.z + up.z},
                {right.x - up.x, right.y - up.y, right.z - up.z},
                {-right.x - up.x, -right.y - up.y, -right.z - up.z},
                {-right.x + up.x, -right.y + up.y, -right.z + up.z}
        };

        for (int i = 0; i < 4; i++) {
            int next = (i + 1) % 4;
            addVertex(consumer, matrix, (float) start.x + offsets[i][0], (float) start.y + offsets[i][1], (float) start.z + offsets[i][2], r, g, b, a);
            addVertex(consumer, matrix, (float) start.x + offsets[next][0], (float) start.y + offsets[next][1], (float) start.z + offsets[next][2], r, g, b, a);
            addVertex(consumer, matrix, (float) end.x + offsets[next][0], (float) end.y + offsets[next][1], (float) end.z + offsets[next][2], r, g, b, a);
            addVertex(consumer, matrix, (float) end.x + offsets[i][0], (float) end.y + offsets[i][1], (float) end.z + offsets[i][2], r, g, b, a);
        }
    }

    private static void addVertex(VertexConsumer consumer, Matrix4f matrix, float x, float y, float z, int r, int g, int b, int a) {
        consumer.vertex(matrix, x, y, z)
                .color(r, g, b, a)
                .uv2(15728880)
                .normal(0, 1, 0)
                .endVertex();
    }
}
