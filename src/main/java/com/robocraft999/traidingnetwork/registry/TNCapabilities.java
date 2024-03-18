package com.robocraft999.traidingnetwork.registry;

import com.robocraft999.traidingnetwork.api.capabilities.IResourceItemProvider;
import com.robocraft999.traidingnetwork.api.capabilities.IResourcePointProvider;
import com.robocraft999.traidingnetwork.api.capabilities.IShopNetworkSync;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

public class TNCapabilities {

    private TNCapabilities(){
    }

    public static final Capability<IResourcePointProvider> RESOURCE_POINT_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {});
    public static final Capability<IResourceItemProvider> RESOURCE_ITEM_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {});
    public static final Capability<IShopNetworkSync> SHOP_SETTINGS_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {});

}
