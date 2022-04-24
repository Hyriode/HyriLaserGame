package fr.hyriode.lasergame.game;

import fr.hyriode.hyrame.game.HyriGameType;

public enum LGGameType implements HyriGameType {
    FOUR_FOUR("4v4", 4, 6, 8),
    FIVE_FIVE("5v5", 5, 8, 10),
    SIX_SIX("6v6", 6, 10, 12)
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
        return this.name();
    }

    @Override
    public String getDisplayName() {
        return this.name;
    }

    public int getTeamsSize() {
        return teamsSize;
    }

    @Override
    public int getMinPlayers() {
        return minPlayers;
    }

    @Override
    public int getMaxPlayers() {
        return maxPlayers;
    }
}
