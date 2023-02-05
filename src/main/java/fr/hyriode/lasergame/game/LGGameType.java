package fr.hyriode.lasergame.game;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.hyrame.game.HyriGameType;

public enum LGGameType implements HyriGameType {
    FIVE_FIVE("5v5", 5, 2, 10),
    ;

    private final String name;
    private final int teamsSize;
    private final int minPlayers;
    private final int maxPlayers;

    LGGameType(String name, int teamsSize, int minPlayers, int maxPlayers) {
        this.name = name;
        this.teamsSize = teamsSize;
        this.minPlayers = HyriAPI.get().getConfig().isDevEnvironment() ? 2 : minPlayers;
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
