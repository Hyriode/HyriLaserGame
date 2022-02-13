package fr.hyriode.lasergame.game.scoreboard;

import fr.hyriode.hyrame.game.HyriGameState;
import fr.hyriode.hyrame.game.scoreboard.HyriScoreboardIpConsumer;
import fr.hyriode.hyrame.scoreboard.Scoreboard;
import fr.hyriode.hyrame.title.Title;
import fr.hyriode.lasergame.HyriLaserGame;
import fr.hyriode.lasergame.game.player.HyriLGPlayer;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class HyriLGScoreboard extends Scoreboard {

    private final HyriLaserGame plugin;
    private final Player player;

    private int timeSecond;

    public HyriLGScoreboard(HyriLaserGame plugin, Player player) {
        super(plugin, player.getPlayer(), "lasergame", ChatColor.DARK_AQUA + "     " + ChatColor.BOLD + plugin.getGame().getDisplayName() + "     ");
        this.plugin = plugin;
        this.player = player;
        this.timeSecond = plugin.getConfiguration().getTimeSecond();

        this.setLine(0, this.getDateLine(), scoreboardLine -> scoreboardLine.setValue(this.getDateLine()), 20);
        this.setLine(1, "  ");
        this.setLine(2, this.getTimeLine(), line -> line.setValue(this.getTimeLine()), 20);
        this.setLine(3, this.getBonusLine(), line -> line.setValue(this.getBonusLine()), 1);
        this.setLine(4, "   ");
        this.setLine(5, ChatColor.DARK_AQUA + "hyriode.fr", new HyriScoreboardIpConsumer("hyriode.fr"), 2);
    }

    private String getBonusLine(){
        if(this.plugin.getGame().getState() == HyriGameState.ENDED)
            return ChatColor.DARK_AQUA + "Points: " + ChatColor.AQUA + this.plugin.getGame().getTeamPoints(this.plugin.getGame().getWinner());
        return ChatColor.DARK_AQUA + "Bonus: " + ChatColor.AQUA +(this.getPlayerGame().hasBonus() ? this.getPlayerGame().getBonus().getLanguageName().getForPlayer(player.getPlayer()) : "Aucun");
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
                    for(HyriLGPlayer player : this.plugin.getGame().getPlayers()) {
                        Title.sendTitle(player.getPlayer(), ChatColor.RED + this.plugin.getHyrame().getLanguageManager().getValue(player.getPlayer(), "game.suddendeath.title"), this.plugin.getHyrame().getLanguageManager().getValue(player.getPlayer(), "game.suddendeath.subtitle"), 1, 20*3, 1);
                    }
                }
            }
        }
        int sec = timeSecond % 60;
        int min = (timeSecond / 60)%60;
        if(this.plugin.getGame().getState() != HyriGameState.ENDED && !this.plugin.getGame().isFinalKill())
            return ChatColor.DARK_AQUA + this.getLinePrefix("time") + ChatColor.AQUA + (min + "m" + sec + "s");
        else if(this.plugin.getGame().getState() == HyriGameState.ENDED)
            return ChatColor.DARK_AQUA + this.getLinePrefix("winner") + ChatColor.AQUA + this.plugin.getGame().getWinner().getDisplayName().getForPlayer(player.getPlayer());
        else
            return ChatColor.DARK_AQUA + this.getLinePrefix("time") + ChatColor.AQUA + this.getLinePrefix("suddendeath");
    }

    private String getLinePrefix(String prefix) {
        return this.plugin.getHyrame().getLanguageManager().getValue(this.player.getPlayer(), "scoreboard." + prefix + ".display");
    }

    private String getDateLine() {
        final SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm");

        format.setTimeZone(TimeZone.getTimeZone("GMT+1"));

        return ChatColor.GRAY + format.format(new Date());
    }

    private HyriLGPlayer getPlayerGame(){
        return this.plugin.getGame().getPlayer(player.getUniqueId());
    }
}
