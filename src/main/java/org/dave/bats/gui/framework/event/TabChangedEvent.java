package org.dave.bats.gui.framework.event;

import org.dave.bats.gui.framework.widgets.WidgetPanel;

public class TabChangedEvent extends ValueChangedEvent<WidgetPanel> {
    public TabChangedEvent(WidgetPanel oldValue, WidgetPanel newValue) {
        super(oldValue, newValue);
    }
}
