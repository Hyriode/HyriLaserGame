package fr.hyriode.lasergame.game.player;

import fr.hyriode.api.language.HyriLanguage;
import fr.hyriode.api.language.HyriLanguageMessage;
import fr.hyriode.api.player.IHyriPlayer;
import fr.hyriode.hyrame.actionbar.ActionBar;
import fr.hyriode.hyrame.game.HyriGamePlayer;
import fr.hyriode.hyrame.game.HyriGameState;
import fr.hyriode.hyrame.game.event.player.HyriGameDeathEvent;
import fr.hyriode.hyrame.item.ItemBuilder;
import fr.hyriode.hyrame.item.ItemNBT;
import fr.hyriode.hyrame.title.Title;
import fr.hyriode.hyrame.utils.BroadcastUtil;
import fr.hyriode.lasergame.HyriLaserGame;
import fr.hyriode.lasergame.api.player.LGPlayerStatistics;
import fr.hyriode.lasergame.game.bonus.LGBonus;
import fr.hyriode.lasergame.game.bonus.LGBonusEntity;
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

    private LGBonus bonus = null;
    private boolean cooldown = false;

    private int kills;
    private int deaths;
    private int bestKillStreak;
    private int points;

    private int killStreak;

    private final static int TIME_DEATH = 5;

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

    public void kill(LGGamePlayer killer){
        if(player == null || !player.isOnline()) return;
        this.dead();
        BroadcastUtil.broadcast(p -> HyriLanguageMessage.get("player.death.chat").getValue(p)
                .replace("%victim%", this.getTeam().getColor().getChatColor() + this.player.getName())
                .replace("%killer%", killer.getTeam().getColor().getChatColor() + killer.getPlayer().getName()));
    }

    public void dead() {
        this.giveDeathArmor();
        this.player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20 * TIME_DEATH, 2, true, true), true);
        this.player.playSound(this.player.getLocation(), Sound.VILLAGER_NO, 1f, 1f);
        this.setDead(HyriGameDeathEvent.Reason.PLAYERS, new ArrayList<>());

        final HyriLanguageMessage dead = new HyriLanguageMessage("")
                .addValue(HyriLanguage.EN, "DEAD")
                .addValue(HyriLanguage.FR, "MORT");

        new BukkitRunnable(){
            int i = TIME_DEATH;
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

        Bukkit.getScheduler().runTaskLaterAsynchronously(this.plugin, () -> {
            if(this.plugin.getGame().getState() != HyriGameState.ENDED) {
                this.setNotDead();
                this.playReviveSound(player);
                this.giveArmor();
            }
        }, 20L * TIME_DEATH);
    }

    public void playReviveSound(final Player player) {
        for (int i = 0; i < 5; i++) {
            float volume = 0.5F + i * 0.2F;
            int ticks = i * 3;
            new BukkitRunnable() {
                @Override
                public void run() {
                    if(player == null || !player.isOnline()) {
                        this.cancel();
                        return;
                    }
                    player.playSound(player.getLocation(), Sound.DRINK, 1, volume);
                }
            }.runTaskLater(this.plugin, ticks);
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
        if(player == null || !player.isOnline()) return;
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

        if(!armorStand.hasMetadata(LGBonusEntity.getIsBonusMetadata())) return;

        LGBonus bonusType = Arrays.asList(LGBonusType.values()).get(ThreadLocalRandom.current().nextInt(LGBonusType.values().length)).get();

        this.activeSpecificBonus(bonusType, armorStand);
    }

    public void activeSpecificBonus(LGBonus bonusType, ArmorStand armorStand){
        if(this.hasBonus()){
            if(this.enableBonus) return;
            this.enableBonus = true;

            this.getPlayer().sendMessage(ChatColor.RED + HyriLanguageMessage.get("bonus.pickup.already").getValue(this.getPlayer()));
            return;
        }

        this.enableBonus = false;

        if(this.isDead()) return;

        if(armorStand != null) {
            armorStand.remove();
        }

        this.setBonus(bonusType);
        bonusType.active(this, this.plugin);

        this.player.playSound(player.getLocation(), Sound.LEVEL_UP, 1, 3F);

        new ActionBar(HyriLanguageMessage.get("bonus.pickup.title").getValue(this.getPlayer()) + " " + ChatColor.RESET + bonusType.getLanguageName().getValue(this.getPlayer())).send(this.getPlayer());
        this.getPlayer().sendMessage("   ");
        this.getPlayer().sendMessage(ChatColor.DARK_AQUA + HyriLanguageMessage.get("bonus.pickup.title").getValue(this.getPlayer()) + " " + ChatColor.RESET + bonusType.getLanguageName().getValue(this.getPlayer()));
        this.getPlayer().sendMessage(ChatColor.DARK_AQUA + HyriLanguageMessage.get("bonus.pickup.description").getValue(this.getPlayer()) + ChatColor.GRAY + bonusType.getLanguageDescription().getValue(this.getPlayer()));

        if(armorStand != null) {
            LGBonusEntity bonus = this.plugin.getGame().getBonus(armorStand.getUniqueId());
            if (bonus != null)
                bonus.respawn();
        }
    }

    public void setScoreboard(LGScoreboard scoreboard) {
        this.scoreboard = scoreboard;
    }

    public void setBonus(LGBonus hyriLGBonusType) {
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

    public int getTotalPoints(){
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

    public LGBonus getBonus(){
        return this.bonus;
    }

    public void setKillStreak(int killStreak) {
        this.killStreak = killStreak;
    }

    public void updateStatistics(boolean isWinner) {
        LGPlayerStatistics playerStatistics = this.getStatistics();
        LGPlayerStatistics.Data statistics = playerStatistics.getData(this.plugin.getGame().getType());
        statistics.addKills(this.kills);
        statistics.addDeaths(this.deaths);
        if(isWinner){
            statistics.addWins(1);
            statistics.addCurrentWinStreak(1);
            if(statistics.getCurrentWinStreak() > statistics.getBestWinStreak()){
                statistics.setBestWinStreak(statistics.getCurrentWinStreak());
            }
        }else {
            statistics.setCurrentWinStreak(0);
        }
        statistics.addPlayedGame();

        playerStatistics.update(this.asHyriPlayer());
    }

    private LGPlayerStatistics getStatistics() {
        IHyriPlayer player = this.asHyriPlayer();
        LGPlayerStatistics playerStatistics = player.getStatistics().read("lasergame", new LGPlayerStatistics());

        if(playerStatistics == null) {
            playerStatistics = new LGPlayerStatistics();
            playerStatistics.update(player);
        }

        return playerStatistics;
    }

    public LGPlayerStatistics.Data getAllStatistics() {
        return this.getStatistics().getAllData();
    }

    public void spawn() {
        final LGScoreboard scoreboard = new LGScoreboard(this.plugin, this.getPlayer());

        this.setScoreboard(scoreboard);
        scoreboard.show();

        this.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 99999*20, 0));
        this.cleanPlayer();
        this.giveGun();
        this.giveArmor();
    }
}
