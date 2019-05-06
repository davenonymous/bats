package org.dave.bats.func.batcage.widgets;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.dave.bats.Bats;
import org.dave.bats.gui.framework.GUIHelper;
import org.dave.bats.gui.framework.widgets.WidgetSelectButton;

public class WidgetRuleListEntryModeButton extends WidgetSelectButton<Boolean> {
    protected static final ResourceLocation SPRITES = new ResourceLocation(Bats.MODID, "textures/gui/tabicons.png");
    private int yOffset;

    public WidgetRuleListEntryModeButton(int width, int height, int texOffset) {
        super();
        this.yOffset = texOffset;
        this.setSize(width, height);
        this.addChoice(true, false);
    }

    @Override
    public void draw(GuiScreen screen) {
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.translate(0.0f, 0.0f, 2.0f);

        // Draw the background
        GlStateManager.color(1.0F, 1.0F, 1.0F, this.getValue() ? 0.8F : 0.3F);

        screen.mc.getTextureManager().bindTexture(SPRITES);
        GUIHelper.drawStretchedTexture(0, 0, width , height, 136, yOffset, width*2, height*2);

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        GlStateManager.popMatrix();
    }
}
