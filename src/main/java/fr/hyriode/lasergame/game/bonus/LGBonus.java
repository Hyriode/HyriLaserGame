package fr.hyriode.lasergame.game.bonus;

import fr.hyriode.api.language.HyriLanguageMessage;
import fr.hyriode.hyrame.actionbar.ActionBar;
import fr.hyriode.lasergame.HyriLaserGame;
import fr.hyriode.lasergame.game.player.LGGamePlayer;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.function.BiConsumer;

public class LGBonus {

    private final String name;
    private final int timeSecond;

    protected BiConsumer<LGGamePlayer, HyriLaserGame> before;
    protected BiConsumer<LGGamePlayer, HyriLaserGame> after;

    public LGBonus(String name, int timeSecond, BiConsumer<LGGamePlayer, HyriLaserGame> before, BiConsumer<LGGamePlayer, HyriLaserGame> after){
        this.name = name;
        this.timeSecond = timeSecond;
        this.before = before;
        this.after = after;
    }

    public LGBonus(String name, int timeSecond){
        this(name, timeSecond, (player, plugin) -> {}, (player, plugin) -> {});
    }

    public String getName() {
        return name;
    }

    public HyriLanguageMessage getLanguageName() {
        return HyriLanguageMessage.get("bonus."+name+".name");
    }

    public HyriLanguageMessage getLanguageDescription() {
        return HyriLanguageMessage.get("bonus."+name+".description");
    }

    public int getTimeSecond() {
        return timeSecond * 20;
    }

    public void active(LGGamePlayer player, HyriLaserGame plugin){
        if(player != null && player.hasBonus()){
            Player pl = player.getPlayer();
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
//                    pl.getActivePotionEffects().forEach(potionEffect -> pl.removePotionEffect(potionEffect.getType()));
                    pl.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 99999*20, 0));
                    player.setBonus(null);
                    cancel();
                }
            }.runTaskTimer(plugin, 0L, 1L);
        }
    }


}
