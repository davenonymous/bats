package org.dave.bats.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.items.ItemHandlerHelper;
import org.dave.bats.func.guano.GuanoItemData;
import org.dave.bats.init.Itemss;
import org.dave.bats.util.Logz;

public class MessageGetGuano implements IMessage, IMessageHandler<MessageGetGuano, MessageGetGuano> {
    BlockPos cagePos;

    public MessageGetGuano() {
    }

    public MessageGetGuano(BlockPos cagePos) {
        this.cagePos = cagePos;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.cagePos = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.cagePos.getX());
        buf.writeInt(this.cagePos.getY());
        buf.writeInt(this.cagePos.getZ());
    }

    @Override
    public MessageGetGuano onMessage(MessageGetGuano message, MessageContext ctx) {
        EntityPlayerMP serverPlayer = ctx.getServerHandler().player;
        serverPlayer.getServerWorld().addScheduledTask(() -> {
            // Step 1: Check if the player already has matching guano, then abort
            for (int slot = 0; slot < serverPlayer.inventory.getSizeInventory(); slot++) {
                ItemStack stack = serverPlayer.inventory.getStackInSlot(slot);
                if(stack.isEmpty() || stack.getItem() != Itemss.guano) {
                    continue;
                }

                // Found guano
                GuanoItemData guanoData = new GuanoItemData(stack);
                if(guanoData.getCagePosition() != null && guanoData.getCagePosition().equals(message.cagePos)) {
                    // Found guano matching the wanted position
                    return;
                }
            }

            // Step 2: Give the player some guano :)
            GuanoItemData guanoData = new GuanoItemData(message.cagePos);
            ItemHandlerHelper.giveItemToPlayer(serverPlayer, guanoData.createNewItemStack());
        });
        return null;
    }
}
