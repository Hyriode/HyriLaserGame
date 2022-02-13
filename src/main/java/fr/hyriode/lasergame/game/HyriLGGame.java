package fr.hyriode.lasergame.game;

import fr.hyriode.hyrame.IHyrame;
import fr.hyriode.hyrame.game.HyriGame;
import fr.hyriode.hyrame.game.HyriGamePlayer;
import fr.hyriode.hyrame.game.HyriGameState;
import fr.hyriode.hyrame.game.team.HyriGameTeam;
import fr.hyriode.hyrame.game.util.HyriGameItems;
import fr.hyriode.hyrame.title.Title;
import fr.hyriode.hyrame.utils.Area;
import fr.hyriode.lasergame.HyriLaserGame;
import fr.hyriode.lasergame.game.bonus.HyriLGBonus;
import fr.hyriode.lasergame.game.map.HyriLGMapRendererWin;
import fr.hyriode.lasergame.game.player.HyriLGPlayer;
import fr.hyriode.lasergame.game.scoreboard.HyriLGScoreboard;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.map.MapView;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class HyriLGGame extends HyriGame<HyriLGPlayer> {

    private final HyriLaserGame plugin;

    private final List<HyriLGBonus> bonus;

    private boolean doorOpen;
    private boolean finalKill = false;

    public HyriLGGame(IHyrame hyrame, HyriLaserGame plugin) {
        super(hyrame, plugin, "lasergame", "LaserGame", HyriLGPlayer.class, true);

        this.plugin = plugin;

        this.bonus = new ArrayList<>();

        this.minPlayers = 4;
        this.maxPlayers = 8;

        for (HyriLGGameTeam team : HyriLGGameTeam.values())
            this.registerTeam(this.createTeam(team));
    }

    @Override
    public void start() {
        super.start();
        this.setState(HyriGameState.WAITING);

        HyriLaserGame.WORLD.get().getEntities().stream().filter(entity -> entity instanceof ArmorStand)
                .collect(Collectors.toList()).forEach(Entity::remove);

        this.plugin.getConfiguration().getBonusLocation()
                .forEach(location -> this.bonus.add(new HyriLGBonus(location, this.plugin).spawn()));

        for (HyriLGPlayer player : this.players) {
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
                if(i >= 0){
                    players.forEach(player -> {
                        String text = ChatColor.YELLOW + "" + i;
                        switch (i){
                            case 3:
                                text = ChatColor.GOLD + "" + i;
                                player.getPlayer().playSound(player.getPlayer().getLocation(), Sound.ORB_PICKUP, 1, 1);
                                break;
                            case 2:
                                text = ChatColor.RED + "" + i;
                                player.getPlayer().playSound(player.getPlayer().getLocation(), Sound.ORB_PICKUP, 1, 1);
                                break;
                            case 1:
                                text = ChatColor.DARK_RED + "" + i;
                                player.getPlayer().playSound(player.getPlayer().getLocation(), Sound.ORB_PICKUP, 1, 1);
                                break;
                            case 0:
                                text = ChatColor.GREEN + "GO";
                                player.getPlayer().playSound(player.getPlayer().getLocation(), Sound.LEVEL_UP, 1, 1);
                                break;
                        }
                        Title.sendTitle(player.getPlayer(), text, null, 1, 20, 1);

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
                                plugin.getGame().setState(HyriGameState.PLAYING);
                                new BukkitRunnable() {
                                    @Override
                                    public void run() {
                                        kickPlayersInBase(team.getFirstPointSpawn(), team.getSecondPointSpawn(), team.getSpawnCloseDoorLocation());
                                    }
                                }.runTaskLater(plugin, 20L);
                            }
                        }.runTaskLater(plugin, 20L * 5);
                        doorAnimationOpen(locFirst, locSecond, block);


                    });
                    cancel();
                }
            }
        }.runTaskTimer(this.plugin, 0L, 20L);

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
        HyriGameItems.LEAVE_ITEM.give(this.hyrame, p, 8);

        this.getPlayer(p.getUniqueId()).setPlugin(this.plugin);
    }

    @Override
    public void win(HyriGameTeam winner) {
        super.win(winner);
        this.players.forEach(player -> {
            player.getPlayer().getInventory().clear();
            giveResultMap(player.getPlayer());
        });
    }

    @SuppressWarnings("deprecation")
    private void giveResultMap(Player player){
        final MapView map = Bukkit.createMap(player.getWorld());
        map.setScale(MapView.Scale.FARTHEST);
        map.removeRenderer(map.getRenderers().get(0));
        map.addRenderer(new HyriLGMapRendererWin(this.plugin));
        player.getInventory().setItem(0, new ItemStack(Material.MAP, 1, map.getId()));
        player.getInventory().setHeldItemSlot(0);
    }

    public boolean hasSamePoints(){
        return this.getTeamPoints(this.getWinner()) == this.getTeamPoints(this.getLooser());
    }

    public HyriGameTeam getWinner(){
        HyriGameTeam winner = null;
        for(HyriGameTeam team : this.teams){
            if(winner == null){
                winner = team;
            }else if(this.getTeamPoints(team) > this.getTeamPoints(winner)){
                winner = team;
            }
        }
        return winner;
    }

    public HyriGameTeam getLooser(){
        HyriGameTeam looser = null;
        for(HyriGameTeam team : this.teams){
            if(looser == null){
                looser = team;
            }else if(this.getTeamPoints(team) < this.getTeamPoints(looser)){
                looser = team;
            }
        }
        return looser;
    }

    public int getTeamPoints(HyriGameTeam team){
        int kills = 0;
        int deaths = 0;
        for(HyriGamePlayer player : team.getPlayers()){
            kills += this.getPlayer(player.getUUID()).getKills();
            deaths += this.getPlayer(player.getUUID()).getDeaths();
        }
        kills *= 75;
        deaths *= 30;
        int result = Math.max(kills - deaths, 0);
        return result;
    }

    private void kickPlayersInBase(Location locFirst, Location locSecond, Location spawnLoc) {
        Area area = new Area(locFirst, locSecond);
        this.plugin.getGame().getPlayers().forEach(player -> {
            if(area.isInArea(player.getPlayer().getLocation())){
                player.getPlayer().teleport(spawnLoc);
            }
        });
    }

    private HyriGameTeam createTeam(HyriLGGameTeam gameTeam){
        final HyriGameTeam team = new HyriGameTeam(gameTeam.getName(), this.hyrame.getLanguageManager().getMessage(gameTeam.getName()+".display"), gameTeam.getColor(), 4);
        team.setSpawnLocation(this.plugin.getConfiguration().getTeams().get(gameTeam).getSpawnLocation());
        return team;
    }

    private void doorAnimationOpen(Location locFirst, Location locSecond, BlockState block){
        this.doorOpen = true;
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
                        Location loc = new Location(locFirst.getWorld(), x, y, fz).clone();
                        Block block = loc.getBlock();
                        block.setType(Material.AIR);
//                        locFirst.getWorld().spawnFallingBlock(loc, block.getType(), block.getData());

                    }
                }
                ++y;
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }

    private void doorAnimationClose(Location locFirst, Location locSecond, BlockState block){
        Area area = new Area(locFirst, locSecond);
        int fx = area.getMin().getBlockX();
        int fy = area.getMin().getBlockY();
        int fz = area.getMin().getBlockZ();

        int sx = area.getMax().getBlockX();
        int sy = area.getMax().getBlockY();

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

    public boolean isDoorOpen() {
        return doorOpen;
    }

    public boolean isFinalKill() {
        return finalKill;
    }

    public void setFinalKill(){
        this.finalKill = true;
    }

    public HyriLGBonus getBonus(UUID uuid) {
        return this.bonus.stream().filter(bonus -> bonus.getUUID().equals(uuid)).findFirst().orElse(null);
    }
}
