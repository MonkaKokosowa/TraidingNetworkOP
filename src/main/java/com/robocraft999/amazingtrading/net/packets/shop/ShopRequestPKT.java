package com.robocraft999.amazingtrading.net.packets.shop;

import com.robocraft999.amazingtrading.client.gui.shop.ShopMenu;
import com.robocraft999.amazingtrading.net.ITNPacket;
import com.robocraft999.amazingtrading.registry.ATCapabilities;
import com.robocraft999.amazingtrading.resourcepoints.RItemStackHandler;
import com.robocraft999.amazingtrading.utils.ResourcePointHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.network.NetworkEvent;

import java.math.BigInteger;

import static com.robocraft999.amazingtrading.client.gui.shop.ShopWidget.MOUSE_BTN_RIGHT;

/** Packet that is sent when an Item is requested through ui e.g. a button or click on slot
 *
 */

public class ShopRequestPKT implements ITNPacket {
    private int mouseButton = 0;
    private ItemStack stack = ItemStack.EMPTY;
    private boolean shift;
    private boolean ctrl;

    public ShopRequestPKT(int mouseButton, ItemStack stack, boolean shift, boolean ctrl) {
        this.mouseButton = mouseButton;
        this.stack = stack.copy();
        if (mouseButton == MOUSE_BTN_RIGHT){
            this.stack.setCount(1);
        } else if (this.stack.getCount() > stack.getMaxStackSize()) {
            this.stack.setCount(stack.getMaxStackSize()); //important or it will be killed by a filter
        }
        this.shift = shift;
        this.ctrl = ctrl;
    }

    @Override
    public void handle(NetworkEvent.Context context) {
        ServerPlayer player = context.getSender();
        ServerLevel level = player.serverLevel();
        level.getCapability(ATCapabilities.RESOURCE_ITEM_CAPABILITY).ifPresent(cap -> {
            RItemStackHandler handler = (RItemStackHandler) cap.getSlotsHandler();
            for (int i = 0; i < handler.getSlots(); i++){
                ItemStack serverStack = handler.getStackInSlot(i);
                if (serverStack.isEmpty())continue;

                if (serverStack.is(this.stack.getItem())){
                    if (player.containerMenu instanceof ShopMenu shopMenu){
                        long newDecrement = Math.min(this.stack.getCount(), shopMenu.shopInventory.provider.getPoints().longValue() / ResourcePointHelper.getRPBuyCost(this.stack));

                        shopMenu.shopInventory.removeResourcePoints(BigInteger.valueOf(ResourcePointHelper.getRPBuyCost(this.stack) * newDecrement));

                        ItemStack extracted = handler.extractItem(i, (int) newDecrement, false);
                        handler.insertItem(i, extracted, false);
                        if (this.shift){
                            ItemHandlerHelper.giveItemToPlayer(player, extracted);
                        } else {
                            shopMenu.setCarried(extracted);
                            shopMenu.broadcastChanges();
                        }
                        cap.sync();
                    }
                    break;
                }
            }
        });

    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(this.mouseButton);
        buf.writeItem(this.stack);
        buf.writeBoolean(this.shift);
        buf.writeBoolean(this.ctrl);
    }

    public static ShopRequestPKT decode(FriendlyByteBuf buf) {
        return new ShopRequestPKT(buf.readInt(), buf.readItem(), buf.readBoolean(), buf.readBoolean());
    }


}
