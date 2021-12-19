package me.badbones69.crazyenchantments.api.multisupport;

import org.bukkit.block.Block;

public interface NMSSupport {
    
    void fullyGrowPlant(Block block);
    
    boolean isFullyGrown(Block block);
    
    void hydrateSoil(Block soil);
    
}