package fr.hyriode.lasergame.listener;

import fr.hyriode.hyrame.listener.HyriListener;
import fr.hyriode.lasergame.HyriLaserGame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class HyriLGPlayerListener extends HyriListener<HyriLaserGame> {

    public HyriLGPlayerListener(HyriLaserGame plugin) {
        super(plugin);
    }

    //+1 death on fall
    @EventHandler
    public void onHunger(FoodLevelChangeEvent event){
        event.setCancelled(true);
    }

    @EventHandler
    public void onHealth(EntityDamageEvent event){
        event.setCancelled(true);
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event){
        event.setCancelled(true);
    }

    @EventHandler
    public void onInteractInventory(InventoryClickEvent event){
        event.setCancelled(true);
    }

    @EventHandler
    public void onMovePlayer(PlayerMoveEvent event){
        if(event.getPlayer().getLocation().getY() < -20){
            event.getPlayer().teleport(this.plugin.getConfiguration().getSpawnLocation());
        }
    }

    @EventHandler
    public void onInteractWorld(PlayerInteractEvent event){
        event.setCancelled(true);
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event){
        if(!event.getPlayer().isOp())
            event.setCancelled(true);
    }

}
