package org.dave.bats.func.batcage.widgets;

import org.dave.bats.func.batcage.BatCageLinkGuiData;
import org.dave.bats.gui.framework.widgets.WidgetPanel;

public class WidgetLinkInventoryPanel extends WidgetPanel {
    BatCageLinkGuiData linkData;
    private final WidgetLinkInventoryFilterPanel filterPanel;
    private final WidgetLinkInventoryAccessPanel accessPanel;

    public WidgetLinkInventoryPanel(int width, int height, BatCageLinkGuiData linkData) {
        super();
        this.setSize(width, height);

        this.linkData = linkData;
        this.accessPanel = new WidgetLinkInventoryAccessPanel(175, 80, linkData);
        this.filterPanel = new WidgetLinkInventoryFilterPanel(width - 178, height, linkData);
        this.filterPanel.setPosition(175, 0);

        this.add(this.accessPanel);
        this.add(this.filterPanel);
    }
}
