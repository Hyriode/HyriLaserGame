package fr.hyriode.lasergame.game.bonus;

import fr.hyriode.lasergame.HyriLaserGame;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

public class LGBonus {

    private static final String ISBONUS_METADATA = "IsBonus";

    private ArmorStand armorStand;
    private final HyriLaserGame plugin;
    private final Location location;

    public LGBonus(ArmorStand armorStand, Location location, HyriLaserGame plugin){
        this.armorStand = armorStand;
        this.location = location;
        this.plugin = plugin;
    }

    public static LGBonus spawn(Location location, HyriLaserGame plugin){
        ArmorStand armorStand = location.getWorld().spawn(location.clone(), ArmorStand.class);

        armorStand.setVisible(false);
        armorStand.setHelmet(new ItemStack(Material.GOLD_BLOCK));
        armorStand.setGravity(false);
        armorStand.setCanPickupItems(false);
        armorStand.setBasePlate(false);
        armorStand.setMetadata(ISBONUS_METADATA, new FixedMetadataValue(plugin, true));
        System.out.println("SPAWN ARMOR STAND");

        new ILGBonusAnimation.Default(plugin, location, armorStand).start();
        LGBonus bonus = new LGBonus(armorStand, location, plugin);
        plugin.getGame().addBonus(bonus);
        return bonus;
    }

    public void respawn(){
        this.removeBonus();
        Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
            this.armorStand = spawn(location, plugin).getArmorStand();
        }, 20*25);
    }

    public ArmorStand getArmorStand() {
        return this.armorStand;
    }

    public Location getLocation() {
        return this.location;
    }

    public static String getIsBonusMetadata() {
        return ISBONUS_METADATA;
    }

    private void removeBonus(){
        this.plugin.getGame().removeBonus(this.armorStand.getUniqueId());
        this.armorStand.remove();
    }
}
