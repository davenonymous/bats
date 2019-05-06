package org.dave.bats.func.batcage.widgets;

import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import org.dave.bats.func.batcage.BatCageLinkGuiData;
import org.dave.bats.func.rules.StackRule;
import org.dave.bats.gui.framework.event.ListEntrySelectionEvent;
import org.dave.bats.gui.framework.event.MouseClickEvent;
import org.dave.bats.gui.framework.event.ValueChangedEvent;
import org.dave.bats.gui.framework.event.WidgetEventResult;
import org.dave.bats.gui.framework.widgets.*;
import org.dave.bats.network.MessageInventoryStackRule;
import org.dave.bats.network.PackageHandler;

public class WidgetRuleListEntry extends WidgetListEntry {
    ResourceLocation deleteBackground = new ResourceLocation("minecraft", "textures/blocks/redstone_block.png");
    private int slot;
    BatCageLinkGuiData linkData;

    public WidgetRuleListEntry(StackRule rule, int width, int slot, BatCageLinkGuiData linkData, boolean isImport) {
        super();
        this.slot = slot;
        this.linkData = linkData;
        this.setWidth(width - 4);
        this.setHeight(12);

        int xOffset = 0;
        WidgetItemStack stackWidget = new WidgetItemStack(rule.getStack());
        stackWidget.setSize(10, 10);
        this.add(stackWidget);

        WidgetTextBoxSmall label = new WidgetTextBoxSmall(I18n.format(rule.getStack().getTranslationKey() + ".name"), 0xFFEEEEEE);
        label.setDimensions(12, 1, 50*2, 9);
        this.add(label);

        xOffset += 62;
        WidgetRuleListEntryModeButton nbtButton = new WidgetRuleListEntryModeButton(6, 8, 0);
        nbtButton.setPosition(xOffset, 0);
        nbtButton.setValue(rule.matchNBT);
        nbtButton.setTooltipLines(I18n.format("gui.filter.match_nbt"));
        nbtButton.addListener(ValueChangedEvent.class, (event, widget) -> {
            rule.matchNBT = (boolean) event.newValue;
            MessageInventoryStackRule message = new MessageInventoryStackRule(slot, rule, MessageInventoryStackRule.FilterRuleAction.EDIT, linkData.linkConfig.getId(), linkData.cagePos, isImport);
            PackageHandler.instance.sendToServer(message);
            return WidgetEventResult.HANDLED;
        });
        this.add(nbtButton);

        xOffset += 8;
        WidgetRuleListEntryModeButton metaButton = new WidgetRuleListEntryModeButton(10, 5, 16);
        metaButton.setPosition(xOffset, 2);
        metaButton.setValue(rule.matchMeta);
        metaButton.setTooltipLines(I18n.format("gui.filter.match_meta"));
        metaButton.addListener(ValueChangedEvent.class, (event, widget) -> {
            rule.matchMeta = (boolean) event.newValue;
            MessageInventoryStackRule message = new MessageInventoryStackRule(slot, rule, MessageInventoryStackRule.FilterRuleAction.EDIT, linkData.linkConfig.getId(), linkData.cagePos, isImport);
            PackageHandler.instance.sendToServer(message);
            return WidgetEventResult.HANDLED;
        });
        this.add(metaButton);

        xOffset += 12;
        WidgetRuleListEntryModeButton oreButton = new WidgetRuleListEntryModeButton(8, 6, 26);
        oreButton.setPosition(xOffset, 2);
        oreButton.setValue(rule.matchOredict);
        oreButton.setTooltipLines(I18n.format("gui.filter.match_ore"));
        oreButton.addListener(ValueChangedEvent.class, (event, widget) -> {
            rule.matchOredict = (boolean) event.newValue;
            MessageInventoryStackRule message = new MessageInventoryStackRule(slot, rule, MessageInventoryStackRule.FilterRuleAction.EDIT, linkData.linkConfig.getId(), linkData.cagePos, isImport);
            PackageHandler.instance.sendToServer(message);
            return WidgetEventResult.HANDLED;
        });
        this.add(oreButton);

        xOffset += 10;
        WidgetRuleListEntryModeButton modButton = new WidgetRuleListEntryModeButton(9, 7, 38);
        modButton.setPosition(xOffset, 1);
        modButton.setValue(rule.matchMod);
        modButton.setTooltipLines(I18n.format("gui.filter.match_mod"));
        modButton.addListener(ValueChangedEvent.class, (event, widget) -> {
            rule.matchMod = (boolean) event.newValue;
            MessageInventoryStackRule message = new MessageInventoryStackRule(slot, rule, MessageInventoryStackRule.FilterRuleAction.EDIT, linkData.linkConfig.getId(), linkData.cagePos, isImport);
            PackageHandler.instance.sendToServer(message);
            return WidgetEventResult.HANDLED;
        });

        this.add(modButton);



        WidgetButton deleteButton = new WidgetButton("x");
        deleteButton.setBackgroundTexture(deleteBackground);
        deleteButton.setDimensions(this.width-10, 0, 10, 9);
        deleteButton.setVisible(false);

        deleteButton.addListener(MouseClickEvent.class, (event, widget) -> {
            MessageInventoryStackRule message = new MessageInventoryStackRule(slot, rule, MessageInventoryStackRule.FilterRuleAction.DELETE, linkData.linkConfig.getId(), linkData.cagePos, isImport);
            PackageHandler.instance.sendToServer(message);
            return WidgetEventResult.HANDLED;
        });



        this.addListener(ListEntrySelectionEvent.class, (event, widget) -> {
            deleteButton.setVisible(event.selected);
            return WidgetEventResult.CONTINUE_PROCESSING;
        });

        this.add(deleteButton);
    }
}
