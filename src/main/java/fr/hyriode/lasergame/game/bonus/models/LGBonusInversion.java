package fr.hyriode.lasergame.game.bonus.models;

import fr.hyriode.lasergame.game.bonus.LGBonus;

import static fr.hyriode.lasergame.game.bonus.BonusManager.INVERSION;

public class LGBonusInversion extends LGBonus {

    public LGBonusInversion() {
        super(INVERSION, 10, (player, __) -> player.giveInverseArmor(),
                (player, __) -> {
            if(!player.isDead()) {
                player.giveArmor();
            }
        });
    }
}
