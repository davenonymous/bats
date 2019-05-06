package org.dave.bats.gui.framework.widgets;

import org.dave.bats.gui.framework.ISelectable;
import org.dave.bats.gui.framework.event.ListEntrySelectionEvent;

public class WidgetListEntry extends WidgetPanel implements ISelectable {
    boolean isSelected = false;

    @Override
    public boolean isSelected() {
        return this.isSelected;
    }

    @Override
    public void setSelected(boolean state) {
        this.isSelected = state;
        this.fireEvent(new ListEntrySelectionEvent(state));
    }
}
