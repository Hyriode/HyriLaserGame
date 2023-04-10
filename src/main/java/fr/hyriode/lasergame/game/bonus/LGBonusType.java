package fr.hyriode.lasergame.game.bonus;

import fr.hyriode.lasergame.game.bonus.models.LGBonusInversion;
import fr.hyriode.lasergame.game.bonus.models.LGBonusInvisibility;
import fr.hyriode.lasergame.game.bonus.models.LGBonusShield;
import fr.hyriode.lasergame.game.bonus.models.LGBonusSpeed;

public enum LGBonusType {
    INVISIBILITY(new LGBonusInvisibility()),
    INVERSION(new LGBonusInversion()),
    SHOOT_FASTER(new LGBonus("shoot_faster", 10)),
    SPEED(new LGBonusSpeed()),
    SHIELD(new LGBonusShield()),
    ;

    private LGBonus bonus;

//    private final String name;
//    private final String languageName;
//    private final String languageDescription;
//    private final int timeSecond;
//
//    private final BiConsumer<LGGamePlayer, HyriLaserGame> before;
//    private final BiConsumer<LGGamePlayer, HyriLaserGame> after;

    LGBonusType(LGBonus bonus) {
        this.bonus = bonus;
    }

    public LGBonus get() {
        return this.bonus;
    }

    //    LGBonusType(String name, int timeSecond, BiConsumer<LGGamePlayer, HyriLaserGame> before, BiConsumer<LGGamePlayer, HyriLaserGame> after){
//        this.name = name;
//        this.timeSecond = timeSecond;
//        this.languageName = "bonus."+name+".name";
//        this.languageDescription = "bonus."+name+".description";
//        this.before = before;
//        this.after = after;
//    }
//
//    LGBonusType(String name, int timeSecond){
//        this(name, timeSecond, (player, plugin) -> {}, (player, plugin) -> {});
//    }

//    public String getName() {
//        return name;
//    }
//
//    public HyriLanguageMessage getLanguageName() {
//        return HyriLanguageMessage.get(languageName);
//    }
//
//    public HyriLanguageMessage getLanguageDescription() {
//        return HyriLanguageMessage.get(languageDescription);
//    }
//
//    public int getTimeSecond() {
//        return timeSecond * 20;
//    }

//    public void active(LGGamePlayer player, HyriLaserGame plugin){
//        Player pl = player.getPlayer();
//        if(player.hasBonus()){
//            int time = this.getTimeSecond();
//            this.before.accept(player, plugin);
//            new BukkitRunnable(){
//                int i = 0;
//                @Override
//                public void run() {
//                    if(!player.isDead() && i < time && player.hasBonus()) {
//                        ++i;
//                        new ActionBar(ChatColor.DARK_AQUA + "Bonus: " + ChatColor.WHITE + player.getBonus().getLanguageName().getValue(pl) + " (" + (time / 20 - i / 20) + "s)").send(pl);
//                        return;
//                    }
//                    after.accept(player, plugin);
//                    new ActionBar(ChatColor.RED + "").send(pl);
//                    pl.getActivePotionEffects().forEach(potionEffect -> pl.removePotionEffect(potionEffect.getType()));
//                    pl.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 99999*20, 0));
//                    player.setBonus(null);
//                    cancel();
//                }
//            }.runTaskTimer(plugin, 0L, 1L);
//        }
//    }

}
