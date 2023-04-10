package fr.hyriode.lasergame.game.gui;

import fr.hyriode.hyrame.inventory.HyriInventory;
import fr.hyriode.hyrame.item.ItemBuilder;
import fr.hyriode.lasergame.HyriLaserGame;
import fr.hyriode.lasergame.game.bonus.LGBonus;
import fr.hyriode.lasergame.game.bonus.LGBonusType;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class BonusMenuGui extends HyriInventory {

    private final HyriLaserGame plugin;

    public BonusMenuGui(HyriLaserGame plugin, Player player) {
        super(player, "Choisis ton Bonus", 9*3);
        this.plugin = plugin;
        for (int i = 0; i < LGBonusType.values().length; i++) {
            LGBonus bonus = LGBonusType.valueOf(LGBonusType.values()[i].name()).get();
            this.setItem(i, new ItemBuilder(Material.GOLD_BLOCK).withName(bonus.getName()).build(), event -> {
                this.plugin.getGame().getPlayer(player).activeSpecificBonus(bonus, null);
            });
        }
    }
}
