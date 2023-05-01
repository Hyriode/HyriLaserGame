package fr.hyriode.lasergame.game.bonus.models;

import fr.hyriode.lasergame.game.bonus.LGBonus;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import static fr.hyriode.lasergame.game.bonus.BonusManager.SPEED;

public class LGBonusSpeed extends LGBonus {
    public LGBonusSpeed() {
        super(SPEED, 10, (player, __) -> player.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20*10, 1, true, true)),
                (player, __) -> {
//                player.getPlayer().removePotionEffect(PotionEffectType.SPEED);
        });
    }
}
