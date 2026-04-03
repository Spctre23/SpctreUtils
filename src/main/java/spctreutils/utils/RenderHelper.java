package spctreutils.utils;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import org.joml.Matrix4f;

public class RenderHelper
{
    public static void drawBlockOutline(WorldRenderContext context, BlockPos pos, float r, float g, float b, float a)
    {
        if (context.consumers() == null) return;

        PoseStack poseStack = context.matrixStack();
        Vec3 cam = context.camera().getPosition();

        poseStack.pushPose();
        poseStack.translate(-cam.x, -cam.y, -cam.z);

        Matrix4f mat = poseStack.last().pose();
        VertexConsumer lines = context.consumers().getBuffer(RenderType.lines());

        double x1 = pos.getX(), y1 = pos.getY(), z1 = pos.getZ();
        double x2 = x1 + 1, y2 = y1 + 1, z2 = z1 + 1;

        // bottom
        line(lines, mat, x1,y1,z1, x2,y1,z1, r,g,b,a);
        line(lines, mat, x2,y1,z1, x2,y1,z2, r,g,b,a);
        line(lines, mat, x2,y1,z2, x1,y1,z2, r,g,b,a);
        line(lines, mat, x1,y1,z2, x1,y1,z1, r,g,b,a);
        // top
        line(lines, mat, x1,y2,z1, x2,y2,z1, r,g,b,a);
        line(lines, mat, x2,y2,z1, x2,y2,z2, r,g,b,a);
        line(lines, mat, x2,y2,z2, x1,y2,z2, r,g,b,a);
        line(lines, mat, x1,y2,z2, x1,y2,z1, r,g,b,a);
        // verticals
        line(lines, mat, x1,y1,z1, x1,y2,z1, r,g,b,a);
        line(lines, mat, x2,y1,z1, x2,y2,z1, r,g,b,a);
        line(lines, mat, x2,y1,z2, x2,y2,z2, r,g,b,a);
        line(lines, mat, x1,y1,z2, x1,y2,z2, r,g,b,a);

        poseStack.popPose();
    }

    private static void line(VertexConsumer consumer, Matrix4f mat,
                             double x1, double y1, double z1,
                             double x2, double y2, double z2,
                             float r, float g, float b, float a)
    {
        float nx = (float)(x2 - x1);
        float ny = (float)(y2 - y1);
        float nz = (float)(z2 - z1);
        float len = (float) Math.sqrt(nx*nx + ny*ny + nz*nz);
        nx /= len; ny /= len; nz /= len;

        consumer.addVertex(mat, (float)x1, (float)y1, (float)z1).setColor(r,g,b,a).setNormal(nx,ny,nz);
        consumer.addVertex(mat, (float)x2, (float)y2, (float)z2).setColor(r,g,b,a).setNormal(nx,ny,nz);
    }
}