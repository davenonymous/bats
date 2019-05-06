package org.dave.bats.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import org.dave.bats.func.batcage.BatCageTileEntity;
import org.dave.bats.func.batcage.BatCageLinkConfig;
import org.dave.bats.util.FaceIdentifier;

public class MessageInventoryLinkConfig implements IMessage, IMessageHandler<MessageInventoryLinkConfig, MessageInventoryLinkConfig> {
    public ConfigAction action;
    public FaceIdentifier id;
    public BlockPos cagePos;

    public boolean isImport;
    public boolean whitelistValue;
    public int priority;

    public MessageInventoryLinkConfig() {
    }

    public MessageInventoryLinkConfig(ConfigAction action, FaceIdentifier id, BlockPos cagePos, boolean isImport, boolean whitelistValue, int priority) {
        this.action = action;
        this.id = id;
        this.cagePos = cagePos;
        this.isImport = isImport;
        this.whitelistValue = whitelistValue;
        this.priority = priority;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.isImport = buf.readBoolean();
        this.whitelistValue = buf.readBoolean();
        this.cagePos = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
        this.action = ConfigAction.values()[buf.readInt()];
        this.id = new FaceIdentifier(ByteBufUtils.readTag(buf));
        this.priority = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeBoolean(isImport);
        buf.writeBoolean(whitelistValue);
        buf.writeInt(cagePos.getX());
        buf.writeInt(cagePos.getY());
        buf.writeInt(cagePos.getZ());
        buf.writeInt(action.ordinal());
        ByteBufUtils.writeTag(buf, id.serializeNBT());
        buf.writeInt(priority);
    }

    @Override
    public MessageInventoryLinkConfig onMessage(MessageInventoryLinkConfig message, MessageContext ctx) {
        EntityPlayerMP serverPlayer = ctx.getServerHandler().player;
        serverPlayer.getServerWorld().addScheduledTask(() -> {
            TileEntity tile = serverPlayer.getServerWorld().getTileEntity(message.cagePos);
            if(!(tile instanceof BatCageTileEntity)) {
                return;
            }

            BatCageTileEntity cageTile = (BatCageTileEntity) tile;
            if(message.action == ConfigAction.SET_WHITELIST) {
                BatCageLinkConfig config = cageTile.links.get(message.id);
                if(message.isImport) {
                    config.itemImport.isWhitelist = message.whitelistValue;
                } else {
                    config.itemExport.isWhitelist = message.whitelistValue;
                }
                cageTile.markDirty();
                cageTile.notifyClients();
            }

            if(message.action == ConfigAction.SET_PRIORITY) {
                BatCageLinkConfig config = cageTile.links.get(message.id);
                config.itemPriority = message.priority;
                cageTile.markDirty();
                cageTile.notifyClients();
            }

        });

        return null;
    }

    public static enum ConfigAction {
        SET_WHITELIST,
        SET_PRIORITY
    }
}
