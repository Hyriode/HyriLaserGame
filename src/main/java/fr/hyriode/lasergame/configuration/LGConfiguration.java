package fr.hyriode.lasergame.configuration;

import fr.hyriode.hyrame.utils.Area;
import fr.hyriode.hyrame.utils.LocationWrapper;
import fr.hyriode.hystia.api.config.IConfig;
import org.bukkit.Location;

import java.util.List;
import java.util.stream.Collectors;

public class LGConfiguration implements IConfig {

    private final List<Team> teams;

    private final WaitingRoom waitingRoom;
    private final List<LocationWrapper> bonusLocation;
    private final List<LocationWrapper> spawnLocations;
    private final double laserRange;
    private final boolean friendlyFire;
    private final int timeSecond;

    public LGConfiguration(List<Team> teams, WaitingRoom waitingRoom, List<LocationWrapper> bonusLocation,
                           List<LocationWrapper> spawnLocations, double laserRange,
                           boolean friendlyFire, int timeSecond) {
        this.teams = teams;
        this.waitingRoom = waitingRoom;
        this.bonusLocation = bonusLocation;
        this.spawnLocations = spawnLocations;
        this.laserRange = laserRange;
        this.friendlyFire = friendlyFire;
        this.timeSecond = timeSecond;
    }

    public List<Team> getTeams() {
        return this.teams;
    }

    public Team getTeam(String name) {
        return this.teams.stream().filter(team -> team.getName().equals(name)).findFirst().orElse(null);
    }

    public double getLaserRange() {
        return this.laserRange;
    }

    public boolean isFriendlyFire() {
        return this.friendlyFire;
    }

    public List<Location> getBonusLocation() {
        return this.bonusLocation.stream().map(LocationWrapper::asBukkit).collect(Collectors.toList());
    }

    public List<LocationWrapper> getSpawnLocations() {
        return spawnLocations;
    }

    public int getTimeSecond() {
        return this.timeSecond;
    }

    public WaitingRoom getWaitingRoom() {
        return waitingRoom;
    }

    public static class Team {

        private final String name;

        private final List<Door> doors;

        private final LocationWrapper firstPointBaseArea;

        private final LocationWrapper secondPointBaseArea;

        private final LocationWrapper spawnLocation;

        private final LocationWrapper spawnCloseDoorLocation;

        public Team(String name, List<Door> doors,
                    LocationWrapper firstPointBaseArea, LocationWrapper secondPointBaseArea,
                    LocationWrapper spawnLocation, LocationWrapper spawnCloseDoorLocation) {
            this.name = name;

            this.doors = doors;

            this.firstPointBaseArea = firstPointBaseArea;
            this.secondPointBaseArea = secondPointBaseArea;

            this.spawnLocation = spawnLocation;
            this.spawnCloseDoorLocation = spawnCloseDoorLocation;
        }

        public String getName() {
            return name;
        }

        public Location getSpawnLocation() {
            return spawnLocation.asBukkit();
        }

        public List<Door> getDoors() {
            return doors;
        }

        public Location getSpawnCloseDoorLocation() {
            return spawnCloseDoorLocation.asBukkit();
        }

        public Location getFirstPointBaseArea() {
            return firstPointBaseArea.asBukkit();
        }

        public Location getSecondPointBaseArea() {
            return secondPointBaseArea.asBukkit();
        }
    }

    public static class WaitingRoom {

        private final LocationWrapper waitingSpawn;

        private final LocationWrapper waitingSpawnPos1;
        private final LocationWrapper waitingSpawnPos2;

        public WaitingRoom(LocationWrapper waitingSpawn, LocationWrapper waitingSpawnPos1, LocationWrapper waitingSpawnPos2) {
            this.waitingSpawn = waitingSpawn;
            this.waitingSpawnPos1 = waitingSpawnPos1;
            this.waitingSpawnPos2 = waitingSpawnPos2;
        }

        public Location getWaitingSpawn() {
            return waitingSpawn.asBukkit();
        }

        public Location getWaitingSpawnPos1() {
            return waitingSpawnPos1.asBukkit();
        }

        public Location getWaitingSpawnPos2() {
            return waitingSpawnPos2.asBukkit();
        }

        public Area getArea() {
            return new Area(this.waitingSpawnPos1.asBukkit(), this.waitingSpawnPos2.asBukkit());
        }
    }

    public static class Door {

        private final LocationWrapper firstPointDoor;
        private final LocationWrapper secondPointDoor;

        public Door(LocationWrapper firstPointDoor, LocationWrapper secondPointDoor) {
            this.firstPointDoor = firstPointDoor;
            this.secondPointDoor = secondPointDoor;
        }

        public Location getFirstPointDoor() {
            return firstPointDoor.asBukkit();
        }

        public Location getSecondPointDoor() {
            return secondPointDoor.asBukkit();
        }


    }

}
