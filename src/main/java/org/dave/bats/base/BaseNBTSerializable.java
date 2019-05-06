package org.dave.bats.base;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;
import org.dave.bats.util.serialization.FieldUtils;
import org.dave.bats.util.serialization.NBTFieldSerializationData;

import java.util.List;

public class BaseNBTSerializable implements INBTSerializable<NBTTagCompound> {
    private List<NBTFieldSerializationData> NBTActions;
    private boolean isDirty = false;

    public BaseNBTSerializable() {
        this.NBTActions = FieldUtils.initSerializableStoreFields(this.getClass());
    }

    @Override
    public NBTTagCompound serializeNBT() {
        return FieldUtils.writeFieldsToNBT(NBTActions, this, new NBTTagCompound(), data -> true);
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        FieldUtils.readFieldsFromNBT(NBTActions, this, nbt, data -> true);
        afterLoad();
    }

    public void markDirty() {
        isDirty = true;
    }

    public boolean isDirty() {
        return isDirty;
    }

    public void afterLoad() {

    }
}
