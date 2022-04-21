package fr.hyriode.lasergame.listener;

import fr.hyriode.hyrame.actionbar.ActionBar;
import fr.hyriode.hyrame.game.HyriGameState;
import fr.hyriode.hyrame.listener.HyriListener;
import fr.hyriode.lasergame.HyriLaserGame;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;

public class LGPlayerListener extends HyriListener<HyriLaserGame> {

    public LGPlayerListener(HyriLaserGame plugin) {
        super(plugin);
    }

    @EventHandler
    public void onHunger(FoodLevelChangeEvent event){
        event.setCancelled(true);
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event){
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
        if(event.getPlayer().getLocation().getY() < 20){
            event.getPlayer().teleport(this.plugin.getConfiguration().getSpawnLocation());
        }
    }

    @EventHandler
    public void onInteractWorld(PlayerInteractEvent event){
        event.setCancelled(true);
    }

    @EventHandler
    public void onPickup(PlayerArmorStandManipulateEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onSpawn(CreatureSpawnEvent event){
        event.setCancelled(false);
    }
}
