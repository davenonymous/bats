package org.dave.bats.func.batcage;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Objects;
import java.util.function.ToIntFunction;

public class BatCageItemHandler implements IItemHandlerModifiable {
    BatCageTileEntity batCageTileEntity;

    ArrayList<RemoteSlotReference> slots;

    public BatCageItemHandler(BatCageTileEntity batCageTileEntity) {
        this.batCageTileEntity = batCageTileEntity;
        refreshLinkCache();
    }

    public void refreshLinkCache() {
        this.slots = new ArrayList<>();

        if(this.batCageTileEntity.links == null) {
            return;
        }

        World world = batCageTileEntity.getWorld();

        for(BatCageLinkConfig link : this.batCageTileEntity.links.values()) {
            if(!batCageTileEntity.hasValidPath(link.getId())) {
                continue;
            }

            TileEntity remoteTile = world.getTileEntity(link.pos);
            if(remoteTile == null) {
                continue;
            }

            if(!remoteTile.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, link.side)) {
                continue;
            }

            IItemHandler remoteCap = remoteTile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, link.side);
            if(!(remoteCap instanceof IItemHandlerModifiable)) {
                continue;
            }

            for(int slot = 0; slot < remoteCap.getSlots(); slot++) {
                slots.add(new RemoteSlotReference(link, (IItemHandlerModifiable)remoteCap, slot));
            }
        }

        slots.sort(comparingIntInverse(o -> o.link.itemPriority));
    }

    private static <T> Comparator<T> comparingIntInverse(ToIntFunction<? super T> keyExtractor) {
        Objects.requireNonNull(keyExtractor);
        return (Comparator<T> & Serializable)
                (c1, c2) -> Integer.compare(keyExtractor.applyAsInt(c2), keyExtractor.applyAsInt(c1));
    }


    @Override
    public int getSlots() {
        return this.slots.size();
    }

    @Nonnull
    @Override
    public ItemStack getStackInSlot(int slot) {
        RemoteSlotReference ref = slots.get(slot);
        return ref.handler.getStackInSlot(ref.slot);
    }

    @Nonnull
    @Override
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        RemoteSlotReference ref = slots.get(slot);

        boolean matched = ref.link.itemImport.matchesRule(stack);
        boolean isWhitelist = ref.link.itemImport.isWhitelist;

        boolean allowed = (matched && isWhitelist) || (!matched && !isWhitelist);
        if(allowed) {
            return ref.handler.insertItem(ref.slot, stack, simulate);
        }

        return stack;
    }

    @Nonnull
    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        RemoteSlotReference ref = slots.get(slot);

        ItemStack presentStack = ref.handler.getStackInSlot(ref.slot);
        boolean matched = ref.link.itemExport.matchesRule(presentStack);
        boolean isWhitelist = ref.link.itemExport.isWhitelist;

        boolean allowed = (matched && isWhitelist) || (!matched && !isWhitelist);
        if(allowed) {
            return ref.handler.extractItem(ref.slot, amount, simulate);
        } else {
            return ItemStack.EMPTY;
        }
    }

    @Override
    public int getSlotLimit(int slot) {
        RemoteSlotReference ref = slots.get(slot);
        return ref.handler.getSlotLimit(ref.slot);
    }

    @Override
    public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
        RemoteSlotReference ref = slots.get(slot);

        boolean matched = ref.link.itemImport.matchesRule(stack);
        boolean isWhitelist = ref.link.itemImport.isWhitelist;

        boolean allowed = (matched && isWhitelist) || (!matched && !isWhitelist);
        if(allowed) {
            return ref.handler.isItemValid(ref.slot, stack);
        }

        return false;
    }

    @Override
    public void setStackInSlot(int slot, @Nonnull ItemStack stack) {
        RemoteSlotReference ref = slots.get(slot);

        boolean matched = ref.link.itemImport.matchesRule(stack);
        boolean isWhitelist = ref.link.itemImport.isWhitelist;

        boolean allowed = (matched && isWhitelist) || (!matched && !isWhitelist);
        if(allowed) {
            ref.handler.setStackInSlot(ref.slot, stack);
        }

        return;
    }

    class RemoteSlotReference {
        BatCageLinkConfig link;
        IItemHandlerModifiable handler;
        int slot;

        public RemoteSlotReference(BatCageLinkConfig link, IItemHandlerModifiable handler, int slot) {
            this.link = link;
            this.handler = handler;
            this.slot = slot;
        }
    }
}
