package org.dave.bats.gui.framework.widgets;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import org.dave.bats.gui.framework.event.MouseClickEvent;
import org.dave.bats.gui.framework.event.MouseScrollEvent;
import org.dave.bats.gui.framework.event.WidgetEventResult;

public class WidgetNumberSelect extends WidgetWithChoiceValue<Integer> {

    public WidgetNumberSelect() {
        super();
        this.choices.wrap = false;

        this.addListener(MouseClickEvent.class, (event, widget) -> {
            int clickX = event.x - this.getActualX();
            if(clickX < 6) {
                this.prev();
            }
            if(clickX > this.width - 7) {
                this.next();
            }

            return WidgetEventResult.HANDLED;
        });

        this.addListener(MouseScrollEvent.class, (event, widget) -> {
            if(event.up) {
                this.next();
            } else {
                this.prev();
            }

            return WidgetEventResult.HANDLED;
        });
    }

    @Override
    public void draw(GuiScreen screen) {
        int backgroundColor = 0xFF333333;
        int borderColor = 0xFF000000;

        Gui.drawRect(0, 0, width, height, borderColor);
        Gui.drawRect(1, 1, width-1, height-1, backgroundColor);

        GlStateManager.pushMatrix();

        int fontHeight = 8;
        float yOffset = (height-fontHeight)/2.0f;
        screen.mc.fontRenderer.drawString("<", 1, yOffset, 0xDDDDDD, false);

        String content = getValue().toString();
        float xOffset = (width - screen.mc.fontRenderer.getStringWidth(content)) / 2.0f;
        screen.mc.fontRenderer.drawString(content, xOffset, yOffset, 0xEEEEEE, false);

        screen.mc.fontRenderer.drawString(">", width-5, yOffset, 0xDDDDDD, false);

        GlStateManager.popMatrix();
    }
}
