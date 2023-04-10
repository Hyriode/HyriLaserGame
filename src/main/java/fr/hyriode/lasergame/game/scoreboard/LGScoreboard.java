package fr.hyriode.lasergame.game.scoreboard;

import fr.hyriode.api.language.HyriLanguageMessage;
import fr.hyriode.hyrame.game.HyriGameState;
import fr.hyriode.hyrame.game.scoreboard.HyriGameScoreboard;
import fr.hyriode.hyrame.game.scoreboard.IPLine;
import fr.hyriode.hyrame.game.team.HyriGameTeam;
import fr.hyriode.hyrame.title.Title;
import fr.hyriode.lasergame.HyriLaserGame;
import fr.hyriode.lasergame.game.LGGame;
import fr.hyriode.lasergame.game.player.LGGamePlayer;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class LGScoreboard extends HyriGameScoreboard<LGGame> {

    private final HyriLaserGame plugin;
    private int timeSecond;

    public LGScoreboard(HyriLaserGame plugin, Player player) {
        super(plugin, plugin.getGame(), player, "lasergame");
        this.plugin = plugin;
        this.timeSecond = plugin.getConfiguration().getTimeSecond();

        this.setLine(0, this.getDateLine(), scoreboardLine -> scoreboardLine.setValue(this.getDateLine()), 20);
        this.addBlankLine(1);
        this.setLine(2, this.getTeamLine());
        this.setLine(3, this.getTimeLine(), line -> line.setValue(this.getTimeLine()), 20);
        this.setLine(4, this.getBonusLine(), line -> line.setValue(this.getBonusLine()), 1);
        this.addBlankLine(5);
        this.setLine(6, this.getPointsLine(), line -> line.setValue(this.getPointsLine()), 2);
        this.setLine(7, this.getPointsAdverseLine(), line -> line.setValue(this.getPointsAdverseLine()), 2);
        this.setLine(8, "    ");
        this.setLine(9, ChatColor.DARK_AQUA + "hyriode.fr", new IPLine("hyriode.fr"), 2);
    }

    private String getTeamLine(){
        HyriGameTeam team = this.getPlayerGame().getTeam();
        return ChatColor.WHITE + this.getLinePrefix("team") + ": " + team.getColor().getChatColor() + team.getDisplayName().getValue(this.getPlayer());
    }

    private String getPointsLine(){
        HyriGameTeam team = this.getPlayerGame().getTeam();
        return team.getColor().getChatColor() + team.getDisplayName().getValue(this.player) + ": " + ChatColor.WHITE + this.game.getTeamPoints(team) + " ";
    }

    private String getPointsAdverseLine(){
        HyriGameTeam team = this.game.getAdverseTeam(this.getPlayerGame().getTeam());
        return team.getColor().getChatColor() + team.getDisplayName().getValue(this.player) + ": " + ChatColor.WHITE + this.game.getTeamPoints(team);
    }

    private String getBonusLine(){
        if(this.plugin.getGame().getState() == HyriGameState.ENDED)
            return ChatColor.WHITE + this.getLinePrefix("points") + ": " + ChatColor.AQUA + this.plugin.getGame().getTeamPoints(this.plugin.getGame().getWinner());
        return ChatColor.WHITE + this.getLinePrefix("bonus") + ": " + ChatColor.AQUA + (this.getPlayerGame().hasBonus() ? this.getPlayerGame().getBonus().getLanguageName().getValue(player.getPlayer()) : HyriLanguageMessage.get("bonus.unknown").getValue(this.player));
    }

    private String getTimeLine() {
        if(this.plugin.getGame().getState() != HyriGameState.ENDED && !this.plugin.getGame().isFinalKill()) {
            if (this.plugin.getGame().isDoorOpen())
                --timeSecond;
            if (timeSecond <= 0) {
                if(!this.plugin.getGame().hasSamePoints()) {
                    this.plugin.getGame().win(this.plugin.getGame().getWinner());
                }else{
                    this.plugin.getGame().setFinalKill();
                    for(LGGamePlayer player : this.plugin.getGame().getPlayers()) {
                        Title.sendTitle(player.getPlayer(), ChatColor.RED + HyriLanguageMessage.get("game.suddendeath.title").getValue(player.getPlayer()), HyriLanguageMessage.get("game.suddendeath.subtitle").getValue(player.getPlayer()), 1, 20*3, 1);
                    }
                }
            }
        }
        int sec = timeSecond % 60;
        int min = (timeSecond / 60) % 60;
        if(this.plugin.getGame().getState() != HyriGameState.ENDED && !this.plugin.getGame().isFinalKill())
            return ChatColor.WHITE + this.getLinePrefix("time") + ChatColor.AQUA + (min + "m" + sec + "s");
        else if(this.plugin.getGame().getState() == HyriGameState.ENDED) {
            HyriGameTeam team = this.plugin.getGame().getWinner();
            return ChatColor.WHITE + this.getLinePrefix("winner") + team.getColor().getChatColor() + team.getDisplayName().getValue(player.getPlayer());
        }else
            return ChatColor.WHITE + this.getLinePrefix("time") + ChatColor.AQUA + this.getLinePrefix("suddendeath");
    }

    private String getLinePrefix(String prefix) {
        return HyriLanguageMessage.get("scoreboard." + prefix + ".display").getValue(this.player.getPlayer());
    }

    private String getDateLine() {
        final SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm");

        format.setTimeZone(TimeZone.getTimeZone("GMT+1"));

        return ChatColor.GRAY + format.format(new Date());
    }

    private LGGamePlayer getPlayerGame(){
        return this.plugin.getGame().getPlayer(player.getUniqueId());
    }
}
