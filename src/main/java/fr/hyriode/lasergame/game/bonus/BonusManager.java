package fr.hyriode.lasergame.game.bonus;

import fr.hyriode.lasergame.game.bonus.models.LGBonusInversion;
import fr.hyriode.lasergame.game.bonus.models.LGBonusInvisibility;
import fr.hyriode.lasergame.game.bonus.models.LGBonusShield;
import fr.hyriode.lasergame.game.bonus.models.LGBonusSpeed;

import java.util.ArrayList;
import java.util.List;

public class BonusManager {

    public final static String INVISIBILITY = "invisibility";
    public final static String INVERSION = "inversion";
    public final static String SHOOT_FASTER = "shoot_faster";
    public final static String SPEED = "speed";
    public final static String SHIELD = "shield";

    private final List<LGBonus> bonus = new ArrayList<>();

    public BonusManager() {
        this.add(new LGBonusInvisibility());
        this.add(new LGBonusInversion());
        this.add(new LGBonus(SHOOT_FASTER, 10));
        this.add(new LGBonusSpeed());
        this.add(new LGBonusShield());
    }

    private void add(LGBonus bonus) {
        this.bonus.add(bonus);
    }

    public List<LGBonus> getBonus() {
        return bonus;
    }

    public LGBonus getBonusByName(String name) {
        return this.bonus.stream().filter(bonus -> bonus.getName().equals(name)).findFirst().orElse(null);
    }
}
