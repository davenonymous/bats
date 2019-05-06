package org.dave.bats.gui.framework.widgets;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;

public class WidgetItemStack extends Widget {
    ItemStack stack;

    public WidgetItemStack(ItemStack stack) {
        this.setSize(16, 16);
        this.stack = stack;
        ITooltipFlag.TooltipFlags tooltipFlag = Minecraft.getMinecraft().gameSettings.advancedItemTooltips ? ITooltipFlag.TooltipFlags.ADVANCED : ITooltipFlag.TooltipFlags.NORMAL;
        this.setTooltipLines(stack.getTooltip(Minecraft.getMinecraft().player, tooltipFlag));
    }

    public WidgetItemStack setStack(ItemStack stack) {
        this.stack = stack;
        return this;
    }

    public ItemStack getStack() {
        return stack;
    }

    @Override
    public void draw(GuiScreen screen) {
        super.draw(screen);

        if(this.stack == null || this.stack.isEmpty()) {
            return;
        }

        GlStateManager.pushMatrix();

        GlStateManager.disableLighting();
        GlStateManager.color(1F, 1F, 1F); //Forge: Reset color in case Items change it.
        GlStateManager.enableBlend(); //Forge: Make sure blend is enabled else tabs show a white border.

        double xScale = this.width / 16.0f;
        double yScale = this.height / 16.0f;

        GlStateManager.scale(xScale, yScale, 1.0f);

        RenderHelper.enableGUIStandardItemLighting();
        Minecraft.getMinecraft().getRenderItem().renderItemAndEffectIntoGUI(this.stack, 0, 0);
        RenderHelper.enableStandardItemLighting();

        GlStateManager.popMatrix();
    }
}
