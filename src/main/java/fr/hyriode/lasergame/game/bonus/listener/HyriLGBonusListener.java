package fr.hyriode.lasergame.game.bonus.listener;

import fr.hyriode.hyrame.actionbar.ActionBar;
import fr.hyriode.hyrame.game.HyriGameState;
import fr.hyriode.hyrame.language.IHyriLanguageManager;
import fr.hyriode.hyrame.listener.HyriListener;
import fr.hyriode.lasergame.HyriLaserGame;
import fr.hyriode.lasergame.game.bonus.HyriLGBonus;
import fr.hyriode.lasergame.game.bonus.HyriLGBonusType;
import fr.hyriode.lasergame.game.player.HyriLGPlayer;
import org.bukkit.ChatColor;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class HyriLGBonusListener extends HyriListener<HyriLaserGame> {

    public HyriLGBonusListener(HyriLaserGame plugin) {
        super(plugin);
    }

    @EventHandler
    public void onPickupBonus(EntityDamageByEntityEvent event){
        if(event.getEntityType() == EntityType.ARMOR_STAND && this.plugin.getGame().getState() != HyriGameState.ENDED){
            if(event.getEntity() instanceof ArmorStand){
                ArmorStand armorStand = (ArmorStand) event.getEntity();
                if(armorStand.getMetadata(HyriLGBonus.getIsBonusMetadata()).size() != 0){
                    HyriLGBonusType bonusType = HyriLGBonusType.valueOf(armorStand.getMetadata(HyriLGBonus.getBonusMetadata()).get(0).asString());
                    HyriLGPlayer lgPlayer = this.plugin.getGame().getPlayer(event.getDamager().getUniqueId());

                    IHyriLanguageManager lm = HyriLaserGame.getLanguageManager();

                    if(lgPlayer.hasBonus()){
                        lgPlayer.getPlayer().sendMessage(lm.getValue(lgPlayer.getPlayer(), "bonus.pickup.already"));
                        return;
                    }

                    if(lgPlayer.isDead()) return;

                    armorStand.remove();
                    lgPlayer.activeBonus(bonusType);

                    new ActionBar(lm.getValue(lgPlayer.getPlayer(), "bonus.pickup.title") + " " + ChatColor.RESET + bonusType.getLanguageName().getForPlayer(lgPlayer.getPlayer())).send(lgPlayer.getPlayer());
                    lgPlayer.getPlayer().sendMessage(ChatColor.DARK_AQUA + lm.getValue(lgPlayer.getPlayer(), "bonus.pickup.title") + " " + ChatColor.RESET + bonusType.getLanguageName().getForPlayer(lgPlayer.getPlayer()));
                    lgPlayer.getPlayer().sendMessage(ChatColor.DARK_AQUA + lm.getValue(lgPlayer.getPlayer(), "bonus.pickup.description") + ChatColor.RESET + bonusType.getLanguageDescription().getForPlayer(lgPlayer.getPlayer()));

                    new BukkitRunnable(){
                        @Override
                        public void run() {
                            plugin.getGame().getBonus(armorStand.getUniqueId()).spawn();
                        }
                    }.runTaskLater(this.plugin, 20L*10);
                }
            }
        }
    }

}
