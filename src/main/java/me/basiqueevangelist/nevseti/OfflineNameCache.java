package me.basiqueevangelist.nevseti;


import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;
import com.mojang.authlib.GameProfile;
import me.basiqueevangelist.nevseti.nbt.NbtCompoundView;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;


public enum OfflineNameCache {
    INSTANCE;
    
    private final BiMap<UUID, String> names = HashBiMap.create();
    
    @Nullable
    private MinecraftServer currentServer;
    
    OfflineNameCache() {
        OfflineDataChanged.EVENT.register((playerUuid, newTag) -> {
            if (newTag.contains("SavedUsername", NbtType.STRING)) {
                names.put(playerUuid, newTag.getString("SavedUsername"));
            }
        });
    }
    
    /**
     * Gets an unmodifiable version of the UUID &lt;-&gt; username BiMap.
     */
    public BiMap<UUID, String> getNames() {
        return Maps.unmodifiableBiMap(names);
    }
    
    public String getNameFromUUID(UUID playerUuid) {
        if (names.containsKey(playerUuid))
            return names.get(playerUuid);
        
        NbtCompoundView offlineData = OfflineDataCache.INSTANCE.get(playerUuid);
        if (offlineData != null && offlineData.contains("SavedUsername", NbtType.STRING)) {
            names.put(playerUuid, offlineData.getString("SavedUsername"));
        }
        
        assert currentServer != null;
        
        Optional<GameProfile> optionalLoadedProfile = currentServer.getUserCache().getByUuid(playerUuid);
        if (optionalLoadedProfile.isPresent()) {
            GameProfile loadedProfile = optionalLoadedProfile.get();
            names.put(playerUuid, loadedProfile.getName());
            return loadedProfile.getName();
        }
        
        GameProfile profile = new GameProfile(playerUuid, null);
        currentServer.getSessionService().fillProfileProperties(profile, false);
        if (profile.isComplete()) {
            names.put(playerUuid, profile.getName());
            currentServer.getUserCache().add(profile);
            return profile.getName();
        }
        return null;
    }
    
    public UUID getUUIDFromName(String name) {
        if (names.containsValue(name)) {
            return names.inverse().get(name);
        }
        
        assert currentServer != null;
        
        Optional<GameProfile> optionalLoadedProfile = currentServer.getUserCache().findByName(name);
        if (optionalLoadedProfile.isPresent()) {
            GameProfile loadedProfile = optionalLoadedProfile.get();
            names.put(loadedProfile.getId(), name);
            return loadedProfile.getId();
        }
        
        return null;
    }
    
    void onServerStart(MinecraftServer server) {
        currentServer = server;
        
        for (Map.Entry<UUID, NbtCompoundView> playerData : OfflineDataCache.INSTANCE.getPlayers().entrySet()) {
            if (playerData.getValue().contains("SavedUsername", NbtType.STRING)) {
                names.put(playerData.getKey(), playerData.getValue().getString("SavedUsername"));
            }
        }
    }
    
    void onServerShutdown(MinecraftServer server) {
        currentServer = null;
    }
    
    public void setInternal(UUID playerUuid, String name) {
        if (names.containsValue(name))
            names.inverse().remove(name);
        names.put(playerUuid, name);
    }
}
