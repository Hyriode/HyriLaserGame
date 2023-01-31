package fr.hyriode.lasergame.game.bonus.effect;

import fr.hyriode.hyrame.game.HyriGameSpectator;
import fr.hyriode.lasergame.HyriLaserGame;
import fr.hyriode.lasergame.game.LGGame;
import fr.hyriode.lasergame.game.player.LGGamePlayer;
import fr.hyriode.lasergame.utils.RandomUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import xyz.xenondevs.particle.ParticleBuilder;
import xyz.xenondevs.particle.ParticleEffect;

import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class SphereEffect {

    public final ParticleEffect particle = ParticleEffect.REDSTONE;
    public final double radius = 1;
    public final double yOffset = 1;
    public final int particlesDisplay = 50;

    private final LGGamePlayer player;
    private final HyriLaserGame plugin;
    private BukkitTask timer;


    public SphereEffect(HyriLaserGame plugin, LGGamePlayer player) {
        this.plugin = plugin;
        this.player = player;
    }

    public void start() {
        LGGame game = this.plugin.getGame();
        this.timer = Bukkit.getScheduler().runTaskTimer(this.plugin, () -> {
            List<Player> players = game.getPlayers().stream()
                    .map(HyriGameSpectator::getPlayer)
                    .filter(player -> !player.getUniqueId().equals(this.player.getUniqueId()))
                    .collect(Collectors.toList());
            Location location = this.player.getPlayer().getLocation();
            location.add(0, this.yOffset, 0);
            for (int i = 0; i < this.particlesDisplay; i++) {
                Vector v = RandomUtils.getRandomVector().multiply(this.radius);
                new ParticleBuilder(this.particle, location.add(v))
                        .setColor(Color.BLUE)
                        .display(players);
                location.subtract(v);
            }
        }, 0, 3);
    }

    public void stop() {
        this.timer.cancel();
    }

}
