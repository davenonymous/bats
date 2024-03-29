package org.dave.bats.gui.framework.widgets;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import org.dave.bats.gui.framework.GUI;
import org.dave.bats.gui.framework.event.*;

import java.util.*;

public class Widget {
    public int x;
    public int y;
    public int width;
    public int height;
    public String id;

    public int debugColor = 0x00000000;

    boolean enabled = true;
    boolean focused = false;
    boolean visible = true;
    boolean hovered = false;
    Widget parent;

    List<String> tooltipLines = new ArrayList<>();

    Map<Class<? extends IEvent>, List<IWidgetListener>> eventListeners = new HashMap<>();
    List<IWidgetListener> anyEventListener = new ArrayList<>();

    public Widget() {
        this.addListener(MouseClickEvent.class, (event, widget) -> {
            widget.getRootWidget().fireEvent(new FocusChangedEvent());

            if(widget.focusable()) {
                widget.focused = true;
            }

            return WidgetEventResult.CONTINUE_PROCESSING;
        });

        this.addListener(FocusChangedEvent.class, ((event, widget) -> {
            widget.focused = false;
            return WidgetEventResult.CONTINUE_PROCESSING;
        }));

        this.addListener(MouseEnterEvent.class, (event, widget) -> {widget.hovered = true; return WidgetEventResult.CONTINUE_PROCESSING; });
        this.addListener(MouseExitEvent.class, (event, widget) -> {widget.hovered = false; return WidgetEventResult.CONTINUE_PROCESSING; });
    }

    public void setPosition(int x, int y) {
        this.setX(x);
        this.setY(y);
    }

    public void setSize(int width, int height) {
        this.setWidth(width);
        this.setHeight(height);
    }

    public void setDimensions(int x, int y, int width, int height) {
        this.setSize(width, height);
        this.setPosition(x, y);
    }

    public boolean hasToolTip() {
        return tooltipLines != null && tooltipLines.size() > 0;
    }

    public List<String> getTooltip() {
        if(tooltipLines == null) {
            return Collections.emptyList();
        }

        return tooltipLines;
    }

    public Widget setTooltipLines(List<String> tooltipLines) {
        this.tooltipLines = tooltipLines;
        return this;
    }

    public Widget setTooltipLines(String ... tooltipLines) {
        this.tooltipLines = new ArrayList<>();
        for(String line : tooltipLines) {
            this.tooltipLines.add(line);
        }
        return this;
    }

    public Widget addTooltipLine(String ... tooltipLines) {
        for(String line : tooltipLines) {
            this.tooltipLines.add(line);
        }
        return this;
    }

    public Widget addTooltipLine(List<String> strings) {
        this.tooltipLines.addAll(strings);
        return this;
    }


    public boolean focusable() {
        return true;
    }

    public static int computeGuiScale(Minecraft mc) {
        int scaleFactor = 1;

        int k = mc.gameSettings.guiScale;

        if (k == 0) {
            k = 1000;
        }

        while (scaleFactor < k && mc.displayWidth / (scaleFactor + 1) >= 320 && mc.displayHeight / (scaleFactor + 1) >= 240) {
            ++scaleFactor;
        }
        return scaleFactor;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        if(id != null) {
            return id;
        }

        return this.getClass().getSimpleName() + "[?]";
    }

    public boolean isPosInside(int x, int y) {
        boolean isInsideX = this.getActualX() <= x && x < this.getActualX() + this.width;
        //Logz.info("[%s] insideX: %d < %d <= %d --> %s", this.id, this.lastDrawX, x, this.lastDrawX + this.lastDrawWidth, this.toString());

        boolean isInsideY = this.getActualY() <= y && y < this.getActualY() + this.height;
        //Logz.info("[%s] insideY: %d < %d <= %d --> %s", this.id, this.lastDrawY, y, this.lastDrawY + this.lastDrawHeight, this.toString());

        return isInsideX && isInsideY;
    }

    public int getActualX() {
        int result = this.x;
        Widget parent = this.parent;
        while(parent != null) {
            result += parent.x;
            parent = parent.parent;
        }

        return result;
    }

    public int getActualY() {
        int result = this.y;
        Widget parent = this.parent;
        while(parent != null) {
            result += parent.y;
            parent = parent.parent;
        }

        return result;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setVisible(boolean visible) {
        boolean oldVisible = this.visible;
        this.visible = visible;
        this.fireEvent(new VisibilityChangedEvent(oldVisible, visible));
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setDisabled() {
        this.enabled = false;
    }

    public void setParent(Widget parent) {
        this.parent = parent;
    }

    public void setFocused(boolean focused) {
        this.focused = focused;
    }

    public Widget getRootWidget() {
        if(this.parent == null) {
            return this;
        }

        return this.parent.getRootWidget();
    }

    public GUI getGUI() {
        Widget root = getRootWidget();
        if(root instanceof GUI) {
            return (GUI) root;
        }

        return null;
    }

    public boolean areAllParentsVisible() {
        return isVisible() && (parent == null || parent.areAllParentsVisible());
    }

    /**
     * Use this in your GuiScreens drawScreen() method and pass it as parameter.
     * This draws the Gui on the screen.
     *
     * Do not override this. Override the draw() method instead.
     *
     * @param screen
     */
    public void shiftAndDraw(GuiScreen screen) {
        this.drawBeforeShift(screen);
        GlStateManager.pushMatrix();
        GlStateManager.translate(this.x, this.y, 0);
        this.draw(screen);
        GlStateManager.popMatrix();
    }

    /**
     * Override this or draw() to implement your own drawing logic.
     *
     * The GLState is not shifted to this widgets x and y coordinates when
     * overriding this method.
     * @param screen
     */
    public void drawBeforeShift(GuiScreen screen) {

    }

    /**
     * Override this or drawBeforeShift() to implement your own drawing logic.
     *
     * The GLState is already positioned at the correct coordinates, i.e. your
     * x and y coordinates start at 0.
     *
     * @param screen
     */
    public void draw(GuiScreen screen) {
        if(getGUI() != null && getGUI().drawDebugFrames && this.debugColor != 0) {
            Gui.drawRect(0, 0, width, height, 0x20000000);
            Gui.drawRect(1, 1, width-1, height-1, debugColor);
        }
        //Logz.debug("Drawing widget: %s, x=%d, y=%d, width=%d, height=%d", this, layoutResult.getX(), layoutResult.getY(), layoutResult.getWidth(), layoutResult.getHeight());
    }

    public <T extends IEvent> void addListener(Class<T> eventClass, IWidgetListener<? super T> listener) {
        if(!eventListeners.containsKey(eventClass)) {
            eventListeners.put(eventClass, new ArrayList<>());
        }

        eventListeners.get(eventClass).add(listener);
    }

    public void addAnyListener(IWidgetListener listener) {
        anyEventListener.add(listener);
    }

    public <T extends IEvent> void addChildListener(Class<T> eventClass, Widget receiveEventsFromWidget) {
        Widget self = this;
        receiveEventsFromWidget.addListener(eventClass, (event, widget) -> self.fireEvent(event));
    }

    public WidgetEventResult fireEvent(IEvent event) {
        if(event instanceof GuiDataUpdatedEvent && eventListeners.containsKey(event.getClass())) {
            for(IWidgetListener listener : eventListeners.get(event.getClass())) {
                WidgetEventResult immediateResult = listener.call(event, this);
                if(immediateResult == WidgetEventResult.HANDLED) {
                    return WidgetEventResult.HANDLED;
                }
            }
        }

        for(IWidgetListener listener : anyEventListener) {
            WidgetEventResult immediateResult = listener.call(event, this);
            if(immediateResult == WidgetEventResult.HANDLED) {
                return WidgetEventResult.HANDLED;
            }
        }

        if(!eventListeners.containsKey(event.getClass())) {
            return WidgetEventResult.CONTINUE_PROCESSING;
        }

        for(IWidgetListener listener : eventListeners.get(event.getClass())) {
            WidgetEventResult immediateResult = listener.call(event, this);
            if(immediateResult == WidgetEventResult.HANDLED) {
                return WidgetEventResult.HANDLED;
            }
        }

        return WidgetEventResult.CONTINUE_PROCESSING;
    }

    public boolean isVisible() {
        return this.visible;
    }
}
