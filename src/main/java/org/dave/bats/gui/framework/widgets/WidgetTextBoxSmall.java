package org.dave.bats.gui.framework.widgets;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;

public class WidgetTextBoxSmall extends WidgetTextBox {
    public WidgetTextBoxSmall(String text) {
        super(text);
    }

    public WidgetTextBoxSmall(String text, int textColor) {
        super(text, textColor);
    }

    @Override
    public void draw(GuiScreen screen) {
        GlStateManager.pushMatrix();
        GlStateManager.scale(0.5f, 0.5f, 1.0f);
        super.draw(screen);
        GlStateManager.popMatrix();
    }
}
