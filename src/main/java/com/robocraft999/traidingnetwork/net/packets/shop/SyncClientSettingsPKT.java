package com.robocraft999.traidingnetwork.net.packets.shop;

import com.robocraft999.traidingnetwork.net.ITNPacket;
import com.robocraft999.traidingnetwork.registry.TNCapabilities;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public record SyncClientSettingsPKT(CompoundTag nbt) implements ITNPacket {

    @Override
    public void handle(NetworkEvent.Context context) {
        context.enqueueWork( () -> {
            LocalPlayer player = Minecraft.getInstance().player;
            if (player != null) {
                player.getCapability(TNCapabilities.SHOP_SETTINGS_CAPABILITY).ifPresent(cap -> {
                    cap.deserializeNBT(nbt);
                });
            }
        });

    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeNbt(nbt);
    }

    public static SyncClientSettingsPKT decode(FriendlyByteBuf buf) {
        return new SyncClientSettingsPKT(buf.readNbt());
    }
}
