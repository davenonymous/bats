package org.dave.bats.func.batcage;

import net.minecraft.item.ItemStack;
import org.dave.bats.base.BaseNBTSerializable;
import org.dave.bats.func.rules.StackRule;
import org.dave.bats.util.serialization.Store;

import java.util.ArrayList;

public class BatCageLinkConfigItemHandler extends BaseNBTSerializable {
    @Store(key = "whitelist")
    public boolean isWhitelist;

    @Store
    public ArrayList<StackRule> rules;


    public BatCageLinkConfigItemHandler() {
        this.rules = new ArrayList<>();
    }

    public void addRule(StackRule rule) {
        rules.add(0, rule);
        this.markDirty();
    }

    public boolean matchesRule(ItemStack stack) {
        for(StackRule rule : rules) {
            if(rule.doesStackMatch(stack)) {
                return true;
            }
        }

        return false;
    }
}
