package fr.hyriode.lasergame.game;

import fr.hyriode.hyrame.game.HyriGame;
import fr.hyriode.hyrame.game.team.HyriGameTeam;
import fr.hyriode.hyrame.scoreboard.team.HyriScoreboardTeam;
import fr.hyriode.lasergame.HyriLaserGame;
import fr.hyriode.lasergame.configuration.LGConfiguration;
import org.bukkit.Location;

public class LGGameTeam extends HyriGameTeam {

    private final HyriLaserGame plugin;

    public LGGameTeam(HyriLaserGame plugin, ELGGameTeam color, int teamSize) {
        super(color.getName(), color.getDisplayName().get(), color.getColor(), false, HyriScoreboardTeam.NameTagVisibility.NEVER, teamSize);
        this.plugin = plugin;
        this.initConfig();
    }

    private void initConfig(){
//        LGConfiguration.Team config = this.plugin.getConfiguration().getTeam(this.getName());


    }

    public LGConfiguration.Team getConfig() {
        return this.plugin.getConfiguration().getTeam(this.getName());
    }

    public void teleportToSpawn() {
        this.teleport(this.plugin.getConfiguration().getTeam(this.getName()).getSpawnLocation().add(0, 1, 0));
    }
}
