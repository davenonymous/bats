package org.dave.bats.func.batcage.widgets;

import net.minecraft.client.resources.I18n;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import org.dave.bats.func.batcage.BatCageLinkGuiData;
import org.dave.bats.gui.framework.widgets.WidgetPanel;
import org.dave.bats.gui.framework.widgets.WidgetTabsPanel;

import java.util.Collections;

public class WidgetLinkPanel extends WidgetPanel {
    BatCageLinkGuiData linkData;

    public WidgetLinkPanel(int width, int height, BatCageLinkGuiData linkData) {
        this.setSize(width, height);

        this.linkData = linkData;

        WidgetTabsPanel tabs = new WidgetTabsPanel();
        tabs.setId("Tabs[TypeChooser]");
        tabs.setEdge(WidgetTabsPanel.TabDockEdge.WEST);
        tabs.setDimensions(28, 0, width+28, height);
        this.add(tabs);

        if(linkData.supportsItems) {
            WidgetLinkInventoryPanel invPanel = new WidgetLinkInventoryPanel(width, height, linkData);
            tabs.addPage(invPanel, new ItemStack(Blocks.CHEST), Collections.singletonList(I18n.format("gui.tab.item")));
        }

        // TODO: Implement Fluid and Energy tabs
        //tabs.addPage(new WidgetPanel(), new ItemStack(Items.BUCKET), Collections.singletonList(I18n.format("gui.tab.fluid")));
        //tabs.addPage(new WidgetPanel(), new ItemStack(Items.REDSTONE), Collections.singletonList(I18n.format("gui.tab.energy")));

        WidgetPanel buttonPanel = tabs.getButtonsPanel();
        buttonPanel.setPosition(0, 0);
        buttonPanel.setSize(28, 120);
        this.add(buttonPanel);
    }

}
