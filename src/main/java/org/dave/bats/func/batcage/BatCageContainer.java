package org.dave.bats.func.batcage;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import org.dave.bats.gui.framework.WidgetSlot;
import org.dave.bats.util.FaceIdentifier;
import org.dave.bats.util.Logz;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BatCageContainer extends Container {
    World world;
    BlockPos pos;
    EntityPlayer player;
    Map<FaceIdentifier, BatCageLinkGuiData> links;

    public BatCageContainer(World world, BlockPos pos, EntityPlayer player) {
        this.world = world;
        this.pos = pos;
        this.player = player;

        this.addPlayerSlots(player.inventory);

        this.links = new HashMap<>();
        updateLinkInfo();
    }

    public void updateLinkInfo() {
        TileEntity tileEntity = world.getTileEntity(pos);
        if(!(tileEntity instanceof BatCageTileEntity)) {
            return;
        }

        BatCageTileEntity cageTile = (BatCageTileEntity) tileEntity;
        cageTile.foreachLink(linkConfig -> {
            TileEntity linkedTile = world.getTileEntity(linkConfig.pos);
            if(linkedTile == null) {
                return;
            }

            boolean isNew = true;
            BatCageLinkGuiData linkInfo;
            if(links.containsKey(linkConfig.getId())) {
                linkInfo = links.get(linkConfig.getId());
                isNew = false;
            } else {
                linkInfo = new BatCageLinkGuiData();
            }

            linkInfo.cagePos = pos;
            linkInfo.linkConfig = linkConfig;
            linkInfo.currentBlockState = world.getBlockState(linkConfig.pos);
            linkInfo.stack = linkInfo.currentBlockState.getBlock().getPickBlock(linkInfo.currentBlockState, null, world, linkConfig.pos, player);
            linkInfo.hasValidPath = cageTile.hasValidPath(linkConfig.getId());

            if(isNew) {
                linkInfo.supportsItems = false;
                if (linkedTile.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, linkConfig.side)) {
                    IItemHandler itemHandler = linkedTile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, linkConfig.side);
                    int x = 0;
                    int y = 0;
                    List<WidgetSlot> slots = new ArrayList<>();
                    for (int iSlot = 0; iSlot < itemHandler.getSlots(); iSlot++) {
                        WidgetSlot slot = new WidgetSlot(itemHandler, iSlot, 8 + x * 18, 20 + y * 18);
                        slots.add(slot);
                        this.addSlotToContainer(slot);

                        x++;
                        if (x == 9) {
                            x = 0;
                            y++;
                        }

                        if (y == 3) {
                            y = 0;
                        }
                    }

                    linkInfo.slots = slots;
                    linkInfo.supportsItems = true;
                }
            }

            if(isNew) {
                links.put(linkConfig.getId(), linkInfo);
            }
        });
    }

    private void addPlayerSlots(IInventory playerInventory) {
        int yOffset = 102;
        int xOffset = 8;

        // Slots for the main inventory
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                int x = col * 18 + xOffset;
                int y = row * 18 + yOffset;
                this.addSlotToContainer(new Slot(playerInventory, col + row * 9 + 9, x, y));
            }
        }

        // Slots for the hotbar
        for (int row = 0; row < 9; ++row) {
            int x = row * 18 + xOffset;
            int y = 58 + yOffset;
            this.addSlotToContainer(new Slot(playerInventory, row, x, y));
        }
    }

    // We are relying on the client to tell the server which slots are currently enabled,
    // see MessageEnabledSlots.
    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
        Slot slot = this.inventorySlots.get(index);
        if(slot == null || !slot.getHasStack()) {
            return ItemStack.EMPTY;
        }

        if(index >= 0 && index <= 35) {
            // Player slot
            int firstValidSlot = -1;
            int lastValidSlot = -1;
            int slotId = 0;
            for(Slot invSlot : this.inventorySlots) {
                if(invSlot instanceof WidgetSlot && invSlot.isEnabled() && invSlot.getStack().getCount() < invSlot.getStack().getMaxStackSize()) {
                    if(firstValidSlot == -1) {
                        firstValidSlot = slotId;
                    }

                    lastValidSlot = slotId;
                } else {
                    if(lastValidSlot != -1) {
                        break;
                    }
                }

                slotId++;
            }

            if(firstValidSlot == -1 || lastValidSlot == -1) {
                return ItemStack.EMPTY;
            }

            ItemStack clickedStack = slot.getStack();
            if(!this.mergeItemStack(clickedStack, firstValidSlot, lastValidSlot+1, false)) {
                return ItemStack.EMPTY;
            }

            slot.onSlotChanged();
            return clickedStack;
        } else if(index > 35) {
            // Inventory slot
            ItemStack clickedStack = slot.getStack();
            if(clickedStack.getCount() > clickedStack.getMaxStackSize()) {
                ItemStack shrinkedStack = clickedStack.copy();
                shrinkedStack.setCount(clickedStack.getMaxStackSize());

                if(!this.mergeItemStack(shrinkedStack, 0, 36, false)) {
                    return ItemStack.EMPTY;
                }

                ItemStack remainder = slot.getStack();
                remainder.setCount(remainder.getCount() - remainder.getMaxStackSize());

                if(!shrinkedStack.isEmpty()) {
                    remainder.setCount(remainder.getCount() + shrinkedStack.getCount());
                }
                slot.putStack(remainder);

                return ItemStack.EMPTY;
            } else {
                if(!this.mergeItemStack(clickedStack, 0, 35, false)) {
                    return ItemStack.EMPTY;
                }

                if(clickedStack.isEmpty()) {
                    slot.putStack(ItemStack.EMPTY);
                } else {
                    slot.onSlotChanged();
                }

                return clickedStack;
            }
        }

        return ItemStack.EMPTY;
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return true;
    }
}
