package spctreutils.helper.Visual;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.StagedVertexBuffer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.joml.Vector4f;
import spctreutils.SpctreUtils;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;

public final class RenderHelper
{
    private RenderHelper() {}

    private static final RenderPipeline PIPELINE = RenderPipelines.register(
        RenderPipeline.builder(RenderPipelines.LINES_SNIPPET)
            .withLocation(Identifier.fromNamespaceAndPath(SpctreUtils.MOD_ID, "pipeline/lines_through_walls"))
            .withDepthStencilState(Optional.empty())
            .build());

    private static final StagedVertexBuffer BUFFER =
        new StagedVertexBuffer(() -> SpctreUtils.MOD_ID + " lines", RenderType.SMALL_BUFFER_SIZE);

    private record Outline(AABB box, float r, float g, float b, float a) {}

    private static final List<Outline> QUEUE = new ArrayList<>();

    private static boolean initialized;

    public static void init()
    {
        if (initialized) return;
        initialized = true;

        LevelRenderEvents.AFTER_TRANSLUCENT_TERRAIN.register(RenderHelper::flush);
    }

    public static void close() {BUFFER.close();}

    public static void drawOutline(BlockPos pos, Color color) {drawOutline(new AABB(pos), color);}

    public static void drawOutline(AABB box, Color c)
    {
        QUEUE.add(new Outline(box, c.getRed() / 255f, c.getGreen() / 255f, c.getBlue() / 255f, c.getAlpha() / 255f));
    }

    private static void flush(LevelRenderContext context)
    {
        if (QUEUE.isEmpty()) return;

        StagedVertexBuffer.Draw draw = BUFFER.appendDraw(PIPELINE.getVertexFormatBinding(0), PIPELINE.getPrimitiveTopology(), null);
        VertexConsumer builder = BUFFER.getVertexBuilder(draw);

        PoseStack pose = context.poseStack();
        Vec3 cam = context.levelState().cameraRenderState.pos;
        pose.pushPose();
        pose.translate(-cam.x, -cam.y, -cam.z);
        QUEUE.forEach(o -> addBox(builder, pose.last().pose(), o));
        pose.popPose();
        QUEUE.clear();

        BUFFER.upload();
        StagedVertexBuffer.ExecuteInfo info = BUFFER.getExecuteInfo(draw);
        if (info != null) execute(info);
        BUFFER.endFrame();
    }

    private static void addBox(VertexConsumer b, Matrix4fc pose, Outline o)
    {
        float[] lo = {(float) o.box().minX, (float) o.box().minY, (float) o.box().minZ};
        float[] hi = {(float) o.box().maxX, (float) o.box().maxY, (float) o.box().maxZ};

        for (int axis = 0; axis < 3; axis++)
        {
            int u = (axis + 1) % 3, v = (axis + 2) % 3;
            for (int i = 0; i < 4; i++)
            {
                float[] start = lo.clone();
                start[u] = (i & 1) == 0 ? lo[u] : hi[u];
                start[v] = (i & 2) == 0 ? lo[v] : hi[v];
                float[] end = start.clone();
                end[axis] = hi[axis];

                vertex(b, pose, o, start, axis);
                vertex(b, pose, o, end, axis);
            }
        }
    }

    private static void vertex(VertexConsumer b, Matrix4fc pose, Outline o, float[] p, int axis)
    {
        b.addVertex(pose, p[0], p[1], p[2])
            .setColor(o.r(), o.g(), o.b(), o.a())
            .setNormal(axis == 0 ? 1 : 0, axis == 1 ? 1 : 0, axis == 2 ? 1 : 0)
            .setLineWidth(1f);
    }

    private static void execute(StagedVertexBuffer.ExecuteInfo info)
    {
        GpuBufferSlice transforms = RenderSystem.getDynamicUniforms().writeTransform(
            RenderSystem.getModelViewMatrixCopy(), new Vector4f(1f, 1f, 1f, 1f), new Vector3f(), new Matrix4f());

        RenderTarget target = Minecraft.getInstance().gameRenderer.mainRenderTarget();
        try (RenderPass pass = RenderSystem.getDevice().createCommandEncoder().createRenderPass(
            () -> SpctreUtils.MOD_ID + " lines rendering",
            target.getColorTextureView(), Optional.empty(), target.getDepthTextureView(), OptionalDouble.empty()))
        {
            pass.setPipeline(PIPELINE);
            RenderSystem.bindDefaultUniforms(pass);
            pass.setUniform("DynamicTransforms", transforms);
            pass.setVertexBuffer(0, info.vertexBuffer().slice());
            pass.setIndexBuffer(info.indexBuffer(), info.indexType());
            pass.drawIndexed(info.indexCount(), 1, info.firstIndex(), info.baseVertex(), 0);
        }
    }
}