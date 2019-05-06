package org.dave.bats.base;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraftforge.fml.common.FMLCommonHandler;
import org.dave.bats.gui.framework.WidgetGuiContainer;
import org.dave.bats.init.Blockss;
import org.dave.bats.util.serialization.FieldUtils;
import org.dave.bats.util.serialization.NBTFieldSerializationData;
import org.dave.bats.util.serialization.Store;

import java.util.List;
import java.util.UUID;

public class BaseTileEntity extends TileEntity implements ITickable {
    private boolean initialized = false;

    @Store(storeWithItem = true, sendInUpdatePackage = true)
    protected String customName;

    @Store(storeWithItem = true, sendInUpdatePackage = true)
    protected UUID owner;

    @Store(sendInUpdatePackage = true)
    private int incomingRedstonePower = 0;

    //private HashMap<Field, Pair<FieldHandlers.Reader, FieldHandlers.Writer>> IOActions;
    private List<NBTFieldSerializationData> NBTActions;

    public BaseTileEntity() {
        //this.IOActions = FieldUtils.initSerializableSyncFields(this.getClass());
        this.NBTActions = FieldUtils.initSerializableStoreFields(this.getClass());
    }

    public void loadFromItem(ItemStack stack) {
        if(!stack.hasTagCompound()) {
            return;
        }

        FieldUtils.readFieldsFromNBT(NBTActions, this, stack.getTagCompound(), data -> data.storeWithItem);
        this.markDirty();
    }

    public ItemStack createItem() {
        ItemStack result = new ItemStack(Blockss.batCage, 1, this.getBlockMetadata());
        NBTTagCompound compound = createItemStackTagCompound();
        result.setTagCompound(compound);

        return result;
    }

    protected NBTTagCompound createItemStackTagCompound() {
        return FieldUtils.writeFieldsToNBT(NBTActions, this, new NBTTagCompound(), data -> data.storeWithItem);
    }

    public void spawnItemInWorld() {
        ItemStack stack = createItem();

        EntityItem entityItem = new EntityItem(world, getPos().getX()+0.5f, getPos().getY(), getPos().getZ()+0.5f, stack);
        entityItem.lifespan = 600;
        entityItem.setPickupDelay(5);

        entityItem.motionX = 0.0f;
        entityItem.motionY = 0.10f;
        entityItem.motionZ = 0.0f;

        world.spawnEntity(entityItem);
    }

    public void notifyClients() {
        world.notifyBlockUpdate(this.pos, world.getBlockState(pos), world.getBlockState(pos), 3);
    }

    public boolean renderUpdateOnDataPacket() {
        return false;
    }

    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(pos, 1, getUpdateTag());
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        return FieldUtils.writeFieldsToNBT(NBTActions, this, super.getUpdateTag(), data -> data.sendInUpdatePackage);
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
        FieldUtils.readFieldsFromNBT(NBTActions, this, packet.getNbtCompound(), data -> data.sendInUpdatePackage);

        // TODO: This should not be generalized in this way as it triggers on changes to blocks not belonging to this gui.
        if(world.isRemote && Minecraft.getMinecraft().currentScreen instanceof WidgetGuiContainer) {
            ((WidgetGuiContainer) Minecraft.getMinecraft().currentScreen).fireDataUpdateEvent();
        }

        if(renderUpdateOnDataPacket()) {
            world.markBlockRangeForRenderUpdate(pos, pos);
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound = super.writeToNBT(compound);
        compound = FieldUtils.writeFieldsToNBT(NBTActions, this, compound, data -> true);

        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);

        FieldUtils.readFieldsFromNBT(NBTActions, this, compound, data -> true);
    }

    @Override
    public void update() {
        if (!this.getWorld().isRemote && !this.initialized && !this.isInvalid()) {
            initialize();
            this.initialized = true;
        }
    }

    /**
     * Called when the block stops receiving a redstone signal.
     */
    public void redstonePulse() {

    }

    public void redstoneChanged(int previous, int now) {

    }

    protected void initialize() {
    }


    public int getIncomingRedstonePower() {
        return incomingRedstonePower;
    }

    public BaseTileEntity setIncomingRedstonePower(int incomingRedstonePower) {
        this.incomingRedstonePower = incomingRedstonePower;
        return this;
    }

    public boolean hasCustomName() {
        return customName != null && customName.length() > 0;
    }

    public String getCustomName() {
        return customName;
    }

    public void setCustomName(String customName) {
        this.customName = customName;
    }

    public UUID getOwner() {
        return owner;
    }

    public String getOwnerName() {
        return FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerProfileCache().getProfileByUUID(getOwner()).getName();
    }

    public boolean hasOwner() {
        return owner != null;
    }

    public void setOwner(UUID owner) {
        this.owner = owner;
    }

    public void setOwner(EntityPlayer player) {
        if(player == null) {
            return;
        }

        setOwner(player.getUniqueID());
    }
}
