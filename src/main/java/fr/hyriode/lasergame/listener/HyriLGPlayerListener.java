package fr.hyriode.lasergame.listener;

import fr.hyriode.hyrame.actionbar.ActionBar;
import fr.hyriode.hyrame.game.HyriGameState;
import fr.hyriode.hyrame.listener.HyriListener;
import fr.hyriode.lasergame.HyriLaserGame;
import fr.hyriode.lasergame.game.bonus.HyriLGBonusType;
import fr.hyriode.lasergame.game.player.HyriLGPlayer;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;

public class HyriLGPlayerListener extends HyriListener<HyriLaserGame> {

    public HyriLGPlayerListener(HyriLaserGame plugin) {
        super(plugin);
    }

    @EventHandler
    public void onHunger(FoodLevelChangeEvent event){
        event.setCancelled(true);
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event){
        if(this.plugin.getGame().getState() == HyriGameState.PLAYING && event.getCause() == EntityDamageEvent.DamageCause.FALL){
            if(event.getEntity() instanceof Player){
                Player player = (Player) event.getEntity();
                Location locPlayer = player.getLocation().clone();
                locPlayer.setY(player.getLocation().getY() - 1);
                Block block = locPlayer.getBlock();
                if(block.getType() != Material.SEA_LANTERN) {
                    HyriLGPlayer lgPlayer = this.plugin.getGame().getPlayer(player.getUniqueId());
                    if(lgPlayer.getBonus() == HyriLGBonusType.SHIELD){
                        lgPlayer.setBonus(null);
                        new ActionBar(ChatColor.GREEN + this.plugin.getHyrame().getLanguageManager().getValue(player, "damage.death.shield_fell")).send(player);
                    }else {
                        lgPlayer.addDeath();
                        new ActionBar(ChatColor.RED + this.plugin.getHyrame().getLanguageManager().getValue(player, "damage.death.fell")).send(player);
                        player.sendMessage(ChatColor.RED + this.plugin.getHyrame().getLanguageManager().getValue(player, "damage.death.fell"));
                        if (this.plugin.getGame().isFinalKill()) {
                            this.plugin.getGame().win(this.plugin.getGame().getWinner());
                        }
                    }
                }
            }
        }
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
    public void onBreak(BlockBreakEvent event){
        event.setCancelled(true);
    }

    @EventHandler
    public void onPickup(PlayerArmorStandManipulateEvent event) {
        event.setCancelled(true);
    }
}
