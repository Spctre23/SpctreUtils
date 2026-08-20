package spctreutils.helper.Visual;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.pipeline.DepthStencilState;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.CompareOp;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MappableRingBuffer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.system.MemoryUtil;
import spctreutils.SpctreUtils;

import java.awt.*;
import java.util.Optional;
import java.util.OptionalDouble;

public class RenderHelper
{
    private static final RenderPipeline LINES_NO_DEPTH = RenderPipelines.register(
        RenderPipeline.builder(RenderPipelines.LINES_SNIPPET)
            .withLocation(Identifier.fromNamespaceAndPath(SpctreUtils.MOD_ID, "pipeline/lines_no_depth"))
            .withDepthStencilState(new DepthStencilState(CompareOp.ALWAYS_PASS, false))
            .build()
    );

    private static final ByteBufferBuilder allocator = new ByteBufferBuilder(RenderType.SMALL_BUFFER_SIZE);
    private static final Vector4f COLOR_MODULATOR = new Vector4f(1f, 1f, 1f, 1f);
    private static final Vector3f MODEL_OFFSET = new Vector3f();
    private static final Matrix4f TEXTURE_MATRIX = new Matrix4f();
    private static MappableRingBuffer vertexBuffer;
    private static BufferBuilder buffer;

    // --- Block outline overloads ---

    public static void drawOutline(LevelRenderContext context, BlockPos pos, Color color)
    {
        drawOutline(context, new AABB(pos), color.getRed() / 255, color.getGreen() / 255, color.getBlue() / 255, 1f);
    }

    public static void drawOutline(LevelRenderContext context, BlockPos pos, float r, float g, float b, float a)
    {
        drawOutline(context, new AABB(pos), r, g, b, a);
    }

    // --- AABB outline overloads ---

    public static void drawOutline(LevelRenderContext context, AABB box, Color color)
    {
        drawOutline(context, box, color.getRed() / 255, color.getGreen() / 255, color.getBlue() / 255, 1f);
    }

    public static void drawOutline(LevelRenderContext context, AABB box, float r, float g, float b, float a)
    {
        PoseStack poseStack = context.poseStack();
        Vec3 cam = context.gameRenderer().mainCamera().position();

        poseStack.pushPose();
        poseStack.translate(-cam.x, -cam.y, -cam.z);

        double x1 = box.minX, y1 = box.minY, z1 = box.minZ;
        double x2 = box.maxX, y2 = box.maxY, z2 = box.maxZ;

        if (buffer == null)
            buffer = new BufferBuilder(allocator, LINES_NO_DEPTH.getPrimitiveTopology(), LINES_NO_DEPTH.getVertexFormatBinding(0));

        Matrix4f mat = poseStack.last().pose();
        addLineToBuffer(buffer, mat, x1, y1, z1, x2, y1, z1, r, g, b, a);
        addLineToBuffer(buffer, mat, x2, y1, z1, x2, y1, z2, r, g, b, a);
        addLineToBuffer(buffer, mat, x2, y1, z2, x1, y1, z2, r, g, b, a);
        addLineToBuffer(buffer, mat, x1, y1, z2, x1, y1, z1, r, g, b, a);
        addLineToBuffer(buffer, mat, x1, y2, z1, x2, y2, z1, r, g, b, a);
        addLineToBuffer(buffer, mat, x2, y2, z1, x2, y2, z2, r, g, b, a);
        addLineToBuffer(buffer, mat, x2, y2, z2, x1, y2, z2, r, g, b, a);
        addLineToBuffer(buffer, mat, x1, y2, z2, x1, y2, z1, r, g, b, a);
        addLineToBuffer(buffer, mat, x1, y1, z1, x1, y2, z1, r, g, b, a);
        addLineToBuffer(buffer, mat, x2, y1, z1, x2, y2, z1, r, g, b, a);
        addLineToBuffer(buffer, mat, x2, y1, z2, x2, y2, z2, r, g, b, a);
        addLineToBuffer(buffer, mat, x1, y1, z2, x1, y2, z2, r, g, b, a);
        drawBuffer(Minecraft.getInstance(), LINES_NO_DEPTH);

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

        try (GpuBufferSlice.MappedView view = vertexBuffer.currentBuffer()
            .slice(0, builtBuffer.vertexBuffer().remaining())
            .map(false, true))
        {
            MemoryUtil.memCopy(builtBuffer.vertexBuffer(), view.data());
        }

        GpuBuffer vertices = vertexBuffer.currentBuffer();
        RenderSystem.AutoStorageIndexBuffer indexBuffer =
            RenderSystem.getSequentialBuffer(pipeline.getPrimitiveTopology());
        GpuBuffer indices = indexBuffer.getBuffer(drawParams.indexCount());

        GpuBufferSlice dynamicTransforms = RenderSystem.getDynamicUniforms()
            .writeTransform(
                RenderSystem.getModelViewMatrixCopy(),
                new Vector4f(1f, 1f, 1f, 1f),
                new Vector3f(0f, 0f, 0f),
                new Matrix4f()
            );

        try (RenderPass renderPass = RenderSystem.getDevice()
            .createCommandEncoder()
            .createRenderPass(
                () -> SpctreUtils.MOD_ID + " lines_no_depth rendering",
                client.gameRenderer.mainRenderTarget().getColorTextureView(),
                Optional.empty(),
                client.gameRenderer.mainRenderTarget().getDepthTextureView(),
                OptionalDouble.empty()))
        {
            renderPass.setPipeline(pipeline);
            RenderSystem.bindDefaultUniforms(renderPass);
            renderPass.setUniform("DynamicTransforms", dynamicTransforms);
            renderPass.setVertexBuffer(0, vertices.slice());
            renderPass.setIndexBuffer(indices, indexBuffer.type());
            renderPass.drawIndexed(drawParams.indexCount(), 1, 0, 0, 0);
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
        float nx = (float) (x2 - x1);
        float ny = (float) (y2 - y1);
        float nz = (float) (z2 - z1);
        float len = (float) Math.sqrt(nx * nx + ny * ny + nz * nz);
        nx /= len;
        ny /= len;
        nz /= len;

        buffer.addVertex(mat, (float) x1, (float) y1, (float) z1)
            .setColor(r, g, b, a).setNormal(nx, ny, nz).setLineWidth(1.0f);
        buffer.addVertex(mat, (float) x2, (float) y2, (float) z2)
            .setColor(r, g, b, a).setNormal(nx, ny, nz).setLineWidth(1.0f);
    }

    private static void line(VertexConsumer consumer, Matrix4f mat,
                             double x1, double y1, double z1,
                             double x2, double y2, double z2,
                             float r, float g, float b, float a)
    {
        float nx = (float) (x2 - x1), ny = (float) (y2 - y1), nz = (float) (z2 - z1);
        float len = (float) Math.sqrt(nx * nx + ny * ny + nz * nz);
        nx /= len;
        ny /= len;
        nz /= len;

        consumer.addVertex(mat, (float) x1, (float) y1, (float) z1)
            .setColor(r, g, b, a).setNormal(nx, ny, nz).setLineWidth(1.0f);
        consumer.addVertex(mat, (float) x2, (float) y2, (float) z2)
            .setColor(r, g, b, a).setNormal(nx, ny, nz).setLineWidth(1.0f);
    }

    public static void close()
    {
        allocator.close();

        if (vertexBuffer != null)
        {
            vertexBuffer.close();
            vertexBuffer = null;
        }
    }
}