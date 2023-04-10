package fr.hyriode.lasergame.game.item;

import fr.hyriode.api.language.HyriLanguageMessage;
import fr.hyriode.hyrame.IHyrame;
import fr.hyriode.hyrame.actionbar.ActionBar;
import fr.hyriode.hyrame.game.HyriGamePlayer;
import fr.hyriode.hyrame.game.team.HyriGameTeam;
import fr.hyriode.hyrame.item.HyriItem;
import fr.hyriode.hyrame.utils.target.TargetInfo;
import fr.hyriode.hyrame.utils.target.TargetProvider;
import fr.hyriode.lasergame.HyriLaserGame;
import fr.hyriode.lasergame.game.LGGame;
import fr.hyriode.lasergame.game.bonus.BonusManager;
import fr.hyriode.lasergame.game.bonus.LGBonus;
import fr.hyriode.lasergame.game.bonus.LGBonusType;
import fr.hyriode.lasergame.game.player.LGGamePlayer;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
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
import java.util.stream.Collectors;

public class LGLaserGun extends HyriItem<HyriLaserGame> {

    public static final String LASER_GUN_NBT = "LaserGun";

    private boolean enable;

    public LGLaserGun(HyriLaserGame plugin) {
        super(plugin, "lasergun", () -> HyriLanguageMessage.get("item.lasergun"), () -> null, Material.IRON_HOE);
        this.enable = false;
    }

    @Override
    public void onRightClick(IHyrame hyrame, PlayerInteractEvent event) {
        this.click(event.getPlayer());
    }

    @Override
    public void onLeftClick(IHyrame hyrame, PlayerInteractEvent event) {
        this.click(event.getPlayer());
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    private void click(Player player){
        if(!enable) return;
        LGGame game = this.plugin.getGame();
        LGGamePlayer killer = game.getPlayer(player.getUniqueId());
        boolean isFaster = killer.hasBonus() && killer.getBonus() == LGBonusType.SHOOT_FASTER.get();

        if((!killer.isCooldown() || isFaster) && !killer.isDead()) {
            final double maxRange = this.plugin.getConfiguration().getLaserRange();
            final boolean friendlyFire = killer.getTeam().isFriendlyFire();

            final HyriGameTeam team = this.plugin.getGame().getPlayer(player.getUniqueId()).getTeam();
            final List<Player> teamPlayers = team.getPlayers().stream().map(HyriGamePlayer::getPlayer).collect(Collectors.toList());
            final List<Material> whitelistedBlocks = Arrays.asList(Material.AIR, Material.IRON_FENCE, Material.BARRIER);
            final TargetInfo targetInfo = new TargetProvider(player, maxRange)
                    .withAimingTolerance(1.60D)
                    .withIgnoredBlocks(whitelistedBlocks)
                    .withIgnoredEntities(friendlyFire ? new ArrayList<>() : teamPlayers.stream().map(p -> (Entity)p).collect(Collectors.toList()))
                    .get();
            final Color colorTeam = team.getColor().getDyeColor().getColor();
            final Location eyeLocation = player.getEyeLocation();
            final Vector direction = eyeLocation.getDirection();

            double range = maxRange;

            if (targetInfo != null) {
                range = targetInfo.getDistance();

                if(!(targetInfo.getEntity() instanceof Player)){
                    if(targetInfo.getEntity() instanceof ArmorStand){
                        ArmorStand bonus = (ArmorStand) targetInfo.getEntity();
                        killer.activeBonus(bonus);
                    }
                }else {

                    LGGamePlayer targetPlayer = game.getPlayer(targetInfo.getEntity().getUniqueId());
                    boolean isShieldTarget = targetPlayer.hasBonus() && targetPlayer.getBonus().getName().equals(BonusManager.SHIELD);

                    if (!targetPlayer.isDead() && !targetPlayer.isSpectator() && !isShieldTarget) {
                        new ActionBar(ChatColor.GREEN + "+1 Kill").send(player);
                        player.sendMessage(ChatColor.GREEN + "+1 Kill");

                        targetPlayer.kill();
                        targetPlayer.addDeath();

                        killer.addKill();
                        killer.addKillStreak();
                        killer.setBestKillStreak();

                        targetPlayer.setKillStreak(0);

                        this.playHitSound(player);
                        this.playDeathSound((Player) targetInfo.getEntity());
                        if (game.isFinalKill()) {
                            game.win(game.getWinner());
                        }
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

            this.playGunSound(player);
            this.setCooldown(killer);
        }
    }

    private void setCooldown(LGGamePlayer player) {
        if(!player.isCooldown()) {
            player.setCooldown(true);
            new BukkitRunnable() {
                int i = 0;
                @Override
                public void run() {
                    player.getPlayer().setExp((float) i / 10);//5
                    LGBonus bonus = player.getBonus();
                    if(i >= 10 || bonus != null && bonus.getName().equals(BonusManager.SHOOT_FASTER)){
                        player.getPlayer().setExp(1.0F);
                        player.setCooldown(false);
                        cancel();
                    }
                    ++i;
                }
            }.runTaskTimer(this.plugin, 0L, 1L);
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
