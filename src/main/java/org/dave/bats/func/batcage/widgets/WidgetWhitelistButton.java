package org.dave.bats.func.batcage.widgets;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.dave.bats.Bats;
import org.dave.bats.gui.framework.GUIHelper;
import org.dave.bats.gui.framework.widgets.WidgetSelectButton;

public class WidgetWhitelistButton extends WidgetSelectButton<Boolean> {
    protected static final ResourceLocation SPRITES = new ResourceLocation(Bats.MODID, "textures/gui/tabicons.png");

    public WidgetWhitelistButton() {
        super();
        this.setSize(15, 12);
        this.addChoice(true, false);
    }

    @Override
    public void draw(GuiScreen screen) {
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.translate(0.0f, 0.0f, 2.0f);

        // Draw the background
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        screen.mc.getTextureManager().bindTexture(SPRITES);
        int yOffset = getValue() ? 52 : 64;
        GUIHelper.drawStretchedTexture(0, 0, width , height, 136, yOffset, width, height);

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        GlStateManager.popMatrix();
    }

}
