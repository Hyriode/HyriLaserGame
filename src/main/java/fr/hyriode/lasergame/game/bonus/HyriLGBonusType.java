package fr.hyriode.lasergame.game.bonus;

import fr.hyriode.hyrame.actionbar.ActionBar;
import fr.hyriode.hyrame.language.HyriLanguageMessage;
import fr.hyriode.lasergame.HyriLaserGame;
import fr.hyriode.lasergame.game.player.HyriLGPlayer;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public enum HyriLGBonusType {
    INVISIBILITY("invisibility", 10),
    INVERSION("inversion", 10),
    SHOOT_FASTER("shoot_faster", 10),
    SPEED("speed", 10),
    SHIELD("shield", 5)
    ;

    private final String name;
    private final String languageName;
    private final String languageDescription;
    private final int timeSecond;

    HyriLGBonusType(String name, int timeSecond){
        this.name = name;
        this.timeSecond = timeSecond;
        this.languageName = "bonus."+name+".name";
        this.languageDescription = "bonus."+name+".description";
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

    public void active(HyriLGPlayer player, HyriLaserGame plugin){
        if(player.hasBonus()){
            long time = player.getBonus().getTimeSecond();
            switch (player.getBonus()){
                case SPEED:
                    player.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20*10, 0, true, true));
                    break;
                case INVISIBILITY:
                    player.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 20*10, 0, true, true));
                    player.clearArmor();
                    new BukkitRunnable(){
                        @Override
                        public void run() {
                            player.giveArmor();
                        }
                    }.runTaskLater(plugin, time);
                    break;
                case INVERSION:
                    player.giveInverseArmor();
                    new BukkitRunnable(){
                        @Override
                        public void run() {
                            player.giveArmor();
                        }
                    }.runTaskLater(plugin, time);
                    break;
            }
            new BukkitRunnable(){
                int i = 0;
                @Override
                public void run() {
                    if(!player.isDead() && i < time && player.hasBonus()) {
                        ++i;
                        new ActionBar("Bonus: " + player.getBonus().getLanguageName().getForPlayer(player.getPlayer()) + " (" + (time / 20 - i / 20) + "s)").send(player.getPlayer());
                        System.out.println(i + " uiui");
                    }else{
                        System.out.println("ET OUI");
                        player.getPlayer().getActivePotionEffects().forEach(potionEffect -> player.getPlayer().removePotionEffect(potionEffect.getType()));
                        player.setBonus(null);
//                        player.getScoreboard().update();
                        cancel();
                    }
                }
            }.runTaskTimer(plugin, 0L, 1L);
        }
    }

}
