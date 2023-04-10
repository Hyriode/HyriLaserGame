package fr.hyriode.lasergame.game.bonus.models;

import fr.hyriode.lasergame.game.bonus.LGBonus;
import fr.hyriode.lasergame.game.bonus.effect.SphereEffect;

import static fr.hyriode.lasergame.game.bonus.BonusManager.SHIELD;

public class LGBonusShield extends LGBonus {

    public LGBonusShield() {
        super(SHIELD, 5);
        this.before = (player, plugin) -> {
            SphereEffect sphereEffect = new SphereEffect(plugin, player);
            sphereEffect.start();
            this.after = (p, pl) -> {
                sphereEffect.stop();
            };
        };

    }
}
