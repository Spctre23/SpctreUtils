package spctreutils.helper;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MappableRingBuffer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.lwjgl.system.MemoryUtil;
import spctreutils.SpctreUtils;

import java.util.OptionalDouble;
import java.util.OptionalInt;

public class RenderHelper
{
    private static final RenderPipeline LINES_NO_DEPTH = RenderPipelines.register(
        RenderPipeline.builder(RenderPipelines.LINES_SNIPPET)
            .withLocation(ResourceLocation.fromNamespaceAndPath(SpctreUtils.MOD_ID, "pipeline/lines_no_depth"))
            .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
            .build()
    );

    private static final ByteBufferBuilder allocator = new ByteBufferBuilder(RenderType.SMALL_BUFFER_SIZE);
    private static final Vector4f COLOR_MODULATOR = new Vector4f(1f, 1f, 1f, 1f);
    private static MappableRingBuffer vertexBuffer;
    private static BufferBuilder buffer;

    // --- Block outline overloads ---

    public static void drawOutline(WorldRenderContext context, BlockPos pos, float r, float g, float b, float a, boolean throughWalls)
    {
        drawOutline(context, new AABB(pos), r, g, b, a, throughWalls);
    }

    public static void drawOutline(WorldRenderContext context, BlockPos pos, float r, float g, float b, float a)
    {
        drawOutline(context, pos, r, g, b, a, false);
    }

    // --- AABB outline overloads ---

    public static void drawOutline(WorldRenderContext context, AABB box, float r, float g, float b, float a)
    {
        drawOutline(context, box, r, g, b, a, false);
    }

    public static void drawOutline(WorldRenderContext context, AABB box, float r, float g, float b, float a, boolean throughWalls)
    {
        PoseStack poseStack = context.matrixStack();
        Vec3 cam = context.camera().getPosition();

        poseStack.pushPose();
        poseStack.translate(-cam.x, -cam.y, -cam.z);

        double x1 = box.minX, y1 = box.minY, z1 = box.minZ;
        double x2 = box.maxX, y2 = box.maxY, z2 = box.maxZ;

        if (throughWalls)
        {
            if (buffer == null)
                buffer = new BufferBuilder(allocator, LINES_NO_DEPTH.getVertexFormatMode(), LINES_NO_DEPTH.getVertexFormat());

            Matrix4f mat = poseStack.last().pose();
            addLineToBuffer(buffer, mat, x1,y1,z1, x2,y1,z1, r,g,b,a);
            addLineToBuffer(buffer, mat, x2,y1,z1, x2,y1,z2, r,g,b,a);
            addLineToBuffer(buffer, mat, x2,y1,z2, x1,y1,z2, r,g,b,a);
            addLineToBuffer(buffer, mat, x1,y1,z2, x1,y1,z1, r,g,b,a);
            addLineToBuffer(buffer, mat, x1,y2,z1, x2,y2,z1, r,g,b,a);
            addLineToBuffer(buffer, mat, x2,y2,z1, x2,y2,z2, r,g,b,a);
            addLineToBuffer(buffer, mat, x2,y2,z2, x1,y2,z2, r,g,b,a);
            addLineToBuffer(buffer, mat, x1,y2,z2, x1,y2,z1, r,g,b,a);
            addLineToBuffer(buffer, mat, x1,y1,z1, x1,y2,z1, r,g,b,a);
            addLineToBuffer(buffer, mat, x2,y1,z1, x2,y2,z1, r,g,b,a);
            addLineToBuffer(buffer, mat, x2,y1,z2, x2,y2,z2, r,g,b,a);
            addLineToBuffer(buffer, mat, x1,y1,z2, x1,y2,z2, r,g,b,a);
            drawBuffer(Minecraft.getInstance(), LINES_NO_DEPTH);
        }
        else
        {
            if (context.consumers() == null) { poseStack.popPose(); return; }
            VertexConsumer lines = context.consumers().getBuffer(RenderType.lines());
            Matrix4f mat = poseStack.last().pose();
            line(lines, mat, x1,y1,z1, x2,y1,z1, r,g,b,a);
            line(lines, mat, x2,y1,z1, x2,y1,z2, r,g,b,a);
            line(lines, mat, x2,y1,z2, x1,y1,z2, r,g,b,a);
            line(lines, mat, x1,y1,z2, x1,y1,z1, r,g,b,a);
            line(lines, mat, x1,y2,z1, x2,y2,z1, r,g,b,a);
            line(lines, mat, x2,y2,z1, x2,y2,z2, r,g,b,a);
            line(lines, mat, x2,y2,z2, x1,y2,z2, r,g,b,a);
            line(lines, mat, x1,y2,z2, x1,y2,z1, r,g,b,a);
            line(lines, mat, x1,y1,z1, x1,y2,z1, r,g,b,a);
            line(lines, mat, x2,y1,z1, x2,y2,z1, r,g,b,a);
            line(lines, mat, x2,y1,z2, x2,y2,z2, r,g,b,a);
            line(lines, mat, x1,y1,z2, x1,y2,z2, r,g,b,a);
        }

        poseStack.popPose();
    }

    // --- Internal drawing ---

    private static void drawBuffer(Minecraft client, RenderPipeline pipeline)
    {
        MeshData builtBuffer = buffer.buildOrThrow();
        MeshData.DrawState drawParams = builtBuffer.drawState();
        VertexFormat format = drawParams.format();

        int vertexBufferSize = drawParams.vertexCount() * format.getVertexSize();
        if (vertexBuffer == null || vertexBuffer.size() < vertexBufferSize)
        {
            if (vertexBuffer != null) vertexBuffer.close();
            vertexBuffer = new MappableRingBuffer(
                () -> SpctreUtils.MOD_ID + " lines_no_depth",
                GpuBuffer.USAGE_VERTEX | GpuBuffer.USAGE_MAP_WRITE,
                vertexBufferSize
            );
        }

        CommandEncoder encoder = RenderSystem.getDevice().createCommandEncoder();
        try (GpuBuffer.MappedView view = encoder.mapBuffer(
            vertexBuffer.currentBuffer().slice(0, builtBuffer.vertexBuffer().remaining()), false, true))
        {
            MemoryUtil.memCopy(builtBuffer.vertexBuffer(), view.data());
        }

        GpuBuffer vertices = vertexBuffer.currentBuffer();
        RenderSystem.AutoStorageIndexBuffer indexBuffer = RenderSystem.getSequentialBuffer(pipeline.getVertexFormatMode());
        GpuBuffer indices = indexBuffer.getBuffer(drawParams.indexCount());

        GpuBufferSlice dynamicTransforms = RenderSystem.getDynamicUniforms()
            .writeTransform(RenderSystem.getModelViewMatrix(), COLOR_MODULATOR,
                RenderSystem.getModelOffset(), RenderSystem.getTextureMatrix(), 1f);

        try (RenderPass renderPass = RenderSystem.getDevice()
            .createCommandEncoder()
            .createRenderPass(
                () -> SpctreUtils.MOD_ID + " lines_no_depth rendering",
                client.getMainRenderTarget().getColorTextureView(),
                OptionalInt.empty(),
                client.getMainRenderTarget().getDepthTextureView(),
                OptionalDouble.empty()))
        {
            renderPass.setPipeline(pipeline);
            RenderSystem.bindDefaultUniforms(renderPass);
            renderPass.setUniform("DynamicTransforms", dynamicTransforms);
            renderPass.setVertexBuffer(0, vertices);
            renderPass.setIndexBuffer(indices, indexBuffer.type());
            renderPass.drawIndexed(0, 0, drawParams.indexCount(), 1);
        }

        builtBuffer.close();
        vertexBuffer.rotate();
        buffer = null;
    }

    private static void addLineToBuffer(BufferBuilder buffer, Matrix4f mat,
                                        double x1, double y1, double z1,
                                        double x2, double y2, double z2,
                                        float r, float g, float b, float a)
    {
        float nx = (float)(x2 - x1);
        float ny = (float)(y2 - y1);
        float nz = (float)(z2 - z1);
        float len = (float) Math.sqrt(nx*nx + ny*ny + nz*nz);
        nx /= len; ny /= len; nz /= len;

        buffer.addVertex(mat, (float)x1, (float)y1, (float)z1).setColor(r, g, b, a).setNormal(nx, ny, nz);
        buffer.addVertex(mat, (float)x2, (float)y2, (float)z2).setColor(r, g, b, a).setNormal(nx, ny, nz);
    }

    private static void line(VertexConsumer consumer, Matrix4f mat,
                             double x1, double y1, double z1,
                             double x2, double y2, double z2,
                             float r, float g, float b, float a)
    {
        float nx = (float)(x2-x1), ny = (float)(y2-y1), nz = (float)(z2-z1);
        float len = (float)Math.sqrt(nx*nx + ny*ny + nz*nz);
        nx /= len; ny /= len; nz /= len;

        consumer.addVertex(mat, (float)x1, (float)y1, (float)z1).setColor(r,g,b,a).setNormal(nx,ny,nz);
        consumer.addVertex(mat, (float)x2, (float)y2, (float)z2).setColor(r,g,b,a).setNormal(nx,ny,nz);
    }
}