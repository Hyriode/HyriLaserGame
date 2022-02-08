package fr.hyriode.lasergame.game;

import fr.hyriode.hyrame.IHyrame;
import fr.hyriode.hyrame.game.HyriGame;
import fr.hyriode.hyrame.game.HyriGamePlayer;
import fr.hyriode.hyrame.game.HyriGameType;
import fr.hyriode.hyrame.game.team.HyriGameTeam;
import fr.hyriode.hyrame.game.team.HyriGameTeamColor;
import fr.hyriode.hyrame.game.util.HyriGameItems;
import fr.hyriode.hyrame.language.HyriLanguageMessage;
import fr.hyriode.hyrame.title.Title;
import fr.hyriode.hyrame.utils.Area;
import fr.hyriode.hyriapi.settings.HyriLanguage;
import fr.hyriode.lasergame.HyriLaserGame;
import fr.hyriode.lasergame.game.scoreboard.HyriLGScoreboard;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.material.MaterialData;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.NameTagVisibility;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.List;
import java.util.function.Supplier;

public class HyriLGGame extends HyriGame<HyriLGPlayer> {

    public static final Supplier<Location> DEFAULT_LOCATION = () -> new Location(HyriLaserGame.WORLD.get(), 0, 90, 0);

    private HyriLaserGame plugin;

    public HyriLGGame(IHyrame hyrame, HyriLaserGame plugin) {
        super(hyrame, plugin, "lasergame", "LaserGame", HyriLGPlayer.class, true);

        this.plugin = plugin;

        this.minPlayers = 4;
        this.maxPlayers = 8;

        for (HyriLGGameTeam team : HyriLGGameTeam.values())
            this.registerTeam(this.createTeam(team));
    }

    @Override
    public void start() {
        super.start();
        Scoreboard score = Bukkit.getScoreboardManager().getMainScoreboard();
        Team teamHide = score.getTeam("nhide");
        if(teamHide == null){
            teamHide = score.registerNewTeam("nhide");
            teamHide.setNameTagVisibility(NameTagVisibility.NEVER);
        }
        for (HyriLGPlayer player : this.players) {
            teamHide.addEntry(player.getPlayer().getName());

            final HyriLGScoreboard scoreboard = new HyriLGScoreboard(this.plugin, player.getPlayer());

            player.setScoreboard(scoreboard);
            scoreboard.show();

            player.cleanPlayer();
            player.giveGun();
            player.giveArmor();
        }
        this.teams.forEach(HyriGameTeam::teleportToSpawn);
        new BukkitRunnable(){
            int i = 10;
            @Override
            public void run() {
                if(i > 0){
                    players.forEach(player -> {
                        Title.sendTitle(player.getPlayer(), i + "", null, 1, 20, 1);
                    });
                    --i;
                }else {
                    plugin.getConfiguration().getTeams().forEach((gameTeam, team) -> {
                        Location locFirst = team.getFirstPointDoor();
                        Location locSecond = team.getSecondPointDoor();
                        BlockState block = locFirst.clone().getBlock().getState();
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                doorAnimationClose(locFirst, locSecond, block);
                                new BukkitRunnable() {
                                    @Override
                                    public void run() {
                                        kickPlayersInBase(team.getFirstPointSpawn(), team.getSecondPointSpawn(), team.getSpawnCloseDoorLocation());
                                    }
                                }.runTaskLater(plugin, 20L);
                            }
                        }.runTaskLater(plugin, 20L * 5);
                        doorAnimationOpen(locFirst, locSecond);

                    });
                    cancel();
                }
            }
        }.runTaskTimer(this.plugin, 0L, 20L);
        //preparer timer


    }

    private void kickPlayersInBase(Location locFirst, Location locSecond, Location spawnLoc) {
        Area area = new Area(locFirst, locSecond);
        this.plugin.getGame().getPlayers().forEach(player -> {
            if(area.isInArea(player.getPlayer().getLocation())){
                player.getPlayer().teleport(spawnLoc);
            }
        });
    }

    @Override
    public void handleLogin(Player p) {
        super.handleLogin(p);
        p.getInventory().setArmorContents(null);
        p.getInventory().clear();
        p.setGameMode(GameMode.ADVENTURE);
        p.setFoodLevel(20);
        p.setHealth(20);
        p.setLevel(0);
        p.setExp(0.0F);
        p.setCanPickupItems(false);
        p.teleport(this.plugin.getConfiguration().getSpawnLocation());

        HyriGameItems.TEAM_CHOOSER.give(this.hyrame, p, 0);
        this.getPlayer(p.getUniqueId()).setPlugin(this.plugin);
    }

    private HyriGameTeam createTeam(HyriLGGameTeam gameTeam){
        final HyriGameTeam team = new HyriGameTeam(gameTeam.getName(), this.hyrame.getLanguageManager().getMessage(gameTeam.getName()+".display"), gameTeam.getColor(), 4);
        team.setSpawnLocation(this.plugin.getConfiguration().getTeams().get(gameTeam).getSpawnLocation());
        return team;
    }

    private void doorAnimationOpen(Location locFirst, Location locSecond){
        Area area = new Area(locFirst, locSecond);
        int fx = area.getMin().getBlockX();
        int fy = area.getMin().getBlockY();
        int fz = area.getMin().getBlockZ();

        int sx = area.getMax().getBlockX();
        int sy = area.getMax().getBlockY();
        int sz = area.getMax().getBlockZ();
        new BukkitRunnable(){
            int y = fy;
            @Override
            public void run() {
                if(y <= sy) {
                    for (int x = fx; x <= sx; ++x) {
                        new Location(locFirst.getWorld(), x, y, fz).getBlock().setType(Material.AIR);
                    }
                }
                ++y;
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }

    private void doorAnimationClose(Location locFirst, Location locSecond, BlockState block){
        System.out.println("asdsq "+block);
        Area area = new Area(locFirst, locSecond);
        int fx = area.getMin().getBlockX();
        int fy = area.getMin().getBlockY();
        int fz = area.getMin().getBlockZ();

        int sx = area.getMax().getBlockX();
        int sy = area.getMax().getBlockY();
        int sz = area.getMax().getBlockZ();

        System.out.println(block.getBlock());

        new BukkitRunnable(){
            int y = sy;
            @Override
            public void run() {
                if(y >= fy) {
                    for (int x = fx; x <= sx; ++x) {
                        Block blockLoc = new Location(locFirst.getWorld(), x, y, fz).getBlock();
                        blockLoc.setType(block.getType()/*data.getType()*/);
                        BlockState blockState = blockLoc.getState();
                        blockState.setData(block.getData());
                        blockState.update();
                    }
                }else{
                    cancel();
                }
                --y;
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }


}
