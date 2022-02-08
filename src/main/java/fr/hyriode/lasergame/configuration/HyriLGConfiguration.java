package fr.hyriode.lasergame.configuration;

import fr.hyriode.hyrame.configuration.HyriConfigurationEntry.*;
import fr.hyriode.hyrame.configuration.IHyriConfiguration;
import fr.hyriode.lasergame.HyriLaserGame;
import fr.hyriode.lasergame.game.HyriLGGameTeam;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class HyriLGConfiguration implements IHyriConfiguration {

    private static final Supplier<Location> DEFAULT_LOCATION = () -> new Location(HyriLaserGame.WORLD.get(), 0, 100, 0);

    private final Map<HyriLGGameTeam, Team> teams = new HashMap<>();

    private Location spawnLocation;
    private final LocationEntry spawnLocationEntry;

    private double laserRange;
    private final DoubleEntry laserRangeEntry;

    private boolean friendlyFire;
    private final BooleanEntry friendlyFireEntry;

    private final HyriLaserGame plugin;
    private final FileConfiguration config;

    public HyriLGConfiguration(HyriLaserGame plugin){
        this.plugin = plugin;
        this.config = plugin.getConfig();

        this.spawnLocation = DEFAULT_LOCATION.get();
        this.spawnLocationEntry = new LocationEntry("spawn-location", config);

        this.laserRange = 20D;
        this.laserRangeEntry = new DoubleEntry("laser-range", config);

        this.friendlyFire = false;
        this.friendlyFireEntry = new BooleanEntry("friendly-fire", config);
    }

    @Override
    public void create() {
        for(HyriLGGameTeam team : HyriLGGameTeam.values()){
            final Team teamConfig = new Team(team.getName());
            teamConfig.create();
            this.teams.put(team, teamConfig);
        }
        this.spawnLocationEntry.setDefault(DEFAULT_LOCATION.get());
        this.laserRangeEntry.setDefault(20D);
        this.friendlyFireEntry.setDefault(false);
        this.plugin.saveConfig();
    }

    @Override
    public void load() {
        for(HyriLGGameTeam team : HyriLGGameTeam.values()){
            final Team teamConfig = new Team(team.getName());
            teamConfig.load();
            this.teams.put(team, teamConfig);
        }
        this.spawnLocation = spawnLocationEntry.get();
        this.laserRange = laserRangeEntry.get();
        this.friendlyFire = friendlyFireEntry.get();
    }

    @Override
    public void save() {
        this.teams.forEach((gameTeam, team) -> team.save());
        this.spawnLocationEntry.set(this.spawnLocation);
        this.laserRangeEntry.set(this.laserRange);
        this.friendlyFireEntry.set(this.friendlyFire);
        this.plugin.saveConfig();
    }

    @Override
    public FileConfiguration getConfig() {
        return this.config;
    }

    public Map<HyriLGGameTeam, Team> getTeams() {
        return teams;
    }

    public double getLaserRange() {
        return laserRange;
    }

    public boolean isFriendlyFire() {
        return friendlyFire;
    }

    public Location getSpawnLocation() {
        return spawnLocation;
    }

    public class Team implements IHyriConfiguration {

        private Location firstPointDoor;
        private final LocationEntry firstPointDoorEntry;

        private Location secondPointDoor;
        private final LocationEntry secondPointDoorEntry;

        private Location firstPointSpawn;
        private final LocationEntry firstPointSpawnEntry;

        private Location secondPointSpawn;
        private final LocationEntry secondPointSpawnEntry;

        private Location spawnLocation;
        private final LocationEntry spawnLocationEntry;

        private Location spawnCloseDoorLocation;
        private final LocationEntry spawnCloseDoorLocationEntry;

        public Team(String key){
            key += ".";

            final String spawnKey = key + "spawn.";
            final String spawnAreaKey = key + "spawnarea.";
            final String doorKey = key + "door.";

            this.firstPointDoor = DEFAULT_LOCATION.get();
            this.firstPointDoorEntry = new LocationEntry(doorKey + "firstPoint", config);

            this.secondPointDoor = DEFAULT_LOCATION.get();
            this.secondPointDoorEntry = new LocationEntry(doorKey + "secondPoint", config);

            this.firstPointSpawn = DEFAULT_LOCATION.get();
            this.firstPointSpawnEntry = new LocationEntry(spawnAreaKey + "firstPointArea", config);

            this.secondPointSpawn = DEFAULT_LOCATION.get();
            this.secondPointSpawnEntry = new LocationEntry(spawnAreaKey + "secondPointArea", config);

            this.spawnLocation = DEFAULT_LOCATION.get();
            this.spawnLocationEntry = new LocationEntry(spawnKey + "location", config);

            this.spawnCloseDoorLocation = DEFAULT_LOCATION.get();
            this.spawnCloseDoorLocationEntry = new LocationEntry(spawnKey + "spawnclose", config);
        }

        @Override
        public void create() {
            this.spawnLocationEntry.setDefault(this.spawnLocation);
            this.firstPointDoorEntry.setDefault(this.firstPointDoor);
            this.secondPointDoorEntry.setDefault(this.secondPointDoor);
            this.spawnCloseDoorLocationEntry.setDefault(this.spawnCloseDoorLocation);
            this.firstPointSpawnEntry.setDefault(this.firstPointSpawn);
            this.secondPointSpawnEntry.setDefault(this.secondPointSpawn);
        }

        @Override
        public void load() {
            this.spawnLocation = this.spawnLocationEntry.get();
            this.firstPointDoor = this.firstPointDoorEntry.get();
            this.secondPointDoor = this.secondPointDoorEntry.get();
            this.spawnCloseDoorLocation = this.spawnCloseDoorLocationEntry.get();
            this.firstPointSpawn = this.firstPointSpawnEntry.get();
            this.secondPointSpawn = this.secondPointSpawnEntry.get();
        }

        @Override
        public void save() {
            this.spawnLocationEntry.set(this.spawnLocation);
            this.firstPointDoorEntry.set(this.firstPointDoor);
            this.secondPointDoorEntry.set(this.secondPointDoor);
            this.spawnCloseDoorLocationEntry.set(this.spawnCloseDoorLocation);
            this.firstPointSpawnEntry.set(this.firstPointSpawn);
            this.secondPointSpawnEntry.set(this.secondPointSpawn);
        }

        @Override
        public FileConfiguration getConfig() {
            return config;
        }

        public Location getSpawnLocation() {
            return spawnLocation;
        }

        public Location getFirstPointDoor() {
            return firstPointDoor;
        }

        public Location getSecondPointDoor() {
            return secondPointDoor;
        }

        public Location getSpawnCloseDoorLocation() {
            return spawnCloseDoorLocation;
        }

        public Location getFirstPointSpawn() {
            return firstPointSpawn;
        }

        public Location getSecondPointSpawn() {
            return secondPointSpawn;
        }
    }

}
