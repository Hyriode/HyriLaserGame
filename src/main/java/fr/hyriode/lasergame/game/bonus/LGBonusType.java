package fr.hyriode.lasergame.game.bonus;

import fr.hyriode.api.language.HyriLanguageMessage;
import fr.hyriode.hyrame.actionbar.ActionBar;
import fr.hyriode.hyrame.utils.PlayerUtil;
import fr.hyriode.lasergame.HyriLaserGame;
import fr.hyriode.lasergame.game.bonus.effect.SphereEffect;
import fr.hyriode.lasergame.game.player.LGGamePlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.function.BiConsumer;

public enum LGBonusType {
    INVISIBILITY("invisibility", 5, (player, plugin) -> {
        Player pl = player.getPlayer();
        pl.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 20*10, 0, true, true));
        plugin.getGame().getTeams().forEach(team -> {
            if(!team.getName().equals(player.getTeam().getName()))
                team.getPlayers().forEach(p -> {
                    PlayerUtil.hideArmor(pl, p.getPlayer());
                });
        });
    }, (player, plugin) -> {
        plugin.getGame().getTeams().forEach(team -> {
            if(!team.getName().equals(player.getTeam().getName()))
                team.getPlayers().forEach(p -> {
                    PlayerUtil.showArmor(player.getPlayer(), p.getPlayer());
                });
        });
    }),
    INVERSION("inversion", 10, (player, __) -> player.giveInverseArmor(),
            (player, __) -> player.giveArmor()),
    SHOOT_FASTER("shoot_faster", 10),
    SPEED("speed", 10, (player, __) -> player.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20*10, 0, true, true)),
            (player, __) -> player.getPlayer().removePotionEffect(PotionEffectType.SPEED)),
    SHIELD("shield", 5, (player, plugin) -> {
        SphereEffect sphereEffect = new SphereEffect(plugin, player);
        sphereEffect.start();
    }, (player, plugin) -> {
    })
    ;

    private final String name;
    private final String languageName;
    private final String languageDescription;
    private final int timeSecond;

    private final BiConsumer<LGGamePlayer, HyriLaserGame> before;
    private final BiConsumer<LGGamePlayer, HyriLaserGame> after;

    LGBonusType(String name, int timeSecond, BiConsumer<LGGamePlayer, HyriLaserGame> before, BiConsumer<LGGamePlayer, HyriLaserGame> after){
        this.name = name;
        this.timeSecond = timeSecond;
        this.languageName = "bonus."+name+".name";
        this.languageDescription = "bonus."+name+".description";
        this.before = before;
        this.after = after;
    }

    LGBonusType(String name, int timeSecond){
        this(name, timeSecond, (player, plugin) -> {}, (player, plugin) -> {});
    }

    public String getName() {
        return name;
    }

    public HyriLanguageMessage getLanguageName() {
        return HyriLanguageMessage.get(languageName);
    }

    public HyriLanguageMessage getLanguageDescription() {
        return HyriLanguageMessage.get(languageDescription);
    }

    public int getTimeSecond() {
        return timeSecond * 20;
    }

    public void active(LGGamePlayer player, HyriLaserGame plugin){
        Player pl = player.getPlayer();
        if(player.hasBonus()){
            int time = this.getTimeSecond();
            this.before.accept(player, plugin);
            new BukkitRunnable(){
                int i = 0;
                @Override
                public void run() {
                    if(!player.isDead() && i < time && player.hasBonus()) {
                        ++i;
                        new ActionBar(ChatColor.DARK_AQUA + "Bonus: " + ChatColor.WHITE + player.getBonus().getLanguageName().getValue(pl) + " (" + (time / 20 - i / 20) + "s)").send(pl);
                        return;
                    }
                    after.accept(player, plugin);
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
