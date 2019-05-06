package org.dave.bats.gui.framework.event;


import org.dave.bats.gui.framework.widgets.Widget;

public interface IWidgetListener<T extends IEvent> {
    WidgetEventResult call(T event, Widget widget);
}
