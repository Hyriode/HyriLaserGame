package fr.hyriode.lasergame.configuration;

import fr.hyriode.api.config.IHyriConfig;
import fr.hyriode.hyrame.game.waitingroom.HyriWaitingRoom;
import fr.hyriode.hyrame.utils.Area;
import fr.hyriode.hyrame.utils.AreaWrapper;
import fr.hyriode.hyrame.utils.LocationWrapper;
import org.bukkit.Location;

import java.util.*;
import java.util.stream.Collectors;

public class LGConfiguration implements IHyriConfig {

    private final List<Team> teams;

    private final HyriWaitingRoom.Config waitingRoom;
    private final List<LocationWrapper> bonusLocation;
    private final List<LocationWrapper> spawnLocations;

    public LGConfiguration(List<Team> teams, HyriWaitingRoom.Config waitingRoom, List<LocationWrapper> bonusLocation,
                           List<LocationWrapper> spawnLocations){
        this.teams = teams;
        this.waitingRoom = waitingRoom;
        this.bonusLocation = bonusLocation;
        this.spawnLocations = spawnLocations;
    }

    public List<Team> getTeams() {
        return this.teams;
    }

    public Team getTeam(String name){
        return this.teams.stream().filter(team -> team.getName().equals(name)).findFirst().orElse(null);
    }

    public List<Location> getBonusLocation() {
        return this.bonusLocation.stream().map(LocationWrapper::asBukkit).collect(Collectors.toList());
    }

    public List<LocationWrapper> getSpawnLocations() {
        return spawnLocations;
    }

    public HyriWaitingRoom.Config getWaitingRoom() {
        return waitingRoom;
    }

    public static class Team {

        private final String name;

        private final List<AreaWrapper> doors;

        private final AreaWrapper baseArea;

        private final LocationWrapper spawnLocation;

        private final LocationWrapper spawnCloseDoorLocation;

        public Team(String name, List<AreaWrapper> doors,
                    AreaWrapper baseArea,
                    LocationWrapper spawnLocation, LocationWrapper spawnCloseDoorLocation){
            this.name = name;

            this.doors = doors;

            this.baseArea = baseArea;

            this.spawnLocation = spawnLocation;
            this.spawnCloseDoorLocation = spawnCloseDoorLocation;
        }

        public String getName() {
            return name;
        }

        public Location getSpawnLocation() {
            return spawnLocation.asBukkit();
        }

        public List<AreaWrapper> getDoors() {
            return doors;
        }

        public Location getSpawnCloseDoorLocation() {
            return spawnCloseDoorLocation.asBukkit();
        }

        public Area getBaseArea() {
            return baseArea.asArea();
        }
    }

}
