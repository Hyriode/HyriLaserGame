package fr.hyriode.lasergame.game;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.hyrame.IHyrame;
import fr.hyriode.hyrame.actionbar.ActionBar;
import fr.hyriode.hyrame.game.HyriGame;
import fr.hyriode.hyrame.game.HyriGamePlayer;
import fr.hyriode.hyrame.game.HyriGameState;
import fr.hyriode.hyrame.game.HyriGameType;
import fr.hyriode.hyrame.game.event.player.HyriGameJoinEvent;
import fr.hyriode.hyrame.game.protocol.HyriSpectatorProtocol;
import fr.hyriode.hyrame.game.team.HyriGameTeam;
import fr.hyriode.hyrame.game.util.HyriGameItems;
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
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.map.MapView;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class LGGame extends HyriGame<LGGamePlayer> {

    private final HyriLaserGame plugin;
    private final LGGameType gameType;

    private final List<LGBonus> bonus;

    private boolean doorOpen;
    private boolean finalKill = false;

    private boolean isStarted = false;

    public LGGame(IHyrame hyrame, HyriLaserGame plugin) {
        super(hyrame, plugin, HyriAPI.get().getGameManager().getGameInfo("lasergame"), LGGamePlayer.class, HyriGameType.getFromData(LGGameType.values()));
        this.gameType = LGGameType.SQUAD;

        this.plugin = plugin;

        this.bonus = new ArrayList<>();

        for (ELGGameTeam team : ELGGameTeam.values())
            this.registerTeam(this.createTeam(team));

    }

    @Override
    public void start() {
        super.start();

        this.isStarted = true;

        this.protocolManager.enableProtocol(new HyriSpectatorProtocol(hyrame, plugin));

        //TODO mettre un message pour expliquer le but du jeu

        HyriLaserGame.WORLD.get().getEntities().stream().filter(entity -> entity instanceof ArmorStand)
                .collect(Collectors.toList()).forEach(Entity::remove);

        this.plugin.getConfiguration().getBonusLocation()
                .forEach(location -> {
                    LGBonus.spawn(location, this.plugin);
                });

        for (LGGamePlayer player : this.players) {
            final LGScoreboard scoreboard = new LGScoreboard(this.plugin, this, player.getPlayer());

            player.setScoreboard(scoreboard);
            scoreboard.show();

            player.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 99999*20, 0));

            player.cleanPlayer();
            player.giveGun();
            player.giveArmor();
        }

        this.teams.forEach(HyriGameTeam::teleportToSpawn);

        new BukkitRunnable(){
            int i = 10;
            @Override
            public void run() {
                if(getState() == HyriGameState.ENDED){
                    this.cancel();
                    return;
                }
                if(i >= 0){
                    players.forEach(p -> {
                        Player player = p.getPlayer();
                        String text = ChatColor.AQUA + "" + i;
                        String msg = ChatColor.DARK_AQUA + HyriLaserGame.getLanguageManager().getValue(player, "game.starting-in") + " ";
                        switch (i){ //TODO refaire ce code foireux
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
                }else {
                    plugin.getConfiguration().getTeams().forEach((team) -> {
                        Location locFirstDoorFirst = team.getDoors().get(0).getFirstPointDoor();
                        Location locSecondDoorFirst = team.getDoors().get(0).getSecondPointDoor();
                        BlockState block = locFirstDoorFirst.clone().getBlock().getState();
                        new BukkitRunnable(){
                            int i = 5;

                            @Override
                            public void run() {
                                if(i <= 0){
                                    doorAnimationClose(locFirstDoorFirst, locSecondDoorFirst, block);
                                    plugin.getGame().setState(HyriGameState.PLAYING);
                                    new BukkitRunnable() {
                                        @Override
                                        public void run() {
                                            kickPlayersInBase(team.getFirstPointBaseArea(), team.getSecondPointBaseArea(), team.getSpawnCloseDoorLocation());
                                            players.forEach(player -> {
                                                player.getPlayer().setExp(1.0F);
                                                new ActionBar(ChatColor.GREEN + HyriLaserGame.getLanguageManager().getValue(player.getPlayer(), "player.death.subtitle.good")).send(player.getPlayer());
                                            });
                                            ((LGLaserGun)hyrame.getItemManager().getItem(LGLaserGun.class)).setEnable(true);
                                        }
                                    }.runTaskLater(plugin, 20L);
                                    cancel();
                                    return;
                                }
                                players.forEach(player -> new ActionBar(ChatColor.GREEN + HyriLaserGame.getLanguageManager().getValue(player.getPlayer(), "game.timer.can-shoot") + " " + i + "s").send(player.getPlayer()));
                                --i;
                            }
                        }.runTaskTimer(plugin, 0, 20);
                        doorAnimationOpen(locFirstDoorFirst, locSecondDoorFirst, block);

                        if(team.getDoors().size() > 1) {
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
        super.handleLogin(p);

//        try {
//            if (this.getState() == HyriGameState.WAITING || this.getState() == HyriGameState.READY) {
//                if (!this.isFull()) {
//                    final LGGamePlayer player = LGGamePlayer.class.getConstructor(HyriGame.class, Player.class).newInstance(this, p);
//
//                    this.players.add(player);
//
////                    this.updatePlayerCount();
//
//                    HyriAPI.get().getEventBus().publish(new HyriGameJoinEvent(this, player));
//
//                    if (this.usingGameTabList) {
//                        this.tabListManager.handleLogin(p);
//                    }
//                }
//            }
//        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
//            p.sendMessage(ChatColor.RED + "An error occurred while joining game! Sending you back to lobby...");
//            HyriAPI.get().getServerManager().sendPlayerToLobby(p.getUniqueId());
//            e.printStackTrace();
//        }

        p.getInventory().setArmorContents(null);
        p.getInventory().clear();
        p.setGameMode(GameMode.ADVENTURE);
        p.setFoodLevel(20);
        p.setHealth(20);
        p.setLevel(0);
        p.setExp(0.0F);
        p.setCanPickupItems(false);
        p.teleport(this.plugin.getConfiguration().getWaitingRoom().getWaitingSpawn());

        this.getPlayer(p.getUniqueId()).setPlugin(this.plugin);
    }

    @Override
    public void handleLogout(Player p) {
        super.handleLogout(p);
        if(this.isStarted){
            if(this.players.isEmpty()) {
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
        this.getWinner().getPlayers().forEach(player -> {
            HyriLGPlayer pl = this.getPlayer(player.getUUID()).getAccount();

            pl.setCurrentWinStreak(pl.getCurrentWinStreak() + 1);
            pl.setBestWinStreak(pl.getBestWinStreak() + 1);
            pl.update((LGGamePlayer) player);
        });
        this.players.forEach(player -> {
            player.setSpectator(true);
            giveResultMap(player.getPlayer());

            HyriLGPlayer pl = this.getPlayer(player.getUUID()).getAccount();
            pl.update(player);
        });
    }

    @SuppressWarnings("deprecation")
    private void giveResultMap(Player player){
        final MapView map = Bukkit.createMap(player.getWorld());
        map.setScale(MapView.Scale.FARTHEST);
        map.removeRenderer(map.getRenderers().get(0));
        map.addRenderer(new LGMapRendererWin(this.plugin));
        player.getInventory().setItem(7, new ItemBuilder(new ItemStack(Material.MAP, 1, map.getId())).withName(ChatColor.YELLOW + HyriLaserGame.getLanguageManager().getValue(player, "map.name")).build());
        player.getInventory().setHeldItemSlot(7);
    }

    public boolean hasSamePoints(){
        return this.getTeamPoints(this.getWinner()) == this.getTeamPoints(this.getLooser());
    }

    public HyriGameTeam getWinner(boolean isWinner){
        HyriGameTeam gameTeam = null;
        for(HyriGameTeam team : this.teams){
            if(gameTeam == null){
                gameTeam = team;
            }else{
                if(isWinner) {
                    if (this.getTeamPoints(team) > this.getTeamPoints(gameTeam)) {
                        gameTeam = team;
                    }
                }else{
                    if(this.getTeamPoints(team) < this.getTeamPoints(gameTeam)){
                        gameTeam = team;
                    }
                }
            }
        }
        return gameTeam;
    }

    public HyriGameTeam getWinner(){
        return this.getWinner(true);
    }

    public HyriGameTeam getLooser(){
        return this.getWinner(false);
    }

    public int getTeamPoints(HyriGameTeam team){
        int pointsTeam = 0;

        for(HyriGamePlayer player : team.getPlayers()){
            pointsTeam += this.getPlayer(player.getUUID()).getAllPoints();
        }

        return pointsTeam;
    }

    private void kickPlayersInBase(Location locFirst, Location locSecond, Location spawnLoc) {
        Area area = new Area(locFirst, locSecond);
        this.plugin.getGame().getPlayers().forEach(player -> {
            if(area.isInArea(player.getPlayer().getLocation())){
                player.getPlayer().teleport(spawnLoc);
            }
        });
    }

    private HyriGameTeam createTeam(ELGGameTeam gameTeam){
        final LGGameTeam team = new LGGameTeam(this, this.plugin, gameTeam, ((LGGameType)this.type).getTeamsSize());
        team.setSpawnLocation(this.plugin.getConfiguration().getTeam(gameTeam.getName()).getSpawnLocation());
        return team;
    }

    private void doorAnimationOpen(Location locFirst, Location locSecond, BlockState blockk){
        this.doorOpen = true;
        Area area = new Area(locFirst, locSecond);
        int fx = area.getMin().getBlockX();
        int fy = area.getMin().getBlockY();
        int fz = area.getMin().getBlockZ();

        int sx = area.getMax().getBlockX();
        int sy = area.getMax().getBlockY();
        int sz = area.getMax().getBlockZ();
        for(int y = fy; y <= sy ; ++y){
            for (int x = fx; x <= sx; ++x) {
                Location loc = new Location(locFirst.getWorld(), x, y, fz).clone();
                Block block = loc.getBlock();
                block.setType(Material.AIR);
                EntityFallingBlock fallingBlock = ((CraftFallingSand) locFirst.getWorld().spawnFallingBlock(loc, blockk.getType(), blockk.getData().getData())).getHandle();
//                EntityFallingBlock fallingBlock = new EntityFallingBlock(((CraftWorld) IHyrame.WORLD.get()).getHandle(), x, y, fz, CraftMagicNumbers.getBlock(blockk.getBlock()).getBlockData());
                fallingBlock.noclip = true;
//                ((CraftWorld) IHyrame.WORLD.get()).getHandle().addEntity(fallingBlock);

                new BukkitRunnable(){
                    int i = 0;
                    @Override
                    public void run() {
                        if(i < 40){
                            i++;
                            fallingBlock.getBukkitEntity().teleport(fallingBlock.getBukkitEntity().getLocation().add(new Vector().setY(-3D)));
                        }else {
                            fallingBlock.getBukkitEntity().remove();
                            cancel();
                        }
                    }
                }.runTaskTimer(this.plugin, 0L, 1L);
            }
        }
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
        }.runTaskTimer(plugin, 0L, 10L);
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

    public LGBonus getBonus(UUID uuid) {
        return this.bonus.stream().filter(bonus -> bonus.getArmorStand().getUniqueId().equals(uuid)).findFirst().orElse(null);
    }

    public List<LGBonus> getBonus() {
        return this.bonus;
    }

    private void refreshAPIPlayer(LGGamePlayer gamePlayer) {
        final HyriLGPlayer account = gamePlayer.getAccount();

        if (this.getState() != HyriGameState.READY && this.getState() != HyriGameState.WAITING) {
            if(account != null) {
                account.update(gamePlayer);
            }
        }
    }

    public HyriGameTeam getAdverseTeam(HyriGameTeam team) {
        for (HyriGameTeam otherTeam : this.teams) {
            if(otherTeam != team)
                return otherTeam;
        }
        return null;
    }

    public void addBonus(LGBonus bonus){
        this.bonus.add(bonus);
    }

    public void removeBonus(UUID uuid){
        this.bonus.removeIf(bonus -> bonus.getArmorStand().getUniqueId() == uuid);
    }
}
