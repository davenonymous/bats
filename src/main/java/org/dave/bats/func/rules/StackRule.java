package org.dave.bats.func.rules;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraftforge.oredict.OreDictionary;
import org.dave.bats.base.BaseNBTSerializable;
import org.dave.bats.util.serialization.Store;

public class StackRule extends BaseNBTSerializable {
    @Store
    private ItemStack stack;

    @Store
    public boolean matchNBT;

    @Store
    public boolean matchMeta;

    @Store
    public boolean matchMod;

    @Store
    public boolean matchOredict;

    public StackRule() {
    }

    public StackRule(NBTTagCompound tagCompound) {
        this();
        this.deserializeNBT(tagCompound);
    }

    public StackRule setStack(ItemStack stack) {
        this.stack = stack;
        return this;
    }

    public ItemStack getStack() {
        return stack.copy();
    }

    public boolean doesStackMatch(ItemStack stack) {
        // If the oredict matches
        if(matchOredict && OreDictionary.itemMatches(this.stack, stack, true)) {
            return true;
        }

        // If the mod matches
        if(matchMod && stack.getItem().getRegistryName().getNamespace().equals(this.stack.getItem().getRegistryName().getNamespace())) {
            return true;
        }

        // If the item does not match our given item, abort
        if(!this.stack.getItem().getRegistryName().equals(stack.getItem().getRegistryName())) {
            return false;
        }

        if(matchMeta && this.stack.getItemDamage() != stack.getItemDamage()) {
            return false;
        }

        if(matchNBT) {
            if(this.stack.hasTagCompound() != stack.hasTagCompound()) {
                // NBT must match, but only one of the stacks actually has a tag
                return false;
            }

            if(stack.hasTagCompound()) {
                // Both stacks have a tag
                if(!NBTUtil.areNBTEquals(stack.getTagCompound(), this.stack.getTagCompound(), true)) {
                    return false;
                }
            }
        }

        return true;
    }

    @Override
    public String toString() {
        return "StackRule{" +
                "stack=" + stack +
                ", matchNBT=" + matchNBT +
                ", matchMeta=" + matchMeta +
                ", matchMod=" + matchMod +
                ", matchOredict=" + matchOredict +
                '}';
    }
}
