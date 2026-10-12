package spctreutils.helper.Visual;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.builders.UVPair;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.StagedVertexBuffer;
import net.minecraft.client.renderer.block.MovingBlockRenderState;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
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

    // ------------------------------------------------------------------
    // Setup
    // ------------------------------------------------------------------

    private static final int FULL_BRIGHT = 0xF000F0;   // max block + sky light; ghost ignores world light
    private static final Direction[] DIRECTIONS = Direction.values();

    private static boolean initialized;

    /** Safe to call more than once. Registers the outline flush. */
    public static void init()
    {
        if (initialized) return;
        initialized = true;

        // Features queue outlines during COLLECT_SUBMITS, which fires before END_MAIN,
        // so everything queued this frame is drawn this frame.
        LevelRenderEvents.END_MAIN.register(RenderHelper::flushOutlines);
    }

    /** Call from the GameRenderer#close mixin. */
    public static void close()
    {
        OUTLINE_BUFFER.close();
    }

    /** Draws a semi-transparent block model at pos. Call from COLLECT_SUBMITS. */
    public static void drawBlock(LevelRenderContext context, BlockPos pos, BlockState state, float alpha)
    {
        drawBlock(context, new AABB(pos), state, alpha);
    }

    /** Draws a semi-transparent block model in every position the box covers, culling hidden faces. */
    public static void drawBlock(LevelRenderContext context, AABB area, BlockState state, float alpha)
    {
        int minX = Mth.floor(area.minX), maxX = Mth.ceil(area.maxX) - 1;
        int minY = Mth.floor(area.minY), maxY = Mth.ceil(area.maxY) - 1;
        int minZ = Mth.floor(area.minZ), maxZ = Mth.ceil(area.maxZ) - 1;

        BlockStateModel model = Minecraft.getInstance().getModelManager().getBlockStateModelSet().get(state); // VERIFY
        boolean cube = state.isSolidRender();                                                                  // VERIFY
        int color = (Mth.clamp((int) (alpha * 255f), 0, 255) << 24) | 0xFFFFFF;

        PoseStack pose = context.poseStack();
        Vec3 cam = context.levelState().cameraRenderState.pos;
        List<BlockStateModelPart> parts = new ArrayList<>();

        for (int x = minX; x <= maxX; x++)
            for (int y = minY; y <= maxY; y++)
                for (int z = minZ; z <= maxZ; z++)
                {
                    // Full cubes with all six neighbors in the area have no visible faces
                    boolean interior = x > minX && x < maxX && y > minY && y < maxY && z > minZ && z < maxZ;
                    if (interior && cube) continue;

                    BlockPos pos = new BlockPos(x, y, z);
                    parts.clear();
                    model.collectParts(RandomSource.create(state.getSeed(pos)), parts);

                    List<BakedQuad> quads = new ArrayList<>();
                    for (BlockStateModelPart part : parts)
                    {
                        quads.addAll(part.getQuads(null));   // never culled
                        for (Direction d : DIRECTIONS)
                        {
                            int nx = x + d.getStepX(), ny = y + d.getStepY(), nz = z + d.getStepZ();
                            boolean neighborInArea = nx >= minX && nx <= maxX && ny >= minY && ny <= maxY && nz >= minZ && nz <= maxZ;
                            if (cube && neighborInArea) continue;   // face is against another ghost block
                            quads.addAll(part.getQuads(d));
                        }
                    }
                    if (quads.isEmpty()) continue;

                    pose.pushPose();
                    pose.translate(x - cam.x, y - cam.y, z - cam.z);
                    context.submitNodeCollector().submitCustomGeometry(pose, RenderTypes.translucentMovingBlock(),
                        (p, buffer) -> { for (BakedQuad quad : quads) writeQuad(p, buffer, quad, color); });
                    pose.popPose();
                }
    }

    /** Same vertex layout as vanilla's putBakedQuad, but with our own color (so we control alpha). */
    private static void writeQuad(PoseStack.Pose pose, VertexConsumer buffer, BakedQuad quad, int color)
    {
        Vector3f normal = pose.transformNormal(quad.direction().getUnitVec3f(), new Vector3f());
        for (int i = 0; i < BakedQuad.VERTEX_COUNT; i++)
        {
            Vector3f p = pose.pose().transformPosition(quad.position(i), new Vector3f());
            long uv = quad.packedUV(i);
            buffer.addVertex(p.x(), p.y(), p.z(), color,
                UVPair.unpackU(uv), UVPair.unpackV(uv),
                OverlayTexture.NO_OVERLAY, FULL_BRIGHT,
                normal.x(), normal.y(), normal.z());
        }
    }

    private static final RenderPipeline OUTLINE_PIPELINE = RenderPipelines.register(
        RenderPipeline.builder(RenderPipelines.LINES_SNIPPET)
            .withLocation(Identifier.fromNamespaceAndPath(SpctreUtils.MOD_ID, "pipeline/lines_through_walls"))
            .withDepthStencilState(Optional.empty())
            .build());

    private static final StagedVertexBuffer OUTLINE_BUFFER =
        new StagedVertexBuffer(() -> SpctreUtils.MOD_ID + " lines", RenderType.SMALL_BUFFER_SIZE);

    /** Immutable render state: everything the draw needs, nothing it has to look up. */
    private record Outline(AABB box, float r, float g, float b, float a) {}

    private static final List<Outline> OUTLINE_QUEUE = new ArrayList<>();

    public static void drawOutline(BlockPos pos, Color color)
    {
        drawOutline(new AABB(pos), color);
    }

    public static void drawOutline(AABB box, Color c)
    {
        OUTLINE_QUEUE.add(new Outline(box,
            c.getRed() / 255f, c.getGreen() / 255f, c.getBlue() / 255f, c.getAlpha() / 255f));
    }

    private static void flushOutlines(LevelRenderContext context)
    {
        if (OUTLINE_QUEUE.isEmpty()) return;

        VertexFormat format = OUTLINE_PIPELINE.getVertexFormatBinding(0);
        assert format != null;

        StagedVertexBuffer.Draw draw =
            OUTLINE_BUFFER.appendDraw(format, OUTLINE_PIPELINE.getPrimitiveTopology(), null);
        VertexConsumer builder = OUTLINE_BUFFER.getVertexBuilder(draw);

        PoseStack pose = context.poseStack();
        Vec3 cam = context.levelState().cameraRenderState.pos;
        pose.pushPose();
        pose.translate(-cam.x, -cam.y, -cam.z);
        for (Outline outline : OUTLINE_QUEUE) addBox(builder, pose.last().pose(), outline);
        pose.popPose();
        OUTLINE_QUEUE.clear();

        OUTLINE_BUFFER.upload();
        StagedVertexBuffer.ExecuteInfo info = OUTLINE_BUFFER.getExecuteInfo(draw);
        if (info != null) executeOutlines(info);
        OUTLINE_BUFFER.endFrame();
    }

    private static void addBox(VertexConsumer b, Matrix4fc pose, Outline o)
    {
        float x1 = (float) o.box().minX, y1 = (float) o.box().minY, z1 = (float) o.box().minZ;
        float x2 = (float) o.box().maxX, y2 = (float) o.box().maxY, z2 = (float) o.box().maxZ;

        // bottom face
        line(b, pose, o, x1, y1, z1, x2, y1, z1);
        line(b, pose, o, x2, y1, z1, x2, y1, z2);
        line(b, pose, o, x2, y1, z2, x1, y1, z2);
        line(b, pose, o, x1, y1, z2, x1, y1, z1);
        // top face
        line(b, pose, o, x1, y2, z1, x2, y2, z1);
        line(b, pose, o, x2, y2, z1, x2, y2, z2);
        line(b, pose, o, x2, y2, z2, x1, y2, z2);
        line(b, pose, o, x1, y2, z2, x1, y2, z1);
        // vertical edges
        line(b, pose, o, x1, y1, z1, x1, y2, z1);
        line(b, pose, o, x2, y1, z1, x2, y2, z1);
        line(b, pose, o, x2, y1, z2, x2, y2, z2);
        line(b, pose, o, x1, y1, z2, x1, y2, z2);
    }

    private static void line(VertexConsumer b, Matrix4fc pose, Outline o,
                             float x1, float y1, float z1, float x2, float y2, float z2)
    {
        float nx = x2 - x1, ny = y2 - y1, nz = z2 - z1;
        float len = (float) Math.sqrt(nx * nx + ny * ny + nz * nz);
        if (len == 0f) return;
        nx /= len; ny /= len; nz /= len;

        b.addVertex(pose, x1, y1, z1).setColor(o.r(), o.g(), o.b(), o.a()).setNormal(nx, ny, nz).setLineWidth(1f);
        b.addVertex(pose, x2, y2, z2).setColor(o.r(), o.g(), o.b(), o.a()).setNormal(nx, ny, nz).setLineWidth(1f);
    }

    private static void executeOutlines(StagedVertexBuffer.ExecuteInfo info)
    {
        GpuBufferSlice transforms = RenderSystem.getDynamicUniforms().writeTransform(
            RenderSystem.getModelViewMatrixCopy(), new Vector4f(1f, 1f, 1f, 1f), new Vector3f(), new Matrix4f());

        RenderTarget target = Minecraft.getInstance().gameRenderer.mainRenderTarget();
        try (RenderPass pass = RenderSystem.getDevice().createCommandEncoder().createRenderPass(
            () -> SpctreUtils.MOD_ID + " lines rendering",
            target.getColorTextureView(), Optional.empty(), target.getDepthTextureView(), OptionalDouble.empty()))
        {
            pass.setPipeline(OUTLINE_PIPELINE);
            RenderSystem.bindDefaultUniforms(pass);
            pass.setUniform("DynamicTransforms", transforms);
            pass.setVertexBuffer(0, info.vertexBuffer().slice());
            pass.setIndexBuffer(info.indexBuffer(), info.indexType());
            pass.drawIndexed(info.indexCount(), 1, info.firstIndex(), info.baseVertex(), 0);
        }
    }
}