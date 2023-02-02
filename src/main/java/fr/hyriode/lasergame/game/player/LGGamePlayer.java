package fr.hyriode.lasergame.game.player;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.language.HyriLanguage;
import fr.hyriode.api.language.HyriLanguageMessage;
import fr.hyriode.api.player.IHyriPlayer;
import fr.hyriode.hyrame.actionbar.ActionBar;
import fr.hyriode.hyrame.game.HyriGame;
import fr.hyriode.hyrame.game.HyriGamePlayer;
import fr.hyriode.hyrame.game.HyriGameState;
import fr.hyriode.hyrame.game.event.player.HyriGameDeathEvent;
import fr.hyriode.hyrame.item.ItemBuilder;
import fr.hyriode.hyrame.item.ItemNBT;
import fr.hyriode.hyrame.title.Title;
import fr.hyriode.lasergame.HyriLaserGame;
import fr.hyriode.lasergame.api.player.HyriLGPlayer;
import fr.hyriode.lasergame.game.bonus.LGBonus;
import fr.hyriode.lasergame.game.bonus.LGBonusType;
import fr.hyriode.lasergame.game.item.LGLaserGun;
import fr.hyriode.lasergame.game.scoreboard.LGScoreboard;
import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class LGGamePlayer extends HyriGamePlayer {

    private HyriLaserGame plugin;

    private LGScoreboard scoreboard;

    private LGBonusType bonus = null;
    private boolean cooldown = false;

    private int kills;
    private int deaths;
    private int bestKillStreak;
    private int points;

    private int killStreak;

    public LGGamePlayer(Player player) {
        super(player);
    }

    public LGGamePlayer setPlugin(HyriLaserGame plugin) {
        this.plugin = plugin;
        return this;
    }

    public void giveGun(){
        this.plugin.getHyrame().getItemManager().giveItem(this.player, 0, LGLaserGun.class);
        this.getPlayer().getInventory().setHeldItemSlot(0);
    }

    public void cleanPlayer(){
        this.player.setGameMode(GameMode.ADVENTURE);
        this.player.setHealth(20.0F);
    }

    public void kill(){
        int timeDeath = 5;

        this.giveDeathArmor();
        this.player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20 * timeDeath, 1, true, true));
        this.player.playSound(this.player.getLocation(), Sound.VILLAGER_NO, 1f, 1f);
        this.setDead(HyriGameDeathEvent.Reason.PLAYERS, new ArrayList<>());

        final HyriLanguageMessage dead = new HyriLanguageMessage("")
                .addValue(HyriLanguage.EN, "DEAD")
                .addValue(HyriLanguage.FR, "MORT");

        new BukkitRunnable(){
            int i = timeDeath;
            @Override
            public void run() {
                if(plugin.getGame().getState() != HyriGameState.ENDED) {
                    if (i == 0) {
                        Title.sendTitle(player, " ", null, 1, 1, 1);
                        new ActionBar(String.format(HyriLanguageMessage.get("player.death.subtitle.good").getValue(player), i)).send(player);
                        this.cancel();
                        return;
                    }
                    Title.sendTitle(player, ChatColor.RED + " " + dead.getValue(player), String.format(HyriLanguageMessage.get("player.death.subtitle").getValue(player), i), 1, 20, 1);
                    --i;
                }else this.cancel();
            }
        }.runTaskTimer(this.plugin, 0L, 20L);

        this.player.sendMessage(HyriLanguageMessage.get("player.death.title").getValue(this.player));

        Bukkit.getScheduler().runTaskLaterAsynchronously(this.plugin, () -> {
            if(this.plugin.getGame().getState() != HyriGameState.ENDED) {
                this.setNotDead();
                this.playReviveSound(player);
                this.giveArmor();
            }
        }, 20L * timeDeath);
    }

    public void playReviveSound(final Player player) {
        for (int i = 0; i < 5; i++) {
            float volume = 0.5F + i * 0.2F;
            int ticks = i * 3;
            Bukkit.getScheduler().runTaskLater(this.plugin, () -> player.playSound(player.getLocation(), Sound.DRINK, 1, volume), ticks);
        }
    }

    public void giveArmor(){
        giveArmor(this.getTeam().getColor().getDyeColor().getColor());
    }

    public void giveInverseArmor(){
        this.giveArmor(this.plugin.getGame().getTeams().stream()
                .filter(hyriGameTeam -> hyriGameTeam.getColor() != this.getTeam().getColor()).collect(Collectors.toList()).get(0)
                .getColor().getDyeColor().getColor());
    }

    public void giveDeathArmor(){
        giveArmor(Color.BLACK);
    }

    private void giveArmor(Color color){
        this.player.getInventory().setHelmet(
                new ItemBuilder(Material.LEATHER_HELMET)
                        .withLeatherArmorColor(color)
                        .build());
        this.player.getInventory().setChestplate(
                new ItemBuilder(Material.LEATHER_CHESTPLATE)
                        .withLeatherArmorColor(color)
                        .build());
    }

    public void clearArmor(){
        this.player.getInventory().setHelmet(null);
        this.player.getInventory().setChestplate(null);
    }

    private boolean enableBonus = false;

    public void activeBonus(ArmorStand armorStand){
        if(this.plugin.getGame().getState() == HyriGameState.ENDED) return;

        if(!armorStand.hasMetadata(LGBonus.getIsBonusMetadata())) return;

        LGBonusType bonusType = LGBonusType.SHIELD;//Arrays.asList(LGBonusType.values()).get(ThreadLocalRandom.current().nextInt(LGBonusType.values().length));

        if(this.hasBonus()){
            if(this.enableBonus) return;
            this.enableBonus = true;

            this.getPlayer().sendMessage(ChatColor.RED + HyriLanguageMessage.get("bonus.pickup.already").getValue(this.getPlayer()));
            return;
        }

        this.enableBonus = false;

        if(this.isDead()) return;

        armorStand.remove();

        this.setBonus(bonusType);
        bonusType.active(this, this.plugin);

        this.player.playSound(player.getLocation(), Sound.LEVEL_UP, 1, 3F);

        new ActionBar(HyriLanguageMessage.get("bonus.pickup.title").getValue(this.getPlayer()) + " " + ChatColor.RESET + bonusType.getLanguageName().getValue(this.getPlayer())).send(this.getPlayer());
        this.getPlayer().sendMessage("   ");
        this.getPlayer().sendMessage(ChatColor.DARK_AQUA + HyriLanguageMessage.get("bonus.pickup.title").getValue(this.getPlayer()) + " " + ChatColor.RESET + bonusType.getLanguageName().getValue(this.getPlayer()));
        this.getPlayer().sendMessage(ChatColor.DARK_AQUA + HyriLanguageMessage.get("bonus.pickup.description").getValue(this.getPlayer()) + ChatColor.GRAY + bonusType.getLanguageDescription().getValue(this.getPlayer()));

        LGBonus bonus = this.plugin.getGame().getBonus(armorStand.getUniqueId());
        if(bonus != null)
            bonus.respawn();
    }

    public void setScoreboard(LGScoreboard scoreboard) {
        this.scoreboard = scoreboard;
    }

    public void setBonus(LGBonusType hyriLGBonusType) {
        this.bonus = hyriLGBonusType;
    }

    public void addKill(){
        this.kills += 1;
    }

    public void addDeath(){
        this.deaths += 1;
    }

    public void addKillStreak(){
        this.killStreak += 1;
    }

    public int getKillStreak() {
        return killStreak;
    }

    public void setCooldown(boolean cooldown){
        this.cooldown = cooldown;
    }

    public int getBestKillStreak() {
        return bestKillStreak;
    }

    public void setBestKillStreak() {
        if(this.killStreak != 0 && this.killStreak % 5 == 0) {
            this.points += 50;
        }
        if(this.killStreak > this.bestKillStreak)
            this.bestKillStreak = this.killStreak;
    }

    public boolean isLaserGun(ItemStack itemStack){
        return new ItemNBT(itemStack).hasTag(LGLaserGun.LASER_GUN_NBT);
    }

    public int getKills() {
        return kills;
    }

    public int getDeaths() {
        return deaths;
    }

    public int getPoints() {
        return points;
    }

    public int getAllPoints(){
        int kills = 0;
        int deaths = 0;
        int points = 0;

        kills += this.getKills();
        deaths += this.getDeaths();
        points += this.getPoints();

        kills *= 75;
        deaths *= 30;

        return Math.max(kills - deaths, 0) + points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public void addPoints(int points){
        this.points += points;
    }

    public boolean isCooldown(){
        return this.cooldown;
    }

    public LGScoreboard getScoreboard() {
        return this.scoreboard;
    }

    public int getPlayerPoints(){
        int kills = this.getKills();
        int deaths = this.getDeaths();
        kills *= 75;
        deaths *= 30;
        return Math.max(kills - deaths, 0) + this.points;
    }

    public boolean hasBonus(){
        return this.getBonus() != null;
    }

    public LGBonusType getBonus(){
        return this.bonus;
    }

    public void setKillStreak(int killStreak) {
        this.killStreak = killStreak;
    }

    public HyriLGPlayer getAccount() {
        IHyriPlayer player = HyriAPI.get().getPlayerManager().getPlayer(this.getUniqueId());
        HyriLGPlayer lgPlayer = player.getStatistics("lasergame", HyriLGPlayer.class);
        if(lgPlayer != null){
            return lgPlayer;
        }
        return new HyriLGPlayer();
    }

    public HyriLGPlayer getStatistics() {
        IHyriPlayer player = HyriAPI.get().getPlayerManager().getPlayer(this.getUniqueId());
        HyriLGPlayer lgPlayer = player.getStatistics("lasergame", HyriLGPlayer.class);
        if(lgPlayer != null){
            return lgPlayer;
        }
        return new HyriLGPlayer();
    }
}
