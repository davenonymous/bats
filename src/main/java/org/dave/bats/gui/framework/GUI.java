package org.dave.bats.gui.framework;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.inventory.Slot;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.config.GuiUtils;
import org.dave.bats.Bats;
import org.dave.bats.gui.framework.widgets.Widget;
import org.dave.bats.gui.framework.widgets.WidgetPanel;

import java.util.ArrayList;
import java.util.List;


public class GUI extends WidgetPanel {
    private static ResourceLocation tabIcons;

    public boolean hasTabs = false;
    public boolean drawDebugFrames = false;

    public GUI(int x, int y, int width, int height) {
        this.setX(x);
        this.setY(y);
        this.setWidth(width);
        this.setHeight(height);

        this.tabIcons = new ResourceLocation(Bats.MODID, "textures/gui/tabicons.png");
    }

    public void drawGUI(GuiScreen screen) {
        this.setX((screen.width - this.width)/2);
        this.setY((screen.height - this.height)/2);

        if(drawDebugFrames) {
            this.drawDebugInfo(screen, 0, 0);
        }

        this.shiftAndDraw(screen);
    }

    @Override
    public void drawBeforeShift(GuiScreen screen) {
        //screen.drawDefaultBackground();

        super.drawBeforeShift(screen);
    }

    @Override
    public void draw(GuiScreen screen) {
        drawWindow(screen);
        super.draw(screen);
    }

    protected void drawWindow(GuiScreen screen) {
        GlStateManager.disableLighting();

        GlStateManager.color(1f, 1f, 1f, 1f);
        screen.mc.renderEngine.bindTexture(tabIcons);

        float offsetX = 0.0f;
        float offsetY = 0.0f;

        GlStateManager.pushMatrix();
        GlStateManager.translate(offsetX, offsetY, 0);

        int texOffsetY = 11;
        int texOffsetX = 64;

        int width = this.width;
        int xOffset = 0;

        if(hasTabs) {
            width -= 32;
            xOffset += 32;
        }

        // Top Left corner
        screen.drawTexturedModalRect(xOffset, 0, texOffsetX, texOffsetY, 4, 4);

        // Top right corner
        screen.drawTexturedModalRect(xOffset+width - 4, 0, texOffsetX + 4 + 64, texOffsetY, 4, 4);

        // Bottom Left corner
        screen.drawTexturedModalRect(xOffset, this.height - 4, texOffsetX, texOffsetY + 4 + 64, 4, 4);

        // Bottom Right corner
        screen.drawTexturedModalRect(xOffset+width - 4, this.height - 4, texOffsetX + 4 + 64, texOffsetY + 4 + 64, 4, 4);

        // Top edge
        GUIHelper.drawStretchedTexture(xOffset+4, 0, width - 8, 4, texOffsetX + 4, texOffsetY, 64, 4);

        // Bottom edge
        GUIHelper.drawStretchedTexture(xOffset+4, this.height - 4, width - 8, 4, texOffsetX + 4, texOffsetY + 4 + 64, 64, 4);

        // Left edge
        GUIHelper.drawStretchedTexture(xOffset, 4, 4, this.height - 8, texOffsetX, texOffsetY+4, 4, 64);

        // Right edge
        GUIHelper.drawStretchedTexture(xOffset+width - 4, 4, 4, this.height - 8, texOffsetX + 64 + 4, texOffsetY + 3, 4, 64);

        GUIHelper.drawStretchedTexture(xOffset+4, 4, width - 8, this.height - 8, texOffsetX + 4, texOffsetY+4, 64, 64);

        GlStateManager.popMatrix();
    }

    public void drawDebugInfo(GuiScreen screen, int x, int y) {
        FontRenderer font = screen.mc.fontRenderer;
        List<Widget> widgets = getHoveredWidgets();

        List<String> widgetDescriptions = new ArrayList<>();
        for(Widget widget : widgets) {
            widgetDescriptions.add(widget.toString());
        }
        GuiUtils.drawHoveringText(widgetDescriptions, x, y, width, height, 200, font);
    }

    public void drawTooltips(GuiScreen screen, int mouseX, int mouseY) {
        FontRenderer font = screen.mc.fontRenderer;
        List<Widget> widgets = getHoveredWidgets();

        for(Widget widget : widgets) {
            if(!widget.hasToolTip() || widget.getTooltip().size() <= 0) {
                continue;
            }

            GuiUtils.drawHoveringText(widget.getTooltip(), mouseX, mouseY, width, height, 180, font);

            break;
        }
    }

    public void drawSlot(GuiScreen screen, Slot slot, int guiLeft, int guiTop) {
        //Logz.info("Drawing slot at %dx%d", slot.xPos, slot.yPos);

        RenderHelper.disableStandardItemLighting();

        GlStateManager.color(1f, 1f, 1f, 1f);
        screen.mc.renderEngine.bindTexture(tabIcons);

        float offsetX = guiLeft-1;
        float offsetY = guiTop-1;

        GlStateManager.pushMatrix();
        GlStateManager.translate(offsetX, offsetY, 0.0f);

        int texOffsetY = 84;
        int texOffsetX = 84;

        // Top Left corner

        screen.drawTexturedModalRect(slot.xPos, slot.yPos, texOffsetX, texOffsetY, 18, 18);
        RenderHelper.enableGUIStandardItemLighting();

        GlStateManager.popMatrix();
    }

}
