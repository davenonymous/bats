package org.dave.bats.util;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.dave.bats.func.batcage.BatCageLinkConfig;
import org.lwjgl.opengl.GL11;
import org.mini2Dx.gdx.math.CatmullRomSpline;
import org.mini2Dx.gdx.math.Vector3;

import java.util.Objects;

public class LineRenderer {
    private static Tessellator renderPullUp(EntityPlayer player, float partialTicks) {
        GlStateManager.pushMatrix();

        Vec3d cameraPosition = new Vec3d(
                player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTicks,
                player.lastTickPosY + (player.posY - player.lastTickPosY) * partialTicks,
                player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTicks
        );

        GlStateManager.translate(-cameraPosition.x, -cameraPosition.y, -cameraPosition.z);

        Tessellator tessellator = Tessellator.getInstance();

        //GlStateManager.disableDepth();
        GlStateManager.disableTexture2D();
        GlStateManager.disableLighting();

        GlStateManager.depthMask(false);
        GlStateManager.enableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);

        return tessellator;
    }

    private static void renderTearDown() {
        GlStateManager.disableBlend();
        GlStateManager.enableDepth();
        GlStateManager.enableTexture2D();

        GlStateManager.enableLighting();

        GlStateManager.popMatrix();
    }

    public static void renderTargetFace(BatCageLinkConfig link, EntityPlayer player, float partialTicks, int argb) {
        Tessellator tessellator = renderPullUp(player, partialTicks);

        int a = (argb >> 24) & 0xFF;
        int r = (argb >> 16) & 0xFF;
        int g = (argb >> 8) & 0xFF;
        int b = (argb & 0xFF);

        BlockPos pos = link.pos;
        if(link.side == EnumFacing.UP) {
            Vec3d A = new Vec3d(pos.getX(), pos.getY()+1.0f, pos.getZ());
            Vec3d B = A.add(1.0f, 0.0f, 0.0f);
            Vec3d C = B.add(0.0f, 0.0f, 1.0f);
            Vec3d D = C.add(-1.0f, 0.0f, 0.0f);

            renderLine(tessellator, new Line(A, B), r, g, b, a);
            renderLine(tessellator, new Line(B, C), r, g, b, a);
            renderLine(tessellator, new Line(C, D), r, g, b, a);
            renderLine(tessellator, new Line(D, A), r, g, b, a);
        }

        if(link.side == EnumFacing.DOWN) {
            Vec3d A = new Vec3d(pos.getX(), pos.getY(), pos.getZ());
            Vec3d B = A.add(1.0f, 0.0f, 0.0f);
            Vec3d C = B.add(0.0f, 0.0f, 1.0f);
            Vec3d D = C.add(-1.0f, 0.0f, 0.0f);

            renderLine(tessellator, new Line(A, B), r, g, b, a);
            renderLine(tessellator, new Line(B, C), r, g, b, a);
            renderLine(tessellator, new Line(C, D), r, g, b, a);
            renderLine(tessellator, new Line(D, A), r, g, b, a);
        }

        if(link.side == EnumFacing.NORTH) {
            Vec3d A = new Vec3d(pos.getX(), pos.getY(), pos.getZ());
            Vec3d B = A.add(1.0f, 0.0f, 0.0f);
            Vec3d C = B.add(0.0f, 1.0f, 0.0f);
            Vec3d D = C.add(-1.0f, 0.0f, 0.0f);

            renderLine(tessellator, new Line(A, B), r, g, b, a);
            renderLine(tessellator, new Line(B, C), r, g, b, a);
            renderLine(tessellator, new Line(C, D), r, g, b, a);
            renderLine(tessellator, new Line(D, A), r, g, b, a);
        }

        if(link.side == EnumFacing.SOUTH) {
            Vec3d A = new Vec3d(pos.getX(), pos.getY(), pos.getZ()+1.0f);
            Vec3d B = A.add(1.0f, 0.0f, 0.0f);
            Vec3d C = B.add(0.0f, 1.0f, 0.0f);
            Vec3d D = C.add(-1.0f, 0.0f, 0.0f);

            renderLine(tessellator, new Line(A, B), r, g, b, a);
            renderLine(tessellator, new Line(B, C), r, g, b, a);
            renderLine(tessellator, new Line(C, D), r, g, b, a);
            renderLine(tessellator, new Line(D, A), r, g, b, a);
        }

        if(link.side == EnumFacing.WEST) {
            Vec3d A = new Vec3d(pos.getX(), pos.getY(), pos.getZ());
            Vec3d B = A.add(0.0f, 0.0f, 1.0f);
            Vec3d C = B.add(0.0f, 1.0f, 0.0f);
            Vec3d D = C.add(0.0f, 0.0f, -1.0f);

            renderLine(tessellator, new Line(A, B), r, g, b, a);
            renderLine(tessellator, new Line(B, C), r, g, b, a);
            renderLine(tessellator, new Line(C, D), r, g, b, a);
            renderLine(tessellator, new Line(D, A), r, g, b, a);
        }

        if(link.side == EnumFacing.EAST) {
            Vec3d A = new Vec3d(pos.getX()+1.0f, pos.getY(), pos.getZ());
            Vec3d B = A.add(0.0f, 0.0f, 1.0f);
            Vec3d C = B.add(0.0f, 1.0f, 0.0f);
            Vec3d D = C.add(0.0f, 0.0f, -1.0f);

            renderLine(tessellator, new Line(A, B), r, g, b, a);
            renderLine(tessellator, new Line(B, C), r, g, b, a);
            renderLine(tessellator, new Line(C, D), r, g, b, a);
            renderLine(tessellator, new Line(D, A), r, g, b, a);
        }

        renderTearDown();
    }

    private static void renderLine(Tessellator tessellator, Line line, int r, int g, int b, int alpha) {
        renderLine(tessellator, line, r, g, b, alpha, 1.5f);
    }

    private static void renderLine(Tessellator tessellator, Line line, int r, int g, int b, int alpha, float lineWidth) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.glLineWidth(lineWidth);
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);

        buffer.pos(line.start.x, line.start.y, line.start.z).color(r, g, b, alpha).endVertex();
        buffer.pos(line.end.x, line.end.y, line.end.z).color(r, g, b, alpha).endVertex();

        tessellator.draw();
        GlStateManager.glLineWidth(1.0F);
    }

    public static void renderSpline(CatmullRomSpline<Vector3> spline, EntityPlayer player, float partialTicks) {
        Tessellator tessellator = renderPullUp(player, partialTicks);

        int subdivisions = 100;
        float stepSize = 1.0f / subdivisions;
        for (int index = 0; index < subdivisions; index++) {
            float offset = stepSize * index;
            float nextOffset = stepSize * (index+1);

            Vector3 point = spline.valueAt(new Vector3(), offset);
            Vector3 next  = spline.valueAt(new Vector3(), nextOffset);

            renderLine(tessellator, new Line(point, next), 0, 255, 0, 128, 3.5f);
        }

        renderTearDown();
    }

    public static void renderPath(Path path, EntityPlayer player, float partialTicks) {
        Tessellator tessellator = renderPullUp(player, partialTicks);

        /*
        PathPoint pp = path.getFinalPathPoint();
        Vec3d center = new Vec3d(pp.x+0.5d, pp.y+0.5d, pp.z+0.5d);
        Vec3d dot = center.add(0.05d, 0.05d, 0.05d);
        renderLine(tessellator, new Line(center, dot), 0, 0, 255, 255);
        */

        for (int index = 0; index < path.getCurrentPathLength(); index++) {
            if(index == path.getCurrentPathLength() - 1) {
                break;
            }

            PathPoint point = path.getPathPointFromIndex(index);
            PathPoint next  = path.getPathPointFromIndex(index+1);

            renderLine(tessellator, new Line(point, next), 255, 128, 0, 128, 3.0f);
        }

        renderTearDown();
    }

    static class Line {
        public Vec3d start;
        public Vec3d end;

        public Line(PathPoint start, PathPoint end) {
            this(new Vec3d(start.x+0.5f, start.y+0.5f, start.z+0.5f), new Vec3d(end.x+0.5f, end.y+0.5f, end.z+0.5f));
        }

        public Line(Vec3d start, Vec3d end) {
            Vec3d origin = new Vec3d(0,0,0);
            if(start.distanceTo(origin) < end.distanceTo(origin)) {
                this.start = start;
                this.end = end;
            } else {
                this.end = start;
                this.start = end;
            }
        }

        public Line(Vector3 start, Vector3 end) {
            this(new Vec3d(start.x+0.5f, start.y+0.5f, start.z+0.5f), new Vec3d(end.x+0.5f, end.y+0.5f, end.z+0.5f));
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Line that = (Line) o;
            return Objects.equals(start, that.start) && Objects.equals(end, that.end);
        }

        @Override
        public int hashCode() {
            return Objects.hash(start, end);
        }

        @Override
        public String toString() {
            return this.getClass().getSimpleName() + "{" +
                    "start=" + start +
                    ", end=" + end +
                    '}';
        }
    }
}
