package org.dave.bats.func.batcage;

import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import org.dave.bats.base.BaseTileEntity;
import org.dave.bats.func.guano.GuanoItemData;
import org.dave.bats.util.FaceIdentifier;
import org.dave.bats.util.PathHelper;
import org.dave.bats.util.serialization.Store;
import org.mini2Dx.gdx.math.CatmullRomSpline;
import org.mini2Dx.gdx.math.Vector3;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class BatCageTileEntity extends BaseTileEntity {
    @Store(storeWithItem = true, sendInUpdatePackage = true)
    public Map<FaceIdentifier, BatCageLinkConfig> links;

    BatCageItemHandler handler;

    public Map<FaceIdentifier, Path> linkPaths;
    public Map<FaceIdentifier, CatmullRomSpline<Vector3>> linkSplines;

    public Map<FaceIdentifier, BatRenderData> linkRenderData;

    public BatCageTileEntity() {
        super();

        links = new HashMap<>();
        handler = new BatCageItemHandler(this);
        linkPaths = new HashMap<>();
        linkSplines = new HashMap<>();
        linkRenderData = new HashMap<>();
    }

    public BatRenderData getRenderData(FaceIdentifier id) {
        if(!linkRenderData.containsKey(id)) {
            linkRenderData.put(id, new BatRenderData());
        }
        return linkRenderData.get(id);
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return new AxisAlignedBB(this.getPos()).grow(1.0d).expand(16.0d, 16.0d, 16.0d);
    }

    public boolean canCreateNewLink() {
        return links.size() < 8;
    }

    @Nullable
    public BatCageLinkConfig getLink(@Nonnull BlockPos pos, @Nonnull EnumFacing side) {
        return links.get(new FaceIdentifier(pos, side));
    }

    private void removeLink(@Nonnull BlockPos pos, @Nonnull EnumFacing side) {
        FaceIdentifier id = new FaceIdentifier(pos, side);
        if(!links.containsKey(id) || links.get(id) == null) {
            return;
        }

        links.remove(id);

        this.markDirty();
        this.notifyClients();
    }

    /**
     * Overrides existing links!
     *
     * @param hitPos
     * @param hitSide
     * @param guanoItemData
     */
    public void createLink(@Nonnull BlockPos hitPos, @Nonnull EnumFacing hitSide, @Nullable GuanoItemData guanoItemData) {
        FaceIdentifier id = new FaceIdentifier(hitPos, hitSide);

        BatCageLinkConfig linkData = new BatCageLinkConfig(hitPos, hitSide);
        // TODO: Pre-Configure link based on the values stored in guanoItemData

        links.put(id, linkData);

        this.markDirty();
        this.notifyClients();
    }

    /**
     * Removes a link if it already exists, otherwise creates a new link.
     *
     * @param hitPos
     * @param hitSide
     * @param guanoItemData
     */
    public void toggleLink(@Nonnull BlockPos hitPos, @Nonnull EnumFacing hitSide, @Nullable GuanoItemData guanoItemData) {
        if(getLink(hitPos, hitSide) != null) {
            removeLink(hitPos, hitSide);
        } else {
            createLink(hitPos, hitSide, guanoItemData);
        }
    }

    public void foreachLink(Consumer<BatCageLinkConfig> consumer) {
        for(BatCageLinkConfig link : links.values()) {
            consumer.accept(link);
        }
    }

    @Override
    public void update() {
        super.update();
        handler.refreshLinkCache();
        this.updateLinkPaths();
    }

    public boolean hasValidPath(FaceIdentifier id) {
        Path path = linkPaths.get(id);
        if(path == null) {
            return false;
        }

        PathPoint endPoint = path.getPathPointFromIndex(path.getCurrentPathLength()-2);
        BlockPos endPos = new BlockPos(endPoint.x, endPoint.y, endPoint.z);

        return endPos.equals(id.pos.offset(id.face));
    }

    private void updateLinkPaths() {
        PathHelper ph = new PathHelper();

        // First check existing paths
        HashMap<FaceIdentifier, Path> newMap = new HashMap<>();
        HashMap<FaceIdentifier, CatmullRomSpline<Vector3>> newSplineMap = new HashMap<>();
        for(FaceIdentifier id : this.links.keySet()) {
            if(this.linkPaths.containsKey(id) && PathHelper.stillValid(world, this.linkPaths.get(id))) {
                // Old path to the link target is still valid, just reuse it
                newMap.put(id, this.linkPaths.get(id));
                newSplineMap.put(id, this.linkSplines.get(id));
            } else {
                // We either never had a path or it isn't fully traversable anymore -> get a new one
                Path path = ph.findPath(world, this.pos, id.pos.offset(id.face), 32.0f);
                if(path == null) {
                    continue;
                }

                path = PathHelper.extendPathByStartAndEnd(path, this.pos, id.pos);
                newMap.put(id, path);
                newSplineMap.put(id, PathHelper.catmull(path));
            }
        }

        this.linkSplines = newSplineMap;
        this.linkPaths = newMap;
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return true;
        }

        return super.hasCapability(capability, facing);
    }

    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(handler);
        }

        return super.getCapability(capability, facing);
    }
}
