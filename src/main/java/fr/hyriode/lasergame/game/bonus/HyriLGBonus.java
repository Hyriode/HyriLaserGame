package fr.hyriode.lasergame.game.bonus;

import fr.hyriode.lasergame.HyriLaserGame;
import net.minecraft.server.v1_8_R3.EntityArmorStand;
import net.minecraft.server.v1_8_R3.World;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.Arrays;
import java.util.Random;
import java.util.UUID;

public class HyriLGBonus {

    private ArmorStand armorStand;
    private final Location location;
    private final HyriLaserGame plugin;
    private IHyriLGBonusAnimation animation;

    private static final String ISBONUS_METADATA = "IsBonus";
    private static final String BONUS_METADATA = "Bonus";

    public HyriLGBonus(Location location, HyriLaserGame laserGame) {
        this.location = location;
        this.plugin = laserGame;
    }

    public HyriLGBonus spawn(){
        ArmorStand armorStand = location.getWorld().spawn(location.clone(), ArmorStand.class);
        HyriLGBonusType bonusType = Arrays.asList(HyriLGBonusType.values())
                .get(new Random().nextInt(HyriLGBonusType.values().length));

        armorStand.setVisible(false);
        armorStand.setHelmet(new ItemStack(Material.GOLD_BLOCK));
        armorStand.setGravity(false);
        armorStand.setCanPickupItems(false);
        armorStand.setMetadata(ISBONUS_METADATA, new FixedMetadataValue(this.plugin, true));
        armorStand.setMetadata(BONUS_METADATA, new FixedMetadataValue(this.plugin, bonusType.name()));

        this.armorStand = armorStand;
        this.animation = new IHyriLGBonusAnimation.Default(this.plugin, this.location, this.armorStand);
        this.animation.start();

        return this;
    }

    public ArmorStand getArmorStand() {
        return armorStand;
    }

    public UUID getUUID(){
        return armorStand.getUniqueId();
    }

    public static String getBonusMetadata() {
        return BONUS_METADATA;
    }

    public static String getIsBonusMetadata() {
        return ISBONUS_METADATA;
    }
}
