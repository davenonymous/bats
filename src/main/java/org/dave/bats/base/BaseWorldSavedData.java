package org.dave.bats.base;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.storage.WorldSavedData;
import org.dave.bats.util.serialization.FieldUtils;
import org.dave.bats.util.serialization.NBTFieldSerializationData;

import java.util.List;

public abstract class BaseWorldSavedData extends WorldSavedData {
    private List<NBTFieldSerializationData> NBTActions;

    public BaseWorldSavedData(String name) {
        super(name);
        this.NBTActions = FieldUtils.initSerializableStoreFields(this.getClass());
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        FieldUtils.readFieldsFromNBT(NBTActions, this, compound, data -> true);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        return FieldUtils.writeFieldsToNBT(NBTActions, this, compound, data -> true);
    }

    public abstract int getDimension();

    public void afterLoad() {
    }
}
