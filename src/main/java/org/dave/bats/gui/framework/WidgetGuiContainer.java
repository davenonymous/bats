package org.dave.bats.gui.framework;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import org.dave.bats.gui.framework.event.*;
import org.lwjgl.input.Mouse;

import java.io.IOException;

public abstract class WidgetGuiContainer extends GuiContainer {
    protected GUI gui;

    private int previousMouseX = Integer.MAX_VALUE;
    private int previousMouseY = Integer.MAX_VALUE;
    public boolean dataUpdated = false;

    public WidgetGuiContainer(Container container) {
        super(container);
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        gui.fireEvent(new UpdateScreenEvent());
        this.resetMousePositions();
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();

        int wheelValue = Mouse.getEventDWheel();
        if(wheelValue != 0) {
            gui.fireEvent(new MouseScrollEvent(wheelValue));
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if(gui.fireEvent(new KeyTypedEvent(typedChar, keyCode)) == WidgetEventResult.CONTINUE_PROCESSING) {
            super.keyTyped(typedChar, keyCode);
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if(gui.fireEvent(new MouseClickEvent(mouseX, mouseY, mouseButton)) == WidgetEventResult.CONTINUE_PROCESSING) {
            super.mouseClicked(mouseX, mouseY, mouseButton);
        }
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        if(gui.fireEvent(new MouseClickMoveEvent(mouseX, mouseY, clickedMouseButton, timeSinceLastClick)) == WidgetEventResult.CONTINUE_PROCESSING) {
            super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if(dataUpdated) {
            dataUpdated = false;
            gui.fireEvent(new GuiDataUpdatedEvent());
        }

        this.drawDefaultBackground();

        if(mouseX != previousMouseX || mouseY != previousMouseY) {
            gui.fireEvent(new MouseMoveEvent(mouseX, mouseY));

            previousMouseX = mouseX;
            previousMouseY = mouseY;
        }

        RenderHelper.enableGUIStandardItemLighting();

        gui.drawGUI(this);
        GlStateManager.enableBlend();

        if(this.inventorySlots != null && this.inventorySlots.inventorySlots != null) {
            for(Slot slot : this.inventorySlots.inventorySlots) {
                if(!slot.isEnabled()) {
                    continue;
                }

                gui.drawSlot(this, slot, guiLeft, guiTop);
            }
        }

        super.drawScreen(mouseX, mouseY, partialTicks);

        gui.drawTooltips(this, mouseX, mouseY);
        renderHoveredToolTip(mouseX, mouseY);
        RenderHelper.disableStandardItemLighting();
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        //this.drawDefaultBackground();
    }

    protected void resetMousePositions() {
        this.previousMouseX = Integer.MIN_VALUE;
        this.previousMouseY = Integer.MIN_VALUE;
    }

    public void fireDataUpdateEvent() {
        dataUpdated = true;
    }
}
