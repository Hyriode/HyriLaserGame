package fr.hyriode.lasergame.game.bonus.models;

import fr.hyriode.hyrame.utils.PlayerUtil;
import fr.hyriode.lasergame.game.bonus.LGBonus;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import static fr.hyriode.lasergame.game.bonus.BonusManager.INVISIBILITY;

public class LGBonusInvisibility extends LGBonus {

    public LGBonusInvisibility() {
        super(INVISIBILITY, 5, (player, plugin) -> {
            Player pl = player.getPlayer();
            pl.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 20*10, 0, true, true));
            plugin.getGame().getTeams().forEach(team -> {
                if(!team.getName().equals(player.getTeam().getName()))
                    team.getPlayers().forEach(p -> {
                        PlayerUtil.hideArmor(pl, p.getPlayer());
                    });
            });
        }, (player, plugin) -> {
            plugin.getGame().getTeams().forEach(team -> {
                if(!team.getName().equals(player.getTeam().getName()))
                    team.getPlayers().forEach(p -> {
                        PlayerUtil.showArmor(player.getPlayer(), p.getPlayer());
                    });
            });
        });
    }
}
