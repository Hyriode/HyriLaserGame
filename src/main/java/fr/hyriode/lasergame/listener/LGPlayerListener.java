package fr.hyriode.lasergame.listener;

import fr.hyriode.hyrame.actionbar.ActionBar;
import fr.hyriode.hyrame.game.HyriGameState;
import fr.hyriode.hyrame.listener.HyriListener;
import fr.hyriode.lasergame.HyriLaserGame;
import fr.hyriode.lasergame.game.player.LGGamePlayer;
import org.bukkit.Location;
import org.bukkit.entity.Player;
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
        final Player player = event.getPlayer();
        final LGGamePlayer gamePlayer = this.plugin.getGame().getPlayer(player);
        if (gamePlayer.isDead()) {
            gamePlayer.respawn();
        }

        final Location loc = this.plugin.getConfiguration().getWaitingRoom().getSpawn().asBukkit();
        if((this.plugin.getGame().getState() == HyriGameState.READY || this.plugin.getGame().getState() == HyriGameState.WAITING)
                && player.getLocation().getY() <= loc.getY() - 10){
            player.teleport(loc);
        }

        if(player.getLocation().getY() < 20){
            player.teleport(this.plugin.getConfiguration().getTeam(this.plugin.getGame().getPlayer(player).getTeam().getName()).getSpawnLocation());
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
