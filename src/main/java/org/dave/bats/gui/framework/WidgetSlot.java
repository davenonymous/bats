package org.dave.bats.gui.framework;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.SlotItemHandler;
import org.dave.bats.gui.framework.event.VisibilityChangedEvent;
import org.dave.bats.gui.framework.event.WidgetEventResult;
import org.dave.bats.gui.framework.widgets.Widget;

import javax.annotation.Nonnull;

public class WidgetSlot extends SlotItemHandler {
    private boolean enabled;

    public WidgetSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
        super(itemHandler, index, xPosition, yPosition);

        this.enabled = false;
    }

    public void bindToWidget(Widget widget) {
        widget.addListener(VisibilityChangedEvent.class, (event, widget1) -> {
            this.setEnabled(event.newValue && widget.areAllParentsVisible());
            return WidgetEventResult.CONTINUE_PROCESSING;
        });
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public WidgetSlot setEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    @Override
    public ItemStack onTake(EntityPlayer thePlayer, ItemStack stack) {
        if(stack.getCount() <= stack.getMaxStackSize()) {
            return super.onTake(thePlayer, stack);
        }

        int total = stack.getCount() + getStack().getCount();
        ItemStack before = stack.copy();
        ItemStack after = before.copy();
        after.setCount(total - before.getMaxStackSize());

        stack.setCount(before.getMaxStackSize());
        this.putStack(after);
        this.onSlotChanged();

        return stack;
    }

    @Override
    public boolean canTakeStack(EntityPlayer player) {
        if(player != null) {
            ItemStack mouseStack = player.inventory.getItemStack();
            if(mouseStack.isEmpty()) {
                return true;
            }

            if(getStack().getCount() > getStack().getMaxStackSize()) {
                return false;
            }
        }

        return true;
    }

    @Override
    public int getItemStackLimit(@Nonnull ItemStack stack) {
        return 64;
    }

    @Override
    public void putStack(@Nonnull ItemStack stack) {
        IItemHandler handler = this.getItemHandler();
        if(handler instanceof IItemHandlerModifiable) {
            super.putStack(stack);
        } else {
            if(!handler.getStackInSlot(getSlotIndex()).isEmpty()) {
                handler.extractItem(getSlotIndex(), handler.getStackInSlot(getSlotIndex()).getCount(), false);
            }

            handler.insertItem(getSlotIndex(), stack.copy(), false);
            this.onSlotChanged();
        }
    }
}
