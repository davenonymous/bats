package org.dave.bats.base;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import org.dave.bats.util.serialization.ByteBufFieldSerializationData;
import org.dave.bats.util.serialization.FieldUtils;

import java.util.List;

public class BaseMessage implements IMessage {
    private List<ByteBufFieldSerializationData> ioActions;

    public BaseMessage() {
        this.ioActions = FieldUtils.initSerializableSyncFields(this.getClass());
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        FieldUtils.readFieldsFromByteBuf(ioActions, this, buf, data -> true);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        FieldUtils.writeFieldsToByteBuf(ioActions, this, buf, data -> true);
    }
}
