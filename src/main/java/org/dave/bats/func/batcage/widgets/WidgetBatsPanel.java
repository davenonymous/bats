package org.dave.bats.func.batcage.widgets;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import org.dave.bats.gui.framework.event.MouseClickEvent;
import org.dave.bats.gui.framework.event.WidgetEventResult;
import org.dave.bats.gui.framework.widgets.WidgetButton;
import org.dave.bats.gui.framework.widgets.WidgetPanel;
import org.dave.bats.gui.framework.widgets.WidgetTextBox;
import org.dave.bats.init.Itemss;
import org.dave.bats.network.MessageGetGuano;
import org.dave.bats.network.PackageHandler;

public class WidgetBatsPanel extends WidgetPanel {
    public WidgetBatsPanel(int width, int height, BlockPos cagePos) {
        this.setSize(width, height);

        String text = I18n.format("gui.hints.step1") + "\n\n";
        text += I18n.format("gui.hints.step2") + "\n\n";
        text += I18n.format("gui.hints.step3");

        WidgetTextBox para1 = new WidgetTextBox(text, 0xFF333333);
        para1.setX(166);
        para1.setWidth(130);
        para1.setHeight(height);
        this.add(para1);

        String buttonText = I18n.format("gui.button.get_guano");
        WidgetButton getGuanoButton = new WidgetButton("GetGuano") {
            @Override
            protected void drawButtonContent(GuiScreen screen, FontRenderer renderer) {
                Minecraft.getMinecraft().getRenderItem().renderItemAndEffectIntoGUI(new ItemStack(Itemss.guano), 2, 2);
                screen.drawString(renderer, buttonText, 30, 6, 0xEEEEEE);
            }
        };
        getGuanoButton.setBackgroundTexture(new ResourceLocation("minecraft", "textures/blocks/mycelium_top.png"));
        getGuanoButton.setWidth(100);
        getGuanoButton.setX(166 + 10);
        getGuanoButton.setY(height - 40);
        getGuanoButton.addListener(MouseClickEvent.class, (event, widget) -> {
            PackageHandler.instance.sendToServer(new MessageGetGuano(cagePos));
            return WidgetEventResult.HANDLED;
        });

        this.add(getGuanoButton);
    }
}
