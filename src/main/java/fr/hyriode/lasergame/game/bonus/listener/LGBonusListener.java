package fr.hyriode.lasergame.game.bonus.listener;

import fr.hyriode.hyrame.listener.HyriListener;
import fr.hyriode.lasergame.HyriLaserGame;
import fr.hyriode.lasergame.game.bonus.LGBonus;
import fr.hyriode.lasergame.game.player.LGGamePlayer;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class LGBonusListener extends HyriListener<HyriLaserGame> {

    public LGBonusListener(HyriLaserGame plugin) {
        super(plugin);
    }

    @EventHandler
    public void onPickupBonus(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof ArmorStand) {
            this.plugin.getGame().getPlayer(event.getDamager().getUniqueId()).activeBonus((ArmorStand) event.getEntity());
        }
    }

    @EventHandler
    public void onClick(PlayerArmorStandManipulateEvent event) {
        LGGamePlayer player = this.plugin.getGame().getPlayer(event.getPlayer().getUniqueId());
        player.activeBonus(event.getRightClicked());
    }

    @EventHandler
    public void onTriggeredBonus(PlayerMoveEvent event) {
        final Location loc = event.getTo();
        LGGamePlayer player = this.plugin.getGame().getPlayer(event.getPlayer().getUniqueId());

        for (LGBonus locBonus : this.plugin.getGame().getBonus()) {
            if (this.isInBonus(loc, locBonus.getLocation())) {
                if (!player.hasBonus()) {
                    player.activeBonus(locBonus.getArmorStand());
                }
                break;
            }
        }
    }

    private boolean isInBonus(Location locTo, Location bonus) {
        return bonus.getBlockX() == locTo.getBlockX() && bonus.getBlockY() == locTo.getBlockY() && bonus.getBlockZ() == locTo.getBlockZ();
    }

}
