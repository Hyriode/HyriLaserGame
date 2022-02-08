package fr.hyriode.lasergame.game;

import fr.hyriode.hyrame.IHyrame;
import fr.hyriode.hyrame.actionbar.ActionBar;
import fr.hyriode.hyrame.game.HyriGamePlayer;
import fr.hyriode.hyrame.game.team.HyriGameTeam;
import fr.hyriode.hyrame.item.HyriItem;
import fr.hyriode.hyrame.utils.target.TargetInfo;
import fr.hyriode.hyrame.utils.target.TargetUtil;
import fr.hyriode.lasergame.HyriLaserGame;
import fr.hyriode.lasergame.configuration.HyriLGConfiguration;
import net.minecraft.server.v1_8_R3.EnumParticle;
import net.minecraft.server.v1_8_R3.PacketPlayOutWorldParticles;
import org.apache.commons.lang.Validate;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;
import xyz.xenondevs.particle.ParticleBuilder;
import xyz.xenondevs.particle.ParticleEffect;
import xyz.xenondevs.particle.data.color.RegularColor;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class HyriLGLaserGun extends HyriItem<HyriLaserGame> {

    private final List<Player> playersInCooldown = new ArrayList<>();
    private final Supplier<HyriLGGame> game = this.plugin::getGame;

    public HyriLGLaserGun(HyriLaserGame plugin) {
        super(plugin, "lasergun", () -> plugin.getHyrame().getLanguageManager().getMessage("item.lasergun"), ArrayList::new, Material.IRON_HOE);
    }

    @Override
    public void onRightClick(IHyrame hyrame, PlayerInteractEvent event) {
        HyriLGPlayer player = game.get().getPlayer(event.getPlayer().getUniqueId());
        if(!this.playersInCooldown.contains(player.getPlayer()) && !player.isDead()) {
            final double maxRange = this.plugin.getConfiguration().getLaserRange();
            final boolean friendlyFire = this.plugin.getConfiguration().isFriendlyFire();

            final HyriGameTeam team = this.plugin.getGame().getPlayer(player.getPlayer().getUniqueId()).getTeam();
            final List<Player> teamPlayers = team.getPlayers().stream().map(HyriGamePlayer::getPlayer).collect(Collectors.toList());

            final TargetInfo targetInfo = TargetUtil.getTarget(player.getPlayer(), maxRange, false, teamPlayers);
            final Color colorTeam = team.getColor().getDyeColor().getColor();

            double range = targetInfo != null ? targetInfo.getDistance() : maxRange;

            if(targetInfo != null){
                HyriLGPlayer playerTarget = game.get().getPlayer(targetInfo.getPlayer().getUniqueId());
                if(!playerTarget.isDead()) {
                    playerTarget.addDeath();
                    playerTarget.kill();
                    playerTarget.getScoreboard().update();
                    new ActionBar(ChatColor.GREEN + "+1").send(player.getPlayer());
                    playTouchSound(player.getPlayer());
                    playDeathSound(playerTarget.getPlayer());
                    player.addKill();
                    player.getScoreboard().update();
                }
            }

            Location origin = player.getPlayer().getEyeLocation();
            Vector direction = origin.getDirection();

            //start locationlui
            Location startingLoc = player.getPlayer().getLocation().clone();
            for (int block = 0; block < range; block++) {
                Vector blockVec = direction.clone().multiply(block);
                Location blockLoc = startingLoc.clone().add(blockVec);
                System.out.println(block);
                if(blockLoc.getBlock() != null && blockLoc.getBlock().getType() != Material.AIR) {
                    range = block;
                    break;
                }
            }

            direction.multiply(range);
            origin.clone().add(direction);
            direction.normalize();

            for (double i = 0; i < range; i += 0.1D) {
                Location loc = origin.add(direction);
//                new ParticleBuilder(ParticleEffect.REDSTONE, loc)
//                        .setParticleData(new RegularColor(colorTeam.getRed(),colorTeam.getGreen(),colorTeam.getBlue()))
//                        .display();
            }
            drawLine(player.getPlayer().getEyeLocation(), origin, 1, colorTeam);


            playGunSound(player.getPlayer());


            playersInCooldown.add(player.getPlayer());
            new BukkitRunnable() {
                @Override
                public void run() {
                    playersInCooldown.remove(player.getPlayer());
                }
            }.runTaskLaterAsynchronously(this.plugin, 10L);
        }

    }

    public void drawLine(Location point1, Location point2, double space, Color colorTeam) {
        World world = point1.getWorld();
        double distance = point1.distance(point2);
        Vector p1 = point1.toVector();
        Vector p2 = point2.toVector();
        Vector vector = p2.clone().subtract(p1).normalize().multiply(space);
        System.out.println(distance + " distance");
        for (double length = 0; length < distance; p1.add(vector)) {
            new ParticleBuilder(ParticleEffect.REDSTONE, new Location(world, p1.getX(), p1.getY(), p1.getZ()))
                    .setParticleData(new RegularColor(colorTeam.getRed(),colorTeam.getGreen(),colorTeam.getBlue()))
                    .display();
            length += space;
        }
    }

    public void playGunSound(final Player player) {
        Bukkit.getScheduler().runTaskLater(this.plugin, () -> player.playSound(player.getLocation(), Sound.SHOOT_ARROW, 1, 3F), 3);
    }

    public void playTouchSound(final Player player) {
        Bukkit.getScheduler().runTaskLater(this.plugin, () -> player.playSound(player.getLocation(), Sound.LEVEL_UP, 1, 3F), 3);
    }

    public void playDeathSound(final Player player) {
        Bukkit.getScheduler().runTaskLater(this.plugin, () -> player.playSound(player.getLocation(), Sound.VILLAGER_HIT, 1, 3F), 3);
    }

}
