package fr.hyriode.lasergame.game;

import fr.hyriode.hyrame.game.HyriGameType;

public enum LGGameType implements HyriGameType {
    SQUAD("Squad", 4, 6, 8),
    QUINTUPLE("Quintuple", 5, 8, 10),
    SEXTUPLE("Sextuple", 6, 10, 12)
    ;

    private final String name;
    private final int teamsSize;
    private final int minPlayers;
    private final int maxPlayers;

    LGGameType(String name, int teamsSize, int minPlayers, int maxPlayers){
        this.name = name;
        this.teamsSize = teamsSize;
        this.minPlayers = minPlayers;
        this.maxPlayers = maxPlayers;
    }

    @Override
    public String getName() {
        return name;
    }

    public int getTeamsSize() {
        return teamsSize;
    }

    public int getMinPlayers() {
        return minPlayers;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }
}
