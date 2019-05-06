package org.dave.bats.func.batcage;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import org.dave.bats.util.FaceIdentifier;
import org.mini2Dx.gdx.math.CatmullRomSpline;
import org.mini2Dx.gdx.math.Vector3;

public class BatCageTESR extends TileEntitySpecialRenderer<BatCageTileEntity> {
    BatModel batModel = new BatModel();
    ResourceLocation batTexture = new ResourceLocation("textures/entity/bat.png");

    private static void renderHanging(BatCageTileEntity te, float partialTicks, int index) {

    }

    @Override
    public void render(BatCageTileEntity te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        GlStateManager.pushMatrix();

        GlStateManager.translate(x, y, z);
        GlStateManager.disableRescaleNormal();

        GlStateManager.enableLighting();
        GlStateManager.enableDepth();
        GlStateManager.enableBlend();

        bindTexture(batTexture);
        int index = 0;
        for (FaceIdentifier identifier : te.links.keySet()) {
            BatRenderData renderData = te.getRenderData(identifier);

            if(!te.hasValidPath(identifier)) {
                renderHanging(te, partialTicks, index);
                index++;
                continue;
            }

            if(!renderData.flying) {
                if(te.getWorld().rand.nextFloat() < 0.002f) {
                    renderData.flying = true;
                    renderData.position = 0.0f;
                    renderData.born = te.getWorld().getTotalWorldTime();
                } else {
                    renderHanging(te, partialTicks, index);
                    index++;
                    continue;
                }
            }

            CatmullRomSpline<Vector3> spline = te.linkSplines.getOrDefault(identifier, null);
            if(spline == null) {
                continue;
            }

            double age = (double)(te.getWorld().getTotalWorldTime() - renderData.born) + partialTicks;

            long travelTime = te.linkPaths.get(identifier).getCurrentPathLength() * 10 * 2;
            double progress = age % travelTime;

            double progressPercent = progress / (double)travelTime;

            boolean wayBack = false;
            if(progressPercent > 0.5d) {
                progressPercent = 1.0d - progressPercent;
                wayBack = true;
            }
            progressPercent *= 2;

            Vector3 pos = new Vector3();
            spline.valueAt(pos, (float)progressPercent);


            GlStateManager.pushMatrix();
            GlStateManager.translate(pos.x - te.getPos().getX() + 0.5f, pos.y - te.getPos().getY() + 0.5f, pos.z - te.getPos().getZ() + 0.5f);
            GlStateManager.translate(0.0F, MathHelper.cos((float)age * 0.3F) * 0.1F, 0.0F);
            GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);

            Vector3 nextPos = new Vector3();
            spline.valueAt(nextPos, (float)progressPercent + 0.01f);

            double angle = Math.atan2(pos.z - nextPos.z, pos.x - nextPos.x);
            angle = Math.toDegrees(angle) + 90.0f;

            if(Math.abs(pos.z-nextPos.z) > 0.02f || Math.abs(pos.x-nextPos.x) > 0.02f) {
                GlStateManager.rotate((float) angle, 0.0f, 1.0f, 0);
            }

            if(wayBack) {
                GlStateManager.rotate(180.0f, 0.0f, 1.0f, 0);
            }

            GlStateManager.scale(0.2f, 0.2f, 0.2f);
            GlStateManager.disableCull();
            batModel.render(0.0f, 0.0f, (float) age, 0.0f, 0.0f, 0.06f);
            GlStateManager.enableCull();

            GlStateManager.popMatrix();

            if(wayBack && progressPercent <= 0.01f) {
                renderData.flying = false;
            }

            index++;
        }

        GlStateManager.popMatrix();
    }
}
