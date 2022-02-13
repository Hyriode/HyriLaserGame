package fr.hyriode.lasergame.game.player;

import fr.hyriode.hyrame.actionbar.ActionBar;
import fr.hyriode.hyrame.game.HyriGame;
import fr.hyriode.hyrame.game.HyriGamePlayer;
import fr.hyriode.hyrame.game.HyriGameState;
import fr.hyriode.hyrame.item.ItemBuilder;
import fr.hyriode.hyrame.item.ItemNBT;
import fr.hyriode.hyrame.title.Title;
import fr.hyriode.lasergame.HyriLaserGame;
import fr.hyriode.lasergame.game.bonus.HyriLGBonusType;
import fr.hyriode.lasergame.game.item.HyriLGLaserGun;
import fr.hyriode.lasergame.game.scoreboard.HyriLGScoreboard;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.stream.Collectors;

public class HyriLGPlayer extends HyriGamePlayer {

    private static final String LASER_GUN_NBT = "LaserGun";

    private HyriLaserGame plugin;

    private HyriLGScoreboard scoreboard;

    private HyriLGBonusType bonus = null;
    private boolean isCooldown = false;

    private int kill;
    private int death;

    public HyriLGPlayer(HyriGame<?> game, Player player) {
        super(game, player);
    }

    public void setPlugin(HyriLaserGame plugin) {
        this.plugin = plugin;
    }

    public void giveGun(){
        this.plugin.getHyrame().getItemManager().giveItem(this.player, 0, HyriLGLaserGun.class);
    }

    public void cleanPlayer(){
        this.player.getInventory().clear();
        this.player.setGameMode(GameMode.ADVENTURE);
        this.player.setHealth(20.0F);
    }

    public void kill(){
        giveDeathArmor();
        this.player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20 * 5, 1, true, true));
        this.dead = true;

        new BukkitRunnable(){
            int i = 5;
            @Override
            public void run() {
                if(plugin.getGame().getState() != HyriGameState.ENDED) {
                    if (i == 0) {
                        new ActionBar(String.format(plugin.getHyrame().getLanguageManager().getValue(player, "player.death.subtitle.good"), i)).send(player);
                        this.cancel();
                    } else
                        new ActionBar(String.format(plugin.getHyrame().getLanguageManager().getValue(player, "player.death.subtitle"), i)).send(player);
                    --i;
                }else this.cancel();
            }
        }.runTaskTimer(this.plugin, 0L, 20L);

        Title.sendTitle(this.player, this.plugin.getHyrame().getLanguageManager().getValue(this.player, "player.death.title"), null, 1, 20*5, 1);
        this.player.sendMessage(this.plugin.getHyrame().getLanguageManager().getValue(this.player, "player.death.title"));

        new BukkitRunnable() {
            @Override
            public void run() {
                dead = false;
                playReviveSound(player);
                giveArmor();
            }
        }.runTaskLaterAsynchronously(this.plugin, 20L * 5);
    }

    public void playReviveSound(final Player player) {
        for (int i = 0; i < 5; i++) {
            float volume = 0.5F + i * 0.2F;
            int ticks = i * 3;
            Bukkit.getScheduler().runTaskLater(this.plugin, () -> player.playSound(player.getLocation(), Sound.DRINK, 1, volume), ticks);
        }
    }

    public void giveArmor(){
        giveArmor(this.team.getColor().getDyeColor().getColor());
    }

    public void giveInverseArmor(){
        giveArmor(this.plugin.getGame().getTeams().stream()
                .filter(hyriGameTeam -> hyriGameTeam.getColor() != team.getColor()).collect(Collectors.toList()).get(0)
                .getColor().getDyeColor().getColor());
    }

    public void giveDeathArmor(){
        giveArmor(Color.BLACK);
    }

    private void giveArmor(Color color){
        this.player.getInventory().setHelmet(new ItemBuilder(Material.LEATHER_HELMET).withLeatherArmorColor(color).build());
        this.player.getInventory().setChestplate(new ItemBuilder(Material.LEATHER_CHESTPLATE).withLeatherArmorColor(color).build());
    }

    public void clearArmor(){
        this.player.getInventory().setHelmet(null);
        this.player.getInventory().setChestplate(null);
    }

    public void setScoreboard(HyriLGScoreboard scoreboard) {
        this.scoreboard = scoreboard;
    }

    public void activeBonus(HyriLGBonusType bonusType){
        this.setBonus(bonusType);
        bonusType.active(this, this.plugin);
    }

    public void setBonus(HyriLGBonusType hyriLGBonusType) {
        this.bonus = hyriLGBonusType;
    }

    public void addKill(){
        this.kill += 1;
    }

    public void addDeath(){
        this.death += 1;
    }

    public void setCooldown(boolean isCooldown){
        this.isCooldown = isCooldown;
    }

    public boolean isLaserGun(ItemStack itemStack){
        return new ItemNBT(itemStack).hasTag(LASER_GUN_NBT);
    }

    public int getKills() {
        return kill;
    }

    public int getDeaths() {
        return death;
    }

    public boolean isCooldown(){
        return this.isCooldown;
    }

    public HyriLGScoreboard getScoreboard() {
        return this.scoreboard;
    }

    public int getPlayerPoints(){
        int kills = this.getKills();
        int deaths = this.getDeaths();
        kills *= 75;
        deaths *= 30;
        return Math.max(kills - deaths, 0);
    }

    public boolean hasBonus(){
        return this.getBonus() != null;
    }

    public HyriLGBonusType getBonus(){
        return this.bonus;
    }

}
