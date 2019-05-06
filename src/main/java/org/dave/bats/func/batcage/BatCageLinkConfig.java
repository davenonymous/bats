package org.dave.bats.func.batcage;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import org.dave.bats.base.BaseNBTSerializable;
import org.dave.bats.util.FaceIdentifier;
import org.dave.bats.util.serialization.Store;

public class BatCageLinkConfig extends BaseNBTSerializable {
    @Store
    public BlockPos pos;

    @Store
    public EnumFacing side;

    @Store
    public int itemPriority;

    @Store
    public BatCageLinkConfigItemHandler itemImport;

    @Store
    public BatCageLinkConfigItemHandler itemExport;

    public BatCageLinkConfig() {
        this.itemExport = new BatCageLinkConfigItemHandler();
        this.itemImport = new BatCageLinkConfigItemHandler();
    }

    public BatCageLinkConfig(BlockPos pos, EnumFacing side) {
        this();
        this.pos = pos;
        this.side = side;
    }

    public FaceIdentifier getId() {
        return new FaceIdentifier(pos, side);
    }

    public boolean matches(BlockPos pos, EnumFacing side) {
        return this.pos.equals(pos) && side == this.side;
    }
}
