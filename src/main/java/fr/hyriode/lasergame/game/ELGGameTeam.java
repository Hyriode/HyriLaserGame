package fr.hyriode.lasergame.game;

import fr.hyriode.hyrame.game.team.HyriGameTeamColor;
import fr.hyriode.hyrame.language.HyriLanguageMessage;
import fr.hyriode.lasergame.HyriLaserGame;

import java.util.function.Supplier;

public enum ELGGameTeam {
    RED("red", HyriGameTeamColor.RED),
    BLUE("blue", HyriGameTeamColor.BLUE);

    private final String name;
    private final HyriGameTeamColor color;
    private final Supplier<HyriLanguageMessage> displayName;

    ELGGameTeam(String name, HyriGameTeamColor color){
        this.name = name;
        this.color = color;
        this.displayName = () -> HyriLaserGame.getLanguageManager().getMessage(this.name + ".display");
    }

    public String getName() {
        return this.name;
    }

    public HyriGameTeamColor getColor() {
        return color;
    }

    public Supplier<HyriLanguageMessage> getDisplayName() {
        return displayName;
    }
}
