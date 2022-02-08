package fr.hyriode.lasergame.game;

import fr.hyriode.hyrame.game.team.HyriGameTeamColor;

public enum HyriLGGameTeam {
    RED("red", HyriGameTeamColor.RED),
    BLUE("blue", HyriGameTeamColor.BLUE);

    private final String name;
    private final HyriGameTeamColor color;

    HyriLGGameTeam(String name, HyriGameTeamColor color){
        this.name = name;
        this.color = color;
    }

    public String getName() {
        return this.name;
    }

    public HyriGameTeamColor getColor() {
        return color;
    }
}
