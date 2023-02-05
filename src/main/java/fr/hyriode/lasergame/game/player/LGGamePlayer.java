package fr.hyriode.lasergame.game.player;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.language.HyriLanguageMessage;
import fr.hyriode.api.player.IHyriPlayer;
import fr.hyriode.hyrame.actionbar.ActionBar;
import fr.hyriode.hyrame.game.HyriGamePlayer;
import fr.hyriode.hyrame.game.HyriGameState;
import fr.hyriode.hyrame.game.event.player.HyriGameDeathEvent;
import fr.hyriode.hyrame.item.ItemBuilder;
import fr.hyriode.hyrame.item.ItemNBT;
import fr.hyriode.hyrame.utils.LocationWrapper;
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

import java.util.ArrayList;
import java.util.List;
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
        this.player.teleport(this.getRandomSpawn());
        this.giveDeathArmor();
        this.player.playSound(this.player.getLocation(), Sound.VILLAGER_NO, 1f, 1f);
        this.setDead(HyriGameDeathEvent.Reason.PLAYERS, new ArrayList<>());
        this.player.sendMessage(HyriLanguageMessage.get("player.death.title").getValue(this.player));
    }

    private Location getRandomSpawn() {
        final List<LocationWrapper> spawns = this.plugin.getConfiguration().getSpawnLocations();
        return spawns.get(ThreadLocalRandom.current().nextInt(spawns.size())).asBukkit();
    }

    public void respawn() {
        new ActionBar(HyriLanguageMessage.get("player.death.subtitle.good").getValue(player)).send(player);
        this.giveArmor();
        this.setNotDead();
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

        //TODO: dont forget to edit this
        LGBonusType bonusType = LGBonusType.SHIELD;//Arrays.asList(LGBonusType.values()).get(ThreadLocalRandom.current().nextInt(LGBonusType.values().length));

        if (this.hasBonus()){
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
