package me.badbones69.crazyenchantments.multisupport;

import me.badbones69.crazyenchantments.api.CrazyManager;
import me.badbones69.crazyenchantments.api.managers.WingsManager;
import me.badbones69.crazyenchantments.multisupport.plotsquared.PlotSquaredVersion;
import me.badbones69.crazyenchantments.multisupport.worldguard.WorldGuardVersion;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;

public class Support {
    
    private static Support instance = new Support();
    //private static FactionPlugin factionPlugin = null;
    private CrazyManager ce = CrazyManager.getInstance();
    private WingsManager wingsManager;
    //private WorldGuardVersion worldGuardVersion;
    //private PlotSquaredVersion plotSquaredVersion;
    
    public static Support getInstance() {
        return instance;
    }
    
    public void load() {
        wingsManager = ce.getWingsManager();
        //worldGuardVersion = ce.getWorldGuardSupport();
       //plotSquaredVersion = ce.getPlotSquaredSupport();
    }
    
    //public boolean inTerritory(Player player) {
        //if (factionPlugin != null && factionPlugin.inTerritory(player)) {
        //    return true;
       // }
       // return SupportedPlugins.PLOT_SQUARED.isPluginLoaded() && plotSquaredVersion.inTerritory(player);
    //}
    
    public boolean isFriendly(Entity pEntity, Entity oEntity) {
        if (pEntity instanceof Player && oEntity instanceof Player) {
            Player player = (Player) pEntity;
            Player other = (Player) oEntity;
            //if (factionPlugin != null && factionPlugin.isFriendly(player, other)) {
            //    return true;
            //}
        }
        return false;
    }
    
    public boolean isVanished(Entity p) {
        for (MetadataValue meta : p.getMetadata("vanished")) {
            if (meta.asBoolean()) return true;
        }
        return false;
    }
    
    //public boolean canBreakBlock(Player player, Block block) {
        //if (player != null) {
            //if (factionPlugin != null && !factionPlugin.canBreakBlock(player, block)) {
            //    return false;
            //}
            //return !SupportedPlugins.PRECIOUS_STONES.isPluginLoaded() || PreciousStonesSupport.canBreakBlock(player, block);
        //}
    //    return true;
    //}
    
    //public boolean allowsPVP(Location location) {
        //if (SupportedPlugins.TOWNY.isPluginLoaded() && !TownySupport.allowsPvP(location)) {
        //    return false;
        //}
        //return !SupportedPlugins.WORLD_EDIT.isPluginLoaded() || !SupportedPlugins.WORLD_GUARD.isPluginLoaded() || worldGuardVersion.allowsPVP(location);
    //}
    
    //public boolean allowsBreak(Location location) {
        //return !SupportedPlugins.WORLD_EDIT.isPluginLoaded() || !SupportedPlugins.WORLD_GUARD.isPluginLoaded() || worldGuardVersion.allowsBreak(location);
    //}
    
    //public boolean allowsExplotions(Location location) {
        //return !SupportedPlugins.WORLD_EDIT.isPluginLoaded() || !SupportedPlugins.WORLD_GUARD.isPluginLoaded() || worldGuardVersion.allowsExplosions(location);
    //}
    
    //public boolean inWingsRegion(Player player) {
        //if (SupportedPlugins.WORLD_EDIT.isPluginLoaded() && SupportedPlugins.WORLD_GUARD.isPluginLoaded()) {
        //    for (String region : wingsManager.getRegions()) {
        //        if (worldGuardVersion.inRegion(region, player.getLocation())) {
        //            return true;
        //        } else {
        //            if (wingsManager.canOwnersFly() && worldGuardVersion.isOwner(player)) {
        //                return true;
        //            }
        //            if (wingsManager.canMembersFly() && worldGuardVersion.isMember(player)) {
        //                return true;
        //            }
        //        }
        //    }
        //}
    //    return false;
    //}
    
    public void noStack(Entity entity) {

    }
    
    public enum SupportedPlugins {

        SPARTAN("Spartan");
        
        private final String name;
        private static final Map<SupportedPlugins, Boolean> cachedPluginState = new HashMap<>();
        
        private SupportedPlugins(String name) {
            this.name = name;
        }
        
        public String getName() {
            return name;
        }
        
        public boolean isPluginLoaded() {
            return cachedPluginState.get(this);
        }
        
        public Plugin getPlugin() {
            return Bukkit.getServer().getPluginManager().getPlugin(name);
        }
        
        /**
         * Used to update the states of plugins CE hooks into.
         */
        public static void updatePluginStates() {
            cachedPluginState.clear();
            for (SupportedPlugins supportedPlugin : values()) {
                Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin(supportedPlugin.name);
                if (plugin != null && plugin.isEnabled()) {
                    //List<String> authors = plugin.getDescription().getAuthors();
                    ///String version = plugin.getDescription().getVersion();
                    //String website = plugin.getDescription().getWebsite() != null ? plugin.getDescription().getWebsite() : "";
                    cachedPluginState.put(supportedPlugin, true);
                } else {
                    cachedPluginState.put(supportedPlugin, false);
                }
            }
        }
    }
}