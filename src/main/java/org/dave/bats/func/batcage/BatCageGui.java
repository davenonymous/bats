package org.dave.bats.func.batcage;

import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.TextFormatting;
import org.dave.bats.func.batcage.widgets.WidgetBatsPanel;
import org.dave.bats.func.batcage.widgets.WidgetLinkPanel;
import org.dave.bats.gui.framework.GUI;
import org.dave.bats.gui.framework.event.GuiDataUpdatedEvent;
import org.dave.bats.gui.framework.event.WidgetEventResult;
import org.dave.bats.gui.framework.widgets.WidgetPanel;
import org.dave.bats.gui.framework.widgets.WidgetTabsPanel;
import org.dave.bats.init.Itemss;

import java.util.ArrayList;
import java.util.List;

public class BatCageGui extends GUI {
    BatCageContainer container;

    public BatCageGui(int width, int height, BatCageContainer container) {
        super(0, 0, width, height);
        this.container = container;

        this.addListener(GuiDataUpdatedEvent.class, (event, widget) -> {
            this.container.updateLinkInfo();
            return WidgetEventResult.CONTINUE_PROCESSING;
        });

        WidgetTabsPanel tabs = new WidgetTabsPanel();
        tabs.setEdge(WidgetTabsPanel.TabDockEdge.NORTH);
        tabs.setId("Tabs[LinkChooser]");
        tabs.setDimensions(-28, -28, width+28, height+28);
        this.add(tabs);

        for(BatCageLinkGuiData linkData : this.container.links.values()) {
            if(!linkData.hasValidPath) {
                continue;
            }

            WidgetLinkPanel linkPanel = new WidgetLinkPanel(this.width, this.height, linkData);
            linkPanel.setPosition(0, 28);
            List<String> tooltip = new ArrayList<>();
            tooltip.add(I18n.format(linkData.currentBlockState.getBlock().getTranslationKey() + ".name"));
            tooltip.add(TextFormatting.GRAY + I18n.format("enumfacing.side", I18n.format("enumfacing." + linkData.linkConfig.side.getName().toLowerCase())));
            tabs.addPage(linkPanel, linkData.stack.copy(), tooltip);
        }

        WidgetBatsPanel batsPanel = new WidgetBatsPanel(this.width, this.height, container.pos);
        batsPanel.setPosition(35, 35);
        tabs.addPage(batsPanel, new ItemStack(Itemss.capturedBat));

        WidgetPanel buttonPanel = tabs.getButtonsPanel();
        buttonPanel.setPosition(0, -28);
        buttonPanel.setSize(310, 31);
        this.add(buttonPanel);
    }


    public BatCageTileEntity getClientBatCageTile() {
        TileEntity tile = this.container.world.getTileEntity(this.container.pos);
        if(tile instanceof BatCageTileEntity) {
            return (BatCageTileEntity) tile;
        }

        return null;
    }
}
