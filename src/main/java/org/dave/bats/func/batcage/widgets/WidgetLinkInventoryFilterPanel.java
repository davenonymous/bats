package org.dave.bats.func.batcage.widgets;

import net.minecraft.client.resources.I18n;
import net.minecraft.util.math.BlockPos;
import org.dave.bats.func.batcage.BatCageLinkGuiData;
import org.dave.bats.func.rules.StackRule;
import org.dave.bats.gui.framework.event.GuiDataUpdatedEvent;
import org.dave.bats.gui.framework.event.MouseClickEvent;
import org.dave.bats.gui.framework.event.ValueChangedEvent;
import org.dave.bats.gui.framework.event.WidgetEventResult;
import org.dave.bats.gui.framework.widgets.*;
import org.dave.bats.network.MessageInventoryLinkConfig;
import org.dave.bats.network.MessageInventoryStackRule;
import org.dave.bats.network.PackageHandler;
import org.dave.bats.util.FaceIdentifier;

public class WidgetLinkInventoryFilterPanel extends WidgetPanel {
    BatCageLinkGuiData linkData;

    public WidgetLinkInventoryFilterPanel(int width, int height, BatCageLinkGuiData linkData) {
        super();
        this.setSize(width, height);

        this.linkData = linkData;


        int yOffset = 7;

        WidgetTextBox importCaption = new WidgetTextBox(I18n.format("gui.filter.import_rules"), 0x333333);
        importCaption.setWidth(200);
        importCaption.setPosition(0, yOffset);
        this.add(importCaption);

        WidgetNumberSelect importPriority = new WidgetNumberSelect();
        importPriority.addChoice(-4, -3, -2, -1, 0, 1, 2, 3, 4);
        importPriority.setDimensions(width-53, yOffset-2, 30, 12);
        importPriority.setTooltipLines(I18n.format("gui.filter.priority"));
        importPriority.addListener(ValueChangedEvent.class, (event, widget) -> {
            MessageInventoryLinkConfig message = new MessageInventoryLinkConfig(MessageInventoryLinkConfig.ConfigAction.SET_PRIORITY, linkData.linkConfig.getId(), linkData.cagePos, true, linkData.linkConfig.itemImport.isWhitelist, (Integer) event.newValue);
            PackageHandler.instance.sendToServer(message);
            return WidgetEventResult.HANDLED;
        });
        this.add(importPriority);

        WidgetWhitelistButton importWhitelistButton = new WidgetWhitelistButton();
        importWhitelistButton.setPosition(width-19, yOffset-2);
        importWhitelistButton.setTooltipLines(I18n.format("gui.filter.mode"));
        importWhitelistButton.addListener(ValueChangedEvent.class, (event, widget) -> {
            MessageInventoryLinkConfig message = new MessageInventoryLinkConfig(MessageInventoryLinkConfig.ConfigAction.SET_WHITELIST, linkData.linkConfig.getId(), linkData.cagePos, true, (Boolean) event.newValue, linkData.linkConfig.itemPriority);
            PackageHandler.instance.sendToServer(message);
            return WidgetEventResult.HANDLED;
        });
        this.add(importWhitelistButton);
        yOffset += 12;


        WidgetList importRules = new WidgetList();
        importRules.setDimensions(0, yOffset, width-4, 68);

        importRules.addListener(MouseClickEvent.class, (event, widget) -> {
            if(event.carriedStack.isEmpty()) {
                return WidgetEventResult.CONTINUE_PROCESSING;
            }

            StackRule stackRule = new StackRule();
            stackRule.setStack(event.carriedStack);

            FaceIdentifier id = linkData.linkConfig.getId();
            BlockPos cageTilePos = linkData.cagePos;

            MessageInventoryStackRule message = new MessageInventoryStackRule(-1, stackRule, MessageInventoryStackRule.FilterRuleAction.ADD, id, cageTilePos, true);
            PackageHandler.instance.sendToServer(message);

            importRules.scrollToTop();
            return WidgetEventResult.HANDLED;
        });
        this.add(importRules);

        Widget scrollUpButton = importRules.getScrollUpButton(0xEEEEEE);
        scrollUpButton.setPosition(width-21, yOffset+62);
        this.add(scrollUpButton);

        Widget scrollDownButton = importRules.getScrollDownButton(0xEEEEEE);
        scrollDownButton.setPosition(width-13, yOffset+62);
        this.add(scrollDownButton);

        yOffset += 68 + 4;


        yOffset += 6;

        WidgetTextBox exportCaption = new WidgetTextBox(I18n.format("gui.filter.export_rules"), 0x333333);
        exportCaption.setWidth(200);
        exportCaption.setPosition(0, yOffset);
        this.add(exportCaption);

        WidgetWhitelistButton exportWhitelistButton = new WidgetWhitelistButton();
        exportWhitelistButton.setPosition(width-19, yOffset-2);
        exportWhitelistButton.setTooltipLines(I18n.format("gui.filter.mode"));
        exportWhitelistButton.addListener(ValueChangedEvent.class, (event, widget) -> {
            MessageInventoryLinkConfig message = new MessageInventoryLinkConfig(MessageInventoryLinkConfig.ConfigAction.SET_WHITELIST, linkData.linkConfig.getId(), linkData.cagePos, false, (Boolean) event.newValue, linkData.linkConfig.itemPriority);
            PackageHandler.instance.sendToServer(message);
            return WidgetEventResult.HANDLED;
        });
        this.add(exportWhitelistButton);
        yOffset += 12;

        WidgetList exportRules = new WidgetList();
        exportRules.setDimensions(0, yOffset, width-4, 68);
        exportRules.addListener(MouseClickEvent.class, (event, widget) -> {
            if(event.carriedStack.isEmpty()) {
                return WidgetEventResult.CONTINUE_PROCESSING;
            }

            StackRule stackRule = new StackRule();
            stackRule.setStack(event.carriedStack);

            FaceIdentifier id = linkData.linkConfig.getId();
            BlockPos cageTilePos = linkData.cagePos;

            MessageInventoryStackRule message = new MessageInventoryStackRule(-1, stackRule, MessageInventoryStackRule.FilterRuleAction.ADD, id, cageTilePos, false);
            PackageHandler.instance.sendToServer(message);

            exportRules.scrollToTop();
            return WidgetEventResult.HANDLED;
        });
        this.add(exportRules);

        Widget exportScrollUpButton = exportRules.getScrollUpButton(0xEEEEEE);
        exportScrollUpButton.setPosition(width-21, yOffset+62);
        this.add(exportScrollUpButton);

        Widget exportScrollDownButton = exportRules.getScrollDownButton(0xEEEEEE);
        exportScrollDownButton.setPosition(width-13, yOffset+62);
        this.add(exportScrollDownButton);

        this.addListener(GuiDataUpdatedEvent.class, (event, widget) -> {
            importWhitelistButton.setValue(linkData.linkConfig.itemImport.isWhitelist, false);
            exportWhitelistButton.setValue(linkData.linkConfig.itemExport.isWhitelist, false);
            importPriority.setValue(linkData.linkConfig.itemPriority, false);
            importRules.clear();
            exportRules.clear();

            int importSlot = 0;
            for(StackRule rule : linkData.linkConfig.itemImport.rules) {
                WidgetRuleListEntry ruleWidget = new WidgetRuleListEntry(rule, width-4, importSlot, linkData, true);
                importRules.addListEntry(ruleWidget);

                importSlot++;
            }

            int exportSlot = 0;
            for(StackRule rule : linkData.linkConfig.itemExport.rules) {
                WidgetRuleListEntry ruleWidget = new WidgetRuleListEntry(rule, width-4, exportSlot, linkData, false);
                exportRules.addListEntry(ruleWidget);

                exportSlot++;
            }


            return WidgetEventResult.CONTINUE_PROCESSING;
        });
        this.fireEvent(new GuiDataUpdatedEvent());


    }
}
