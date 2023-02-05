package fr.hyriode.lasergame.game.bonus.effect;

import fr.hyriode.lasergame.HyriLaserGame;
import fr.hyriode.lasergame.game.LGGame;
import fr.hyriode.lasergame.game.player.LGGamePlayer;
import fr.hyriode.lasergame.utils.RandomUtils;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import xyz.xenondevs.particle.ParticleBuilder;
import xyz.xenondevs.particle.ParticleEffect;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class SphereEffect {

    public final ParticleEffect particle = ParticleEffect.REDSTONE;
    public final double radius = 1;
    public final double yOffset = 1;
    public final int particlesDisplay = 50;

    private final LGGamePlayer player;
    private final HyriLaserGame plugin;
    private final int seconds;


    public SphereEffect(HyriLaserGame plugin, LGGamePlayer player, int seconds) {
        this.plugin = plugin;
        this.player = player;
        this.seconds = seconds;
    }

    public void start() {
        final LGGame game = this.plugin.getGame();

        List<Player> players = new ArrayList<>();

        for (LGGamePlayer gamePlayer : game.getPlayers()) {
            if (gamePlayer.getUniqueId() != player.getUniqueId()) {
                players.add(gamePlayer.getPlayer());
            }
        }

        new BukkitRunnable() {
            int index = 0;

            @Override
            public void run() {
                if (index % 3 == 0) {
                    Location location = player.getPlayer().getLocation();
                    location.add(0, yOffset, 0);

                    for (int i = 0; i < particlesDisplay; i++) {
                        Vector v = RandomUtils.getRandomVector().multiply(radius);
                        new ParticleBuilder(particle, location.add(v))
                                .setColor(Color.BLUE)
                                .display(players);
                        location.subtract(v);
                    }
                }

                if (index == seconds * 20) {
                    cancel();
                }

                index++;
            }
        }.runTaskTimer(this.plugin, 0, 1);
    }

}
