package fr.hyriode.lasergame.game;

import fr.hyriode.api.language.HyriLanguageMessage;
import fr.hyriode.hyrame.game.team.HyriGameTeamColor;

import java.util.function.Supplier;

public enum ELGGameTeam {
    RED("red", HyriGameTeamColor.RED),
    BLUE("blue", HyriGameTeamColor.BLUE);

    private final String name;
    private final HyriGameTeamColor color;
    private final Supplier<HyriLanguageMessage> displayName;

    ELGGameTeam(String name, HyriGameTeamColor color) {
        this.name = name;
        this.color = color;
        this.displayName = () -> HyriLanguageMessage.get(this.name + ".display");
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
