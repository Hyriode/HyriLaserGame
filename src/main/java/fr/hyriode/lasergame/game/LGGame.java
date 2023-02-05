package fr.hyriode.lasergame.game;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.language.HyriLanguageMessage;
import fr.hyriode.api.player.IHyriPlayer;
import fr.hyriode.hyrame.IHyrame;
import fr.hyriode.hyrame.actionbar.ActionBar;
import fr.hyriode.hyrame.game.HyriGame;
import fr.hyriode.hyrame.game.HyriGamePlayer;
import fr.hyriode.hyrame.game.HyriGameState;
import fr.hyriode.hyrame.game.HyriGameType;
import fr.hyriode.hyrame.game.team.HyriGameTeam;
import fr.hyriode.hyrame.game.util.HyriGameMessages;
import fr.hyriode.hyrame.game.util.HyriRewardAlgorithm;
import fr.hyriode.hyrame.item.ItemBuilder;
import fr.hyriode.hyrame.title.Title;
import fr.hyriode.hyrame.utils.Area;
import fr.hyriode.lasergame.HyriLaserGame;
import fr.hyriode.lasergame.api.player.HyriLGPlayer;
import fr.hyriode.lasergame.game.bonus.LGBonus;
import fr.hyriode.lasergame.game.item.LGLaserGun;
import fr.hyriode.lasergame.game.map.LGMapRendererWin;
import fr.hyriode.lasergame.game.player.LGGamePlayer;
import fr.hyriode.lasergame.game.scoreboard.LGScoreboard;
import net.minecraft.server.v1_8_R3.EntityFallingBlock;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftFallingSand;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.map.MapView;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class LGGame extends HyriGame<LGGamePlayer> {

    private final HyriLaserGame plugin;

    private final List<LGBonus> bonus;

    private boolean doorOpen;
    private boolean finalKill = false;

    private boolean isStarted = false;

    public LGGame(IHyrame hyrame, HyriLaserGame plugin) {
        super(hyrame, plugin, HyriAPI.get().getConfig().isDevEnvironment() ? new LGGameInfo("lasergame", "LaserGame") : HyriAPI.get().getGameManager().getGameInfo("lasergame"), LGGamePlayer.class, HyriAPI.get().getConfig().isDevEnvironment() ? LGGameType.FIVE_FIVE : HyriGameType.getFromData(LGGameType.values()));

        this.plugin = plugin;

        this.description = HyriLanguageMessage.get("game.description");

        this.bonus = new ArrayList<>();

        for (ELGGameTeam team : ELGGameTeam.values())
            this.registerTeam(this.createTeam(team));
    }

    @Override
    public void postRegistration() {
        super.postRegistration();
    }

    @Override
    public void start() {
        super.start();

        this.isStarted = true;

        IHyrame.WORLD.get().getEntities().stream().filter(entity -> entity instanceof ArmorStand)
                .collect(Collectors.toList()).forEach(Entity::remove);

        this.plugin.getConfiguration().getBonusLocation()
                .forEach(location -> Bukkit.getScheduler().runTaskLater(this.plugin, () -> LGBonus.spawn(location, this.plugin), 20));

        for (LGGamePlayer player : this.players) {
            Player p = player.getPlayer();
            final LGScoreboard scoreboard = new LGScoreboard(this.plugin, this, p);

            player.setScoreboard(scoreboard);
            scoreboard.show();

            p.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 99999 * 20, 0));

            player.cleanPlayer();
            player.giveGun();
            player.giveArmor();
        }

        this.getLGTeams().forEach(LGGameTeam::teleportToSpawn);

        new BukkitRunnable() {
            int i = 10;

            @Override
            public void run() {
                if (getState() == HyriGameState.ENDED) {
                    this.cancel();
                    return;
                }
                if (i >= 0) {
                    players.forEach(p -> {
                        Player player = p.getPlayer();
                        String text = ChatColor.AQUA + "" + i;
                        String msg = ChatColor.DARK_AQUA + HyriLanguageMessage.get("game.starting-in").getValue(player) + " ";
                        switch (i) { //TODO refaire ce code foireux
                            case 3:
                                text = ChatColor.YELLOW + "" + i;
                                player.sendMessage(msg + text + ChatColor.DARK_AQUA + " seconds");
                                player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1, 1);
                                Title.sendTitle(player.getPlayer(), text, null, 1, 20, 1);
                                break;
                            case 2:
                                text = ChatColor.GOLD + "" + i;
                                player.sendMessage(msg + text + ChatColor.DARK_AQUA + " seconds");
                                player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1, 1);
                                Title.sendTitle(player.getPlayer(), text, null, 1, 20, 1);
                                break;
                            case 1:
                                text = ChatColor.RED + "" + i;
                                player.sendMessage(msg + text + ChatColor.DARK_AQUA + " seconds");
                                player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1, 1);
                                Title.sendTitle(player.getPlayer(), text, null, 1, 20, 1);
                                break;
                            case 10:
                                player.sendMessage(msg + text + ChatColor.DARK_AQUA + " seconds");
                                Title.sendTitle(player.getPlayer(), text, null, 1, 20, 1);
                                break;
                            case 0:
                                text = ChatColor.GREEN + "GO";
                                player.playSound(player.getLocation(), Sound.LEVEL_UP, 1, 1);
                                player.sendMessage(text);
                                Title.sendTitle(player.getPlayer(), text, null, 1, 20, 1);
                                break;
                        }
                    });
                    --i;
                } else {
                    plugin.getConfiguration().getTeams().forEach((team) -> {
                        Location locFirstDoorFirst = team.getDoors().get(0).getFirstPointDoor();
                        Location locSecondDoorFirst = team.getDoors().get(0).getSecondPointDoor();
                        BlockState block = locFirstDoorFirst.clone().getBlock().getState();
                        new BukkitRunnable() {
                            int i = 5;

                            @Override
                            public void run() {
                                if (i <= 0) {
                                    doorAnimationClose(locFirstDoorFirst, locSecondDoorFirst, block);
                                    plugin.getGame().setState(HyriGameState.PLAYING);
                                    new BukkitRunnable() {
                                        @Override
                                        public void run() {
                                            kickPlayersInBase(team.getFirstPointBaseArea(), team.getSecondPointBaseArea(), team.getSpawnCloseDoorLocation());
                                            players.forEach(player -> {
                                                player.getPlayer().setExp(1.0F);
                                                new ActionBar(ChatColor.GREEN + HyriLanguageMessage.get("player.death.subtitle.good").getValue(player.getPlayer())).send(player.getPlayer());
                                            });
                                            ((LGLaserGun) hyrame.getItemManager().getItem(LGLaserGun.class)).setEnable(true);
                                        }
                                    }.runTaskLater(plugin, 20L);
                                    cancel();
                                    return;
                                }
                                players.forEach(player -> new ActionBar(ChatColor.GREEN + HyriLanguageMessage.get("game.timer.can-shoot").getValue(player.getPlayer()) + " " + i + "s").send(player.getPlayer()));
                                --i;
                            }
                        }.runTaskTimer(plugin, 0, 20);
                        doorAnimationOpen(locFirstDoorFirst, locSecondDoorFirst, block);

                        if (team.getDoors().size() > 1) {
                            Location locFirstDoorSecond = team.getDoors().get(1).getFirstPointDoor();
                            Location locSecondDoorSecond = team.getDoors().get(1).getSecondPointDoor();

                            if (locFirstDoorSecond != null && locSecondDoorSecond != null) {
                                BlockState blockSecond = locFirstDoorSecond.clone().getBlock().getState();
                                new BukkitRunnable() {
                                    @Override
                                    public void run() {
                                        doorAnimationClose(locFirstDoorSecond, locSecondDoorSecond, blockSecond);
                                    }
                                }.runTaskLater(plugin, 20L * 5);
                                doorAnimationOpen(locFirstDoorSecond, locSecondDoorSecond, blockSecond);
                            } else System.out.println("The second door don't exist");
                        }
                    });
                    cancel();

                }
            }
        }.runTaskTimer(this.plugin, 0L, 20L);

    }

    @Override
    public void handleLogin(Player p) {
        System.out.println("Login");
        super.handleLogin(p);

        p.teleport(this.plugin.getConfiguration().getWaitingRoom().getWaitingSpawn());

        this.getPlayer(p.getUniqueId()).setPlugin(this.plugin);
    }

    @Override
    public void handleLogout(Player p) {
        super.handleLogout(p);
        if (this.getState() == HyriGameState.PLAYING && this.isStarted) {
            if (this.players.isEmpty()) {
                this.win(this.getWinner());
            } else {
                for (HyriGameTeam team : this.teams) {
                    if (team.getPlayers().size() <= 0) {
                        this.win(this.getWinner());
                    }
                }
            }
        }
    }

    @Override
    public void win(HyriGameTeam winner) {
        super.win(winner);
        System.out.println("Game down");
        this.players.forEach(gamePlayer -> {
            if (winner.contains(gamePlayer)) {
                final HyriLGPlayer pl = gamePlayer.getAccount();

                pl.setCurrentWinStreak(pl.getCurrentWinStreak() + 1);
                pl.setBestWinStreak(pl.getBestWinStreak() + 1);
                pl.update(gamePlayer);
            }

            final Player player = gamePlayer.getPlayer();
            final List<String> killsLines = new ArrayList<>();
            final List<LGGamePlayer> topKillers = new ArrayList<>(this.players);

            topKillers.sort((o1, o2) -> o2.getAllPoints() - o1.getAllPoints());

            for (int i = 0; i <= 2 && topKillers.size() > i; i++) {
                final LGGamePlayer endPlayer = topKillers.get(i);
                final String line = HyriLanguageMessage.get("message.game.end.kills").getValue(player).replace("%position%", HyriLanguageMessage.get("message.game.end." + (i + 1)).getValue(player));

                if (endPlayer == null) {
                    killsLines.add(line.replace("%player%", HyriLanguageMessage.get("message.game.end.nobody").getValue(player))
                            .replace("%kills%", "0"));
                    continue;
                }

                killsLines.add(line.replace("%player%", HyriAPI.get().getPlayerManager().getPlayer(endPlayer.getUniqueId()).getNameWithRank())
                        .replace("%kills%", String.valueOf(endPlayer.getAllPoints())));
            }

            final int kills = gamePlayer.getKills();
            final boolean isWinner = winner.contains(gamePlayer);

            final long hyris = HyriRewardAlgorithm.getHyris(kills, gamePlayer.getPlayTime(), isWinner);
            final double xp = HyriRewardAlgorithm.getXP(kills, gamePlayer.getPlayTime(), isWinner);
            final List<String> rewards = new ArrayList<>();

            rewards.add(ChatColor.LIGHT_PURPLE + String.valueOf(hyris) + " Hyris");
            rewards.add(ChatColor.GREEN + String.valueOf(xp) + " XP");

            IHyriPlayer p = HyriAPI.get().getPlayerManager().getPlayer(gamePlayer.getUniqueId());

            p.getHyris().add(hyris).withMessage(false).exec();
            p.getNetworkLeveling().addExperience(xp);

            p.update();

            player.spigot().sendMessage(HyriGameMessages.createWinMessage(this, player, winner, killsLines, rewards));

            gamePlayer.setSpectator(true);
            giveResultMap(player);
        });
    }

    @SuppressWarnings("deprecation")
    private void giveResultMap(Player player) {
        final MapView map = Bukkit.createMap(player.getWorld());
        map.setScale(MapView.Scale.FARTHEST);
        map.removeRenderer(map.getRenderers().get(0));
        map.addRenderer(new LGMapRendererWin(this.plugin));
        player.getInventory().setItem(7, new ItemBuilder(new ItemStack(Material.MAP, 1, map.getId())).withName(ChatColor.YELLOW + HyriLanguageMessage.get("map.name").getValue(player)).build());
        player.getInventory().setHeldItemSlot(7);
    }

    public boolean hasSamePoints() {
        return this.getTeamPoints(this.getWinner()) == this.getTeamPoints(this.getLooser());
    }

    public HyriGameTeam getWinner(boolean isWinner) {
        HyriGameTeam gameTeam = null;
        for (HyriGameTeam team : this.teams) {
            if (gameTeam == null) {
                gameTeam = team;
            } else {
                if (isWinner) {
                    if (this.getTeamPoints(team) > this.getTeamPoints(gameTeam)) {
                        gameTeam = team;
                    }
                } else {
                    if (this.getTeamPoints(team) < this.getTeamPoints(gameTeam)) {
                        gameTeam = team;
                    }
                }
            }
        }
        return gameTeam;
    }

    public HyriGameTeam getWinner() {
        return this.getWinner(true);
    }

    public HyriGameTeam getLooser() {
        return this.getWinner(false);
    }

    public int getTeamPoints(HyriGameTeam team) {
        int pointsTeam = 0;

        for (HyriGamePlayer player : team.getPlayers()) {
            pointsTeam += this.getPlayer(player.getPlayer()).getAllPoints();
        }

        return pointsTeam;
    }

    public List<LGGameTeam> getLGTeams() {
        return this.teams.stream().map(team -> (LGGameTeam) team).collect(Collectors.toList());
    }

    private void kickPlayersInBase(Location locFirst, Location locSecond, Location spawnLoc) {
        Area area = new Area(locFirst, locSecond);
        this.plugin.getGame().getPlayers().forEach(player -> {
            if (area.isInArea(player.getPlayer().getLocation())) {
                player.getPlayer().teleport(spawnLoc);
            }
        });
    }

    private HyriGameTeam createTeam(ELGGameTeam gameTeam) {
        return new LGGameTeam(this.plugin, gameTeam, ((LGGameType) this.type).getTeamsSize());
    }

    private void doorAnimationOpen(Location locFirst, Location locSecond, BlockState blockk) {
        this.doorOpen = true;
        Area area = new Area(locFirst, locSecond);
        int fx = area.getMin().getBlockX();
        int fy = area.getMin().getBlockY();
        int fz = area.getMin().getBlockZ();

        int sx = area.getMax().getBlockX();
        int sy = area.getMax().getBlockY();
        int sz = area.getMax().getBlockZ();
        for (int y = fy; y <= sy; ++y) {
            for (int x = fx; x <= sx; ++x) {
                Location loc = new Location(locFirst.getWorld(), x, y, fz).clone();
                Block block = loc.getBlock();
                block.setType(Material.AIR);
                EntityFallingBlock fallingBlock = ((CraftFallingSand) locFirst.getWorld().spawnFallingBlock(loc, blockk.getType(), blockk.getData().getData())).getHandle();
//                EntityFallingBlock fallingBlock = new EntityFallingBlock(((CraftWorld) IHyrame.WORLD.get()).getHandle(), x, y, fz, CraftMagicNumbers.getBlock(blockk.getBlock()).getBlockData());
                fallingBlock.noclip = true;
//                ((CraftWorld) IHyrame.WORLD.get()).getHandle().addEntity(fallingBlock);

                new BukkitRunnable() {
                    int i = 0;

                    @Override
                    public void run() {
                        if (i < 40) {
                            i++;
                            fallingBlock.getBukkitEntity().teleport(fallingBlock.getBukkitEntity().getLocation().add(new Vector().setY(-3D)));
                        } else {
                            fallingBlock.getBukkitEntity().remove();
                            cancel();
                        }
                    }
                }.runTaskTimer(this.plugin, 0L, 1L);
            }
        }
    }

    private void doorAnimationClose(Location locFirst, Location locSecond, BlockState block) {
        Area area = new Area(locFirst, locSecond);
        int fx = area.getMin().getBlockX();
        int fy = area.getMin().getBlockY();
        int fz = area.getMin().getBlockZ();

        int sx = area.getMax().getBlockX();
        int sy = area.getMax().getBlockY();

        new BukkitRunnable() {
            int y = sy;

            @Override
            public void run() {
                if (y >= fy) {
                    for (int x = fx; x <= sx; ++x) {
                        Block blockLoc = new Location(locFirst.getWorld(), x, y, fz).getBlock();
                        blockLoc.setType(block.getType()/*data.getType()*/);
                        BlockState blockState = blockLoc.getState();
                        blockState.setData(block.getData());
                        blockState.update();
                    }
                } else {
                    cancel();
                }
                --y;
            }
        }.runTaskTimer(plugin, 0L, 10L);
    }

    public boolean isDoorOpen() {
        return doorOpen;
    }

    public boolean isFinalKill() {
        return finalKill;
    }

    public void setFinalKill() {
        this.finalKill = true;
    }

    public LGBonus getBonus(UUID uuid) {
        return this.bonus.stream().filter(bonus -> bonus.getArmorStand().getUniqueId().equals(uuid)).findFirst().orElse(null);
    }

    public List<LGBonus> getBonus() {
        return this.bonus;
    }

    private void refreshAPIPlayer(LGGamePlayer gamePlayer) {
        final HyriLGPlayer account = gamePlayer.getAccount();

        if (this.getState() != HyriGameState.READY && this.getState() != HyriGameState.WAITING) {
            if (account != null) {
                account.update(gamePlayer);
            }
        }
    }

    public HyriGameTeam getAdverseTeam(HyriGameTeam team) {
        for (HyriGameTeam otherTeam : this.teams) {
            if (otherTeam != team)
                return otherTeam;
        }
        return null;
    }

    public void addBonus(LGBonus bonus) {
        this.bonus.add(bonus);
    }

    public void removeBonus(UUID uuid) {
        this.bonus.removeIf(bonus -> bonus.getArmorStand().getUniqueId() == uuid);
    }
}
