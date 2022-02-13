package fr.hyriode.lasergame.game.item;

import fr.hyriode.hyrame.IHyrame;
import fr.hyriode.hyrame.actionbar.ActionBar;
import fr.hyriode.hyrame.game.HyriGamePlayer;
import fr.hyriode.hyrame.game.team.HyriGameTeam;
import fr.hyriode.hyrame.item.HyriItem;
import fr.hyriode.hyrame.utils.LocationUtil;
import fr.hyriode.hyrame.utils.Vector3D;
import fr.hyriode.hyrame.utils.target.TargetInfo;
import fr.hyriode.hyrame.utils.target.TargetUtil;
import fr.hyriode.lasergame.HyriLaserGame;
import fr.hyriode.lasergame.game.HyriLGGame;
import fr.hyriode.lasergame.game.bonus.HyriLGBonusType;
import fr.hyriode.lasergame.game.player.HyriLGPlayer;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import xyz.xenondevs.particle.ParticleBuilder;
import xyz.xenondevs.particle.ParticleEffect;
import xyz.xenondevs.particle.data.color.RegularColor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class HyriLGLaserGun extends HyriItem<HyriLaserGame> {

    private final Supplier<HyriLGGame> game = this.plugin::getGame;

    public HyriLGLaserGun(HyriLaserGame plugin) {
        super(plugin, "lasergun", () -> plugin.getHyrame().getLanguageManager().getMessage("item.lasergun"), ArrayList::new, Material.IRON_HOE);
    }

    @Override
    public void onRightClick(IHyrame hyrame, PlayerInteractEvent event) {
        HyriLGPlayer player = game.get().getPlayer(event.getPlayer().getUniqueId());
        boolean isFaster = player.hasBonus() && player.getBonus() == HyriLGBonusType.SHOOT_FASTER;

        if((!player.isCooldown() || isFaster) && !player.isDead()) {
            final double maxRange = this.plugin.getConfiguration().getLaserRange();
            final boolean friendlyFire = this.plugin.getConfiguration().isFriendlyFire();

            final HyriGameTeam team = this.plugin.getGame().getPlayer(player.getPlayer().getUniqueId()).getTeam();
            final List<Player> teamPlayers = team.getPlayers().stream().map(HyriGamePlayer::getPlayer).collect(Collectors.toList());
            final List<Material> whitelistedBlocks = Arrays.asList(Material.AIR, Material.IRON_FENCE, Material.BARRIER);

            final TargetInfo targetInfo = TargetUtil.getTarget(player.getPlayer(), maxRange, false, teamPlayers);
            final Color colorTeam = team.getColor().getDyeColor().getColor();
            final Location eyeLocation = player.getPlayer().getEyeLocation();
            final Vector direction = eyeLocation.getDirection();

            double range = maxRange;

            if (targetInfo != null) {
                range = targetInfo.getDistance();
                HyriLGPlayer targetPlayer = this.game.get().getPlayer(targetInfo.getPlayer().getUniqueId());
                boolean isShieldTarget = targetPlayer.hasBonus() && targetPlayer.getBonus() == HyriLGBonusType.SHIELD;

                if(!targetPlayer.isDead() && !isShieldTarget) {
                    new ActionBar(ChatColor.GREEN + "+1 Kill").send(player.getPlayer());

                    targetPlayer.kill();
                    targetPlayer.addDeath();
                    player.addKill();

                    this.playHitSound(player.getPlayer());
                    this.playDeathSound(targetInfo.getPlayer());
                    if(this.plugin.getGame().isFinalKill()){
                        this.plugin.getGame().win(this.plugin.getGame().getWinner());
                    }
                }
            } else {

                final Location startingLoc = eyeLocation.clone();

                for (int blockIndex = 0; blockIndex < range; blockIndex++) {
                    final Vector blockVec = direction.clone().multiply(blockIndex);
                    final Location blockLoc = startingLoc.clone().add(blockVec);
                    final Block block = blockLoc.getBlock();

                    if(block != null) {
                        final Material type = block.getType();

                        if (!whitelistedBlocks.contains(type)) {
                            range = blockIndex;
                            break;
                        }
                    }
                }
            }

            direction.multiply(range);
            final Location endLocation = eyeLocation.clone().add(direction);
            direction.normalize();

            this.drawLine(eyeLocation, endLocation, 1, colorTeam);


            this.playGunSound(player.getPlayer());
            this.setCooldown(player);
        }

    }

    private void setCooldown(HyriLGPlayer player) {
        if(!player.isCooldown()) {
            player.setCooldown(true);
            new BukkitRunnable() {
                @Override
                public void run() {
                    System.out.println("REMOVE PLAYER " + player.getPlayer().getName());
                    player.setCooldown(false);
                }
            }.runTaskLaterAsynchronously(this.plugin, 10L);
        }
    }

    public void drawLine(Location first, Location end, double space, Color colorTeam) {
        final World world = first.getWorld();
        final double distance = first.distance(end);
        final Vector p1 = first.toVector();
        final Vector p2 = end.toVector();
        final Vector vector = p2.clone().subtract(p1).normalize().multiply(space);

        for (double length = 0; length < distance; p1.add(vector)) {
            new ParticleBuilder(ParticleEffect.REDSTONE, new Location(world, p1.getX(), p1.getY(), p1.getZ()))
                    .setParticleData(new RegularColor(colorTeam.getRed(), colorTeam.getGreen(), colorTeam.getBlue()))
                    .display();
            length += space;
        }
    }

    public void playGunSound(Player player) {
        this.playSound(player, Sound.SHOOT_ARROW);
    }

    public void playHitSound(Player player) {
        this.playSound(player, Sound.LEVEL_UP);
    }

    public void playDeathSound(Player player) {
        this.playSound(player, Sound.VILLAGER_HIT);
    }

    private void playSound(Player player, Sound sound){
        player.playSound(player.getLocation(), sound, 1, 3F);
    }

}
