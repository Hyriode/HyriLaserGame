package fr.hyriode.lasergame.game.bonus;

import fr.hyriode.hyrame.actionbar.ActionBar;
import fr.hyriode.hyrame.language.HyriLanguageMessage;
import fr.hyriode.hyrame.utils.PlayerUtil;
import fr.hyriode.lasergame.HyriLaserGame;
import fr.hyriode.lasergame.game.player.LGGamePlayer;
import fr.hyriode.lasergame.utils.TriConsumer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public enum LGBonusType {
    INVISIBILITY("invisibility", 5, (player, plugin, time) -> {
        Player pl = player.getPlayer();
        pl.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 20*10, 0, true, true));
        plugin.getGame().getTeams().forEach(team -> {
            if(!team.getName().equals(player.getTeam().getName()))
                team.getPlayers().forEach(p -> {
                    PlayerUtil.hideArmor(pl, p.getPlayer());
                });
        });
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            plugin.getGame().getTeams().forEach(team -> {
                if(!team.getName().equals(player.getTeam().getName()))
                    team.getPlayers().forEach(p -> {
                        PlayerUtil.showArmor(pl, p.getPlayer());
                    });
            });
        }, time);
    }),
    INVERSION("inversion", 10, (player, plugin, time) -> {
        player.giveInverseArmor();
        new BukkitRunnable(){
            int i = 0;
            @Override
            public void run() {
                if(i > time*20){
                    this.cancel();
                    if(player.isDead()) return;
                }
                player.giveArmor();
                ++i;
            }
        }.runTaskTimer(plugin, 0, 1);
    }),
    SHOOT_FASTER("shoot_faster", 10),
    SPEED("speed", 10, (player, laserGame, integer) -> player.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20*10, 0, true, true))),
    SHIELD("shield", 5)
    ;

    private final String name;
    private final String languageName;
    private final String languageDescription;
    private final int timeSecond;

    private final TriConsumer<LGGamePlayer, HyriLaserGame, Integer> active;

    LGBonusType(String name, int timeSecond, TriConsumer<LGGamePlayer, HyriLaserGame, Integer> active){
        this.name = name;
        this.timeSecond = timeSecond;
        this.languageName = "bonus."+name+".name";
        this.languageDescription = "bonus."+name+".description";
        this.active = active;
    }

    LGBonusType(String name, int timeSecond){
        this(name, timeSecond, (player, laserGame, integer) -> {});
    }

    public String getName() {
        return name;
    }

    public HyriLanguageMessage getLanguageName() {
        return HyriLaserGame.getLanguageManager().getMessage(languageName);
    }

    public HyriLanguageMessage getLanguageDescription() {
        return HyriLaserGame.getLanguageManager().getMessage(languageDescription);
    }

    public int getTimeSecond() {
        return timeSecond * 20;
    }

    public void active(LGGamePlayer player, HyriLaserGame plugin){
        Player pl = player.getPlayer();
        if(player.hasBonus()){
            int time = this.getTimeSecond();
            this.active.accept(player, plugin, time);
            new BukkitRunnable(){
                int i = 0;
                @Override
                public void run() {
                    if(!player.isDead() && i < time && player.hasBonus()) {
                        ++i;
                        new ActionBar(ChatColor.DARK_AQUA + "Bonus: " + ChatColor.WHITE + player.getBonus().getLanguageName().getForPlayer(pl) + " (" + (time / 20 - i / 20) + "s)").send(pl);
                        return;
                    }
                    new ActionBar(ChatColor.RED + "").send(pl);
                    pl.getActivePotionEffects().forEach(potionEffect -> pl.removePotionEffect(potionEffect.getType()));
                    pl.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 99999*20, 0));
                    player.setBonus(null);
                    cancel();
                }
            }.runTaskTimer(plugin, 0L, 1L);
        }
    }

}
