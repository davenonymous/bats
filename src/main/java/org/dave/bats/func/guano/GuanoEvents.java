package org.dave.bats.func.guano;

import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.items.ItemHandlerHelper;
import org.dave.bats.init.Itemss;

public class GuanoEvents {

    @SubscribeEvent
    public static void onEntityJoinWorld(EntityJoinWorldEvent event) {
        if(event.getWorld().isRemote) {
            return;
        }

        if(!(event.getEntity() instanceof EntityItem)) {
            return;
        }

        EntityItem itemEntity = (EntityItem) event.getEntity();
        if(itemEntity.getItem().getItem() == Itemss.capturedBat) {
            EntityBat entityBat = new EntityBat(event.getWorld());
            entityBat.setLocationAndAngles(itemEntity.posX, itemEntity.posY, itemEntity.posZ, MathHelper.wrapDegrees(event.getWorld().rand.nextFloat() * 360.0F), 0.0F);
            entityBat.onInitialSpawn(event.getWorld().getDifficultyForLocation(new BlockPos(itemEntity)), (IEntityLivingData)null);

            event.getWorld().spawnEntity(entityBat);
            entityBat.playLivingSound();
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onPlayerRightClick(PlayerInteractEvent.EntityInteract event) {
        if(event.getWorld().isRemote) {
            return;
        }

        if(event.getEntityPlayer() == null || event.getTarget() == null) {
            return;
        }

        if(event.getItemStack().isEmpty()) {
            return;
        }

        if(event.getItemStack().getItem() != Items.LEAD) {
            return;
        }

        if(!(event.getTarget() instanceof EntityBat)) {
            return;
        }

        ItemStack capBatStack = new ItemStack(Itemss.capturedBat);
        ItemHandlerHelper.giveItemToPlayer(event.getEntityPlayer(), capBatStack);
        event.getTarget().setDead();
    }
}
