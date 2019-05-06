package org.dave.bats.gui.framework.event;

public class VisibilityChangedEvent extends ValueChangedEvent<Boolean> {
    public VisibilityChangedEvent(Boolean oldValue, Boolean newValue) {
        super(oldValue, newValue);
    }
}
