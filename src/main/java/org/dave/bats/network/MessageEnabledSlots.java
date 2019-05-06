package org.dave.bats.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Slot;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import org.dave.bats.gui.BatCageSlot;

import java.util.List;

public class MessageEnabledSlots implements IMessage, IMessageHandler<MessageEnabledSlots, MessageEnabledSlots> {
    boolean[] enabledSlots;

    public MessageEnabledSlots() {
    }

    public MessageEnabledSlots(List<Slot> slots) {
        this.enabledSlots = new boolean[slots.size()];

        int index = 0;
        for(Slot slot : slots) {
            this.enabledSlots[index] = slot.isEnabled();
            index++;
        }
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        int count = buf.readInt();
        enabledSlots = new boolean[count];
        for (int i = 0; i < count; i++) {
            enabledSlots[i] = buf.readBoolean();
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(enabledSlots.length);
        for (int i = 0; i < enabledSlots.length; i++) {
            buf.writeBoolean(enabledSlots[i]);
        }
    }

    @Override
    public MessageEnabledSlots onMessage(MessageEnabledSlots message, MessageContext ctx) {
        EntityPlayerMP serverPlayer = ctx.getServerHandler().player;
        int index = 0;
        for(Slot slot : serverPlayer.openContainer.inventorySlots) {
            if(slot instanceof BatCageSlot) {
                if(index >= message.enabledSlots.length) {
                    break;
                }

                ((BatCageSlot) slot).setEnabled(message.enabledSlots[index]);
            }

            index++;
        }

        return null;
    }
}
