package org.dave.bats.func.batcage.widgets;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import org.dave.bats.func.batcage.BatCageLinkGuiData;
import org.dave.bats.gui.framework.WidgetSlot;
import org.dave.bats.gui.framework.event.ValueChangedEvent;
import org.dave.bats.gui.framework.event.VisibilityChangedEvent;
import org.dave.bats.gui.framework.event.WidgetEventResult;
import org.dave.bats.gui.framework.widgets.WidgetPanel;
import org.dave.bats.gui.framework.widgets.WidgetSelectButton;
import org.dave.bats.gui.framework.widgets.WidgetTextBox;
import org.dave.bats.network.MessageEnabledSlots;
import org.dave.bats.network.PackageHandler;

import java.util.ArrayList;
import java.util.List;

public class WidgetLinkInventoryAccessPanel extends WidgetPanel {
    BatCageLinkGuiData linkData;
    List<WidgetPanel> pages;

    public WidgetLinkInventoryAccessPanel(int width, int height, BatCageLinkGuiData linkData) {
        super();
        this.setSize(width, height);

        this.linkData = linkData;
        this.pages = new ArrayList<>();

        WidgetTextBox text = new WidgetTextBox(I18n.format("key.categories.inventory"), 0x333333);
        text.setWidth(200);
        text.setPosition(7, 7);
        this.add(text);

        WidgetSelectButton<Integer> pageSelect = new WidgetSelectButton<>();
        pageSelect.setPosition(155, 4);
        pageSelect.setSize(14, 14);

        for(int page = 0; page < getPageCount(); page++) {
            WidgetPanel slotPage = new WidgetPanel();
            slotPage.setDimensions(0, 0, 0, 0);
            slotPage.setVisible(page == 0);

            int firstVisibleSlot = page * 27;
            int lastVisibleSlot = firstVisibleSlot + 27;
            int slotNum = 0;
            for(WidgetSlot slot : linkData.slots) {
                if(slotNum >= firstVisibleSlot && slotNum < lastVisibleSlot) {
                    slot.bindToWidget(slotPage);
                }
                slotNum++;
            }

            slotPage.addListener(VisibilityChangedEvent.class, (event, widget1) -> {
                PackageHandler.instance.sendToServer(new MessageEnabledSlots(Minecraft.getMinecraft().player.openContainer.inventorySlots));
                return WidgetEventResult.CONTINUE_PROCESSING;
            });

            this.pages.add(slotPage);
            this.add(slotPage);
            pageSelect.addChoice(page);
        }

        if(shouldShowPageButtons()) {
            pageSelect.addListener(ValueChangedEvent.class, (event, widget) -> {
                WidgetPanel newPage = this.pages.get((Integer) event.newValue);
                if (newPage == null) {
                    return WidgetEventResult.CONTINUE_PROCESSING;
                }

                WidgetPanel oldPage = this.pages.get((Integer) event.oldValue);
                if (oldPage != null) {
                    oldPage.setVisible(false);
                }

                newPage.setVisible(true);
                return WidgetEventResult.CONTINUE_PROCESSING;
            });
            this.add(pageSelect);
        }
    }

    private boolean shouldShowPageButtons() {
        return getPageCount() > 1;
    }

    private int getPageCount() {
        return (int) Math.ceil(linkData.slots.size() / 27.0f);
    }
}
