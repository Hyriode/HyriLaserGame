package fr.hyriode.lasergame.configuration;

import fr.hyriode.hyrame.configuration.HyriConfigurationEntry.*;
import fr.hyriode.hyrame.configuration.IHyriConfiguration;
import fr.hyriode.lasergame.HyriLaserGame;
import fr.hyriode.lasergame.game.LGGameTeam;
import fr.hyriode.lasergame.game.LGGameType;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class LGConfiguration implements IHyriConfiguration {

    private static final Supplier<Location> DEFAULT_LOCATION = () -> new Location(HyriLaserGame.WORLD.get(), 0, 100, 0);

    private LGGameType gameType;
    private final StringEntry gameTypeEntry;

    private final Map<LGGameTeam, Team> teams = new HashMap<>();

    private List<Location> bonusLocation;
    private final ListEntry bonusLocationEntry;

    private Location spawnLocation;
    private final LocationEntry spawnLocationEntry;

    private double laserRange;
    private final DoubleEntry laserRangeEntry;

    private boolean friendlyFire;
    private final BooleanEntry friendlyFireEntry;

    private int timeSecond;
    private final IntegerEntry timeSecondEntry;

    private final HyriLaserGame plugin;
    private final FileConfiguration config;

    public LGConfiguration(HyriLaserGame plugin){
        this.plugin = plugin;
        this.config = plugin.getConfig();

        this.gameType = LGGameType.SQUAD;
        this.gameTypeEntry = new StringEntry("game-type", this.config);

        this.spawnLocation = DEFAULT_LOCATION.get();
        this.spawnLocationEntry = new LocationEntry("spawn-location", this.config);

        this.laserRange = 20;
        this.laserRangeEntry = new DoubleEntry("laser-range", this.config);

        this.friendlyFire = false;
        this.friendlyFireEntry = new BooleanEntry("friendly-fire", this.config);

        this.bonusLocation = new ArrayList<>();
        this.bonusLocationEntry = new ListEntry("location-bonus", this.config);

        this.timeSecond = 600;
        this.timeSecondEntry = new IntegerEntry("time-second", this.config);
    }

    @Override
    public void create() {
        for(LGGameTeam team : LGGameTeam.values()){
            final Team teamConfig = new Team(team.getName());
            teamConfig.create();
            this.teams.put(team, teamConfig);
        }
        this.gameTypeEntry.setDefault(this.gameType.name());
        this.spawnLocationEntry.setDefault(this.spawnLocation);
        this.laserRangeEntry.setDefault(this.laserRange);
        this.friendlyFireEntry.setDefault(this.friendlyFire);
        this.bonusLocationEntry.setDefault(this.bonusLocation);
        this.timeSecondEntry.setDefault(this.timeSecond);
        this.plugin.saveConfig();
    }

    @Override
    public void load() {
        for(LGGameTeam team : LGGameTeam.values()){
            final Team teamConfig = new Team(team.getName());
            teamConfig.load();
            this.teams.put(team, teamConfig);
        }
        this.gameType = LGGameType.valueOf(gameTypeEntry.get().toUpperCase());
        this.spawnLocation = spawnLocationEntry.get();
        this.laserRange = laserRangeEntry.get();
        this.friendlyFire = friendlyFireEntry.get();
        this.bonusLocation = bonusLocationEntry.get().stream().map(o -> (Location)o).collect(Collectors.toList());
        this.timeSecond = timeSecondEntry.get();
    }

    @Override
    public void save() {
        this.teams.forEach((gameTeam, team) -> team.save());
        this.gameTypeEntry.set(this.gameType.name());
        this.spawnLocationEntry.set(this.spawnLocation);
        this.laserRangeEntry.set(this.laserRange);
        this.friendlyFireEntry.set(this.friendlyFire);
        this.bonusLocationEntry.set(this.bonusLocation);
        this.timeSecondEntry.set(this.timeSecond);
        this.plugin.saveConfig();
    }

    @Override
    public FileConfiguration getConfig() {
        return this.config;
    }

    public LGGameType getGameType() {
        return this.gameType;
    }

    public Map<LGGameTeam, Team> getTeams() {
        return this.teams;
    }

    public double getLaserRange() {
        return this.laserRange;
    }

    public boolean isFriendlyFire() {
        return this.friendlyFire;
    }

    public Location getSpawnLocation() {
        return this.spawnLocation;
    }

    public List<Location> getBonusLocation() {
        return this.bonusLocation;
    }

    public int getTimeSecond() {
        return this.timeSecond;
    }

    public class Team implements IHyriConfiguration {

//        private List<Door> doors;
//        private final ListEntry doorsEntry;

        private Location firstPointFirstDoor;
        private final LocationEntry firstPointFirstDoorEntry;

        private Location secondPointFirstDoor;
        private final LocationEntry secondPointFirstDoorEntry;

        private Location firstPointSecondDoor;
        private final LocationEntry firstPointSecondDoorEntry;

        private Location secondPointSecondDoor;
        private final LocationEntry secondPointSecondDoorEntry;

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
            final String doorKey = key + "doors.";

//            this.doors = new ArrayList<>(Arrays.asList(new Door(DEFAULT_LOCATION.get(), DEFAULT_LOCATION.get()), new Door(DEFAULT_LOCATION.get(), DEFAULT_LOCATION.get())));
//            this.doorsEntry = new ListEntry(doorKey, config);

            final String firstDoor = doorKey + "firstDoor.";
            this.firstPointFirstDoor = DEFAULT_LOCATION.get();
            this.firstPointFirstDoorEntry = new LocationEntry(firstDoor + "firstPoint", config);
            this.secondPointFirstDoor = DEFAULT_LOCATION.get();
            this.secondPointFirstDoorEntry = new LocationEntry(firstDoor + "secondPoint", config);

            final String secondDoor = doorKey + "secondDoor.";
            this.firstPointSecondDoor = null;
            this.firstPointSecondDoorEntry = new LocationEntry(secondDoor + "firstPoint", config);
            this.secondPointSecondDoor = null;
            this.secondPointSecondDoorEntry = new LocationEntry(secondDoor + "secondPoint", config);


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

//            this.doorsEntry.setDefault(this.doors);

            this.spawnCloseDoorLocationEntry.setDefault(this.spawnCloseDoorLocation);

            this.firstPointFirstDoorEntry.setDefault(this.firstPointFirstDoor);
            this.secondPointFirstDoorEntry.setDefault(this.secondPointFirstDoor);
            this.firstPointSecondDoorEntry.setDefault(this.firstPointSecondDoor);
            this.secondPointSecondDoorEntry.setDefault(this.secondPointSecondDoor);

            this.firstPointSpawnEntry.setDefault(this.firstPointSpawn);
            this.secondPointSpawnEntry.setDefault(this.secondPointSpawn);
        }

        @Override
        public void load() {
            this.spawnLocation = this.spawnLocationEntry.get();
            this.spawnCloseDoorLocation = this.spawnCloseDoorLocationEntry.get();

//           this.doors = this.doorsEntry.get().stream().map(o -> (Door)o).collect(Collectors.toList());
            this.firstPointFirstDoor = this.firstPointFirstDoorEntry.get();
            this.secondPointFirstDoor = this.secondPointFirstDoorEntry.get();

            this.firstPointSecondDoor = this.firstPointSecondDoorEntry.get();
            this.secondPointSecondDoor = this.secondPointSecondDoorEntry.get();

            this.firstPointSpawn = this.firstPointSpawnEntry.get();
            this.secondPointSpawn = this.secondPointSpawnEntry.get();
        }

        @Override
        public void save() {
            this.spawnLocationEntry.set(this.spawnLocation);
            this.firstPointFirstDoorEntry.set(this.firstPointFirstDoor);
            this.secondPointFirstDoorEntry.set(this.secondPointFirstDoor);

            this.firstPointSecondDoorEntry.set(this.firstPointSecondDoor);
            this.secondPointSecondDoorEntry.set(this.secondPointSecondDoor);
//            this.doorsEntry.set(this.doors);
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

        public Location getFirstPointFirstDoor() {
            return firstPointFirstDoor;
        }

        public Location getSecondPointFirstDoor() {
            return secondPointFirstDoor;
        }

        public Location getFirstPointSecondDoor() {
            return firstPointSecondDoor;
        }

        public Location getSecondPointSecondDoor() {
            return secondPointSecondDoor;
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

//    public class Door{
//
//        private Location firstPointDoor;
//        private Location secondPointDoor;
//
//        public Door(String key, Location firstPointDoor, Location secondPointDoor){
//            this.firstPointDoor = firstPointDoor;
//            this.firstPointDoorEntry = new LocationEntry("", config);
//            this.secondPointDoor = secondPointDoor;
//            this.secondPointDoorEntry = new LocationEntry("", config);
//        }
//
//        public Location getFirstPointDoor() {
//            return firstPointDoor;
//        }
//
//        public Location getSecondPointDoor() {
//            return secondPointDoor;
//        }
//
//
//    }

}
