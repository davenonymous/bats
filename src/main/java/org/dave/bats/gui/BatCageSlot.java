package org.dave.bats.gui;

import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import org.dave.bats.gui.framework.event.VisibilityChangedEvent;
import org.dave.bats.gui.framework.event.WidgetEventResult;
import org.dave.bats.gui.framework.widgets.Widget;

public class BatCageSlot extends SlotItemHandler {
    private boolean enabled;

    public BatCageSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
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

    public BatCageSlot setEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }
}
