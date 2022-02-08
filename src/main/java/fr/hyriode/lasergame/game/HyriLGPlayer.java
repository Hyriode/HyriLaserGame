package fr.hyriode.lasergame.game;

import fr.hyriode.hyrame.actionbar.ActionBar;
import fr.hyriode.hyrame.game.HyriGame;
import fr.hyriode.hyrame.game.HyriGamePlayer;
import fr.hyriode.hyrame.item.ItemBuilder;
import fr.hyriode.hyrame.item.ItemNBT;
import fr.hyriode.hyrame.language.HyriLanguageMessage;
import fr.hyriode.hyrame.title.Title;
import fr.hyriode.lasergame.HyriLaserGame;
import fr.hyriode.lasergame.game.scoreboard.HyriLGScoreboard;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;

public class HyriLGPlayer extends HyriGamePlayer {

    private static final String LASER_GUN_NBT = "LaserGun";

    private HyriLaserGame plugin;

    private HyriLGScoreboard scoreboard;

    private int kill;
    private int death;

    public HyriLGPlayer(HyriGame<?> game, Player player) {
        super(game, player);

    }

    void setPlugin(HyriLaserGame plugin) {
        this.plugin = plugin;
    }

    public void giveGun(){
        this.plugin.getHyrame().getItemManager().giveItem(this.player, 0, HyriLGLaserGun.class);
    }

    public boolean isLaserGun(ItemStack itemStack){
        return new ItemNBT(itemStack).hasTag(LASER_GUN_NBT);
    }

    public void cleanPlayer(){
        this.player.getInventory().clear();
        this.player.setGameMode(GameMode.ADVENTURE);
        this.player.setHealth(20.0F);
    }

    public int getKills() {
        return kill;
    }

    public int getDeaths() {
        return death;
    }

    public void addKill(){
        this.kill += 1;
    }

    public void addDeath(){
        this.death += 1;
    }

    public void kill(){
        giveDeathArmor();
        this.dead = true;
        new BukkitRunnable(){
            int index = 5;
            @Override
            public void run() {
                if(index == 0) {
                    new ActionBar(String.format(plugin.getHyrame().getLanguageManager().getValue(player, "player.death.subtitle.good"), index)).send(player);
                    this.cancel();
                }else
                new ActionBar(String.format(plugin.getHyrame().getLanguageManager().getValue(player, "player.death.subtitle"), index)).send(player);
                --index;
            }
        }.runTaskTimer(this.plugin, 0L, 20L);
        Title.sendTitle(this.player, this.plugin.getHyrame().getLanguageManager().getValue(this.player, "player.death.title"), null, 1, 20*5, 1);
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
        giveArmor(this.player.getInventory(), this.team.getColor().getDyeColor().getColor());
    }

    public void giveDeathArmor(){
        giveArmor(this.player.getInventory(), Color.BLACK);
    }

    private void giveArmor(PlayerInventory inventory, Color color){
        inventory.setHelmet(new ItemBuilder(Material.LEATHER_HELMET).withLeatherArmorColor(color).build());
        inventory.setChestplate(new ItemBuilder(Material.LEATHER_CHESTPLATE).withLeatherArmorColor(color).build());
    }

    public void setScoreboard(HyriLGScoreboard scoreboard) {
        this.scoreboard = scoreboard;
    }

    public HyriLGScoreboard getScoreboard() {
        return this.scoreboard;
    }
}
