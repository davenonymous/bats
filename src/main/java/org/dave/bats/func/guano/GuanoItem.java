package org.dave.bats.func.guano;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import org.dave.bats.base.BaseItem;
import org.dave.bats.func.batcage.BatCageLinkConfig;
import org.dave.bats.func.batcage.BatCageTileEntity;
import org.dave.bats.util.FaceIdentifier;
import org.dave.bats.util.LineRenderer;
import org.mini2Dx.gdx.math.CatmullRomSpline;
import org.mini2Dx.gdx.math.Vector3;

import javax.annotation.Nullable;
import java.util.List;

public class GuanoItem extends BaseItem {
    public GuanoItem() {
        super("guano");
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        tooltip.add(I18n.format("item.bats.guano.tooltip"));

        GuanoItemData guanoItemData = new GuanoItemData(stack);

        BlockPos cagePos = guanoItemData.getCagePosition();
        if(cagePos != null) {
            tooltip.add(TextFormatting.YELLOW + I18n.format("item.bats.guano.tooltip_pos", cagePos.getX(), cagePos.getY(), cagePos.getZ()));
        }
    }

    @Override
    public void renderEffectOnHeldItem(EntityPlayer player, EnumHand mainHand, float partialTicks) {
        GuanoItemData itemData = new GuanoItemData(player.getHeldItem(mainHand));
        if(!itemData.hasBatCagePosition()) {
            return;
        }

        BatCageTileEntity cageTile = itemData.getBatCageTile(player.world);
        if(cageTile == null) {
            return;
        }

        //LineRenderer.renderTile(cageTile, player, partialTicks);
        for(FaceIdentifier linkId : cageTile.links.keySet()) {
            BatCageLinkConfig linkConfig = cageTile.links.get(linkId);

            boolean hasValidPath = cageTile.hasValidPath(linkId);
            int targetColor = hasValidPath ? 0xFF009000 : 0xFFFF0000;

            // Render target
            LineRenderer.renderTargetFace(linkConfig, player, partialTicks, targetColor);

            if(!hasValidPath) {
                continue;
            }

            // Render raw path
            Path path = cageTile.linkPaths.getOrDefault(linkId, null);
            if(path == null) {
                continue;
            }
            //LineRenderer.renderPath(path, player, partialTicks);

            // Render matching spline
            CatmullRomSpline<Vector3> spline = cageTile.linkSplines.getOrDefault(linkId, null);
            if(spline == null) {
                continue;
            }
            LineRenderer.renderSpline(cageTile.linkSplines.get(linkId), player, partialTicks);
        }
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (!world.isRemote) {
            if(player.isSneaking()) {
                GuanoItemData itemData = new GuanoItemData(stack);
                if(itemData == null) {
                    player.sendStatusMessage(new TextComponentTranslation("msg.bats.invalid_guano"), true);
                    return new ActionResult<>(EnumActionResult.FAIL, stack);
                }

                BatCageTileEntity cageTile = itemData.getBatCageTile(world);
                if(cageTile == null) {
                    player.sendStatusMessage(new TextComponentTranslation("msg.bats.core_not_found"), true);
                    return new ActionResult<>(EnumActionResult.FAIL, stack);
                }

                RayTraceResult traceResult = tracePlayer(player);
                if(traceResult == null || traceResult.getBlockPos() == null) {
                    return new ActionResult<>(EnumActionResult.PASS, stack);
                }

                BlockPos hitPos = traceResult.getBlockPos();
                EnumFacing hitSide = traceResult.sideHit;

                if(cageTile.getLink(hitPos, hitSide) == null) {
                    if(!cageTile.canCreateNewLink()) {
                        player.sendStatusMessage(new TextComponentTranslation("msg.bats.no_more_free_links"), true);
                        return new ActionResult<>(EnumActionResult.FAIL, stack);
                    }

                    player.sendStatusMessage(new TextComponentTranslation("msg.bats.created_link"), true);
                } else {
                    player.sendStatusMessage(new TextComponentTranslation("msg.bats.removed_link"), true);
                }

                cageTile.toggleLink(hitPos, hitSide, itemData);

            } else {
                // TODO: Implement guano GUI to be able to preconfigure new links
                //player.openGui(Bats.instance, GuiHandler.GuiIDs.GUANO.ordinal(), world, hand.ordinal(), 0, 0);
            }

            return new ActionResult<>(EnumActionResult.SUCCESS, stack);
        }


        return new ActionResult<>(EnumActionResult.PASS, stack);
    }

    public static RayTraceResult tracePlayer(EntityPlayer player) {
        double blockReachDistance = 10.0f;
        Vec3d vec3d = player.getPositionEyes(0.0f);
        Vec3d vec3d1 = player.getLook(0.0f);
        Vec3d vec3d2 = vec3d.add(vec3d1.x * blockReachDistance, vec3d1.y * blockReachDistance, vec3d1.z * blockReachDistance);
        return player.world.rayTraceBlocks(vec3d, vec3d2, false, false, false);
    }
}
