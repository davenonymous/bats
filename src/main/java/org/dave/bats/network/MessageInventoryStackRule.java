package org.dave.bats.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import org.dave.bats.func.batcage.BatCageLinkConfig;
import org.dave.bats.func.batcage.BatCageTileEntity;
import org.dave.bats.func.rules.StackRule;
import org.dave.bats.util.FaceIdentifier;

public class MessageInventoryStackRule implements IMessage, IMessageHandler<MessageInventoryStackRule, MessageInventoryStackRule> {
    public int slot = -1;
    public StackRule stackRule;
    public FilterRuleAction action;
    public FaceIdentifier id;
    public BlockPos cagePos;
    public boolean isImport;

    public MessageInventoryStackRule() {
    }

    public MessageInventoryStackRule(int slot, StackRule stackRule, FilterRuleAction action, FaceIdentifier id, BlockPos cagePos, boolean isImport) {
        this.slot = slot;
        this.stackRule = stackRule;
        this.action = action;
        this.id = id;
        this.cagePos = cagePos;
        this.isImport = isImport;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.isImport = buf.readBoolean();
        this.slot = buf.readInt();
        this.cagePos = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
        this.action = FilterRuleAction.values()[buf.readInt()];
        this.stackRule = new StackRule(ByteBufUtils.readTag(buf));
        this.id = new FaceIdentifier(ByteBufUtils.readTag(buf));
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeBoolean(isImport);
        buf.writeInt(slot);
        buf.writeInt(cagePos.getX());
        buf.writeInt(cagePos.getY());
        buf.writeInt(cagePos.getZ());
        buf.writeInt(action.ordinal());
        ByteBufUtils.writeTag(buf, stackRule.serializeNBT());
        ByteBufUtils.writeTag(buf, id.serializeNBT());
    }

    @Override
    public MessageInventoryStackRule onMessage(MessageInventoryStackRule message, MessageContext ctx) {
        EntityPlayerMP serverPlayer = ctx.getServerHandler().player;
        serverPlayer.getServerWorld().addScheduledTask(() -> {
            TileEntity tile = serverPlayer.getServerWorld().getTileEntity(message.cagePos);
            if(!(tile instanceof BatCageTileEntity)) {
                return;
            }

            BatCageTileEntity cageTile = (BatCageTileEntity) tile;
            if(message.action == FilterRuleAction.ADD) {
                BatCageLinkConfig config = cageTile.links.get(message.id);
                if(message.isImport) {
                    config.itemImport.addRule(message.stackRule);
                } else {
                    config.itemExport.addRule(message.stackRule);
                }
                cageTile.markDirty();
                cageTile.notifyClients();
            }

            if(message.action == FilterRuleAction.DELETE) {
                BatCageLinkConfig config = cageTile.links.get(message.id);
                if(message.isImport) {
                    config.itemImport.rules.remove(message.slot);
                } else {
                    config.itemExport.rules.remove(message.slot);
                }
                cageTile.markDirty();
                cageTile.notifyClients();
            }

            if(message.action == FilterRuleAction.EDIT) {
                BatCageLinkConfig config = cageTile.links.get(message.id);
                if(message.isImport) {
                    config.itemImport.rules.set(message.slot, message.stackRule);
                } else {
                    config.itemExport.rules.set(message.slot, message.stackRule);
                }
                cageTile.markDirty();
                cageTile.notifyClients();
            }
        });

        return null;
    }

    public static enum FilterRuleAction {
        ADD,
        EDIT,
        DELETE
    }
}
