package org.dave.bats.util;

import com.google.common.base.Objects;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import org.dave.bats.base.BaseNBTSerializable;
import org.dave.bats.util.serialization.Store;

public class FaceIdentifier extends BaseNBTSerializable {
    @Store
    public BlockPos pos;

    @Store
    public EnumFacing face;

    public FaceIdentifier() {
    }

    public FaceIdentifier(NBTTagCompound tag) {
        this();
        this.deserializeNBT(tag);
    }

    public FaceIdentifier(BlockPos pos, EnumFacing face) {
        this();
        this.pos = pos;
        this.face = face;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FaceIdentifier that = (FaceIdentifier) o;
        return Objects.equal(pos, that.pos) &&
                face == that.face;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(pos, face);
    }
}
