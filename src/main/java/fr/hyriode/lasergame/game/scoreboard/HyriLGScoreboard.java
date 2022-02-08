package fr.hyriode.lasergame.game.scoreboard;

import fr.hyriode.hyrame.game.scoreboard.HyriScoreboardIpConsumer;
import fr.hyriode.hyrame.scoreboard.Scoreboard;
import fr.hyriode.hyrame.utils.Symbols;
import fr.hyriode.lasergame.HyriLaserGame;
import fr.hyriode.lasergame.game.HyriLGGameTeam;
import fr.hyriode.lasergame.game.HyriLGPlayer;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class HyriLGScoreboard extends Scoreboard {

    private final HyriLaserGame plugin;

    public HyriLGScoreboard(HyriLaserGame plugin, Player player) {
        super(plugin, player, "lasergame", ChatColor.DARK_AQUA + "     " + ChatColor.BOLD + plugin.getGame().getDisplayName() + "     ");
        this.plugin = plugin;

        this.addLines();

        this.setLine(0, this.getDateLine(), scoreboardLine -> scoreboardLine.setValue(this.getDateLine()), 20);
        this.setLine(6, this.getTimeLine(), line -> line.setValue(this.getTimeLine()), 20);
        this.setLine(7, ChatColor.DARK_AQUA + "hyriode.fr", new HyriScoreboardIpConsumer("hyriode.fr"), 2);
    }

    private void addLines() {
        this.setLine(1, "  ");
        this.setLine(2, "  ");
        this.setLine(3, "  ");
//        this.setLine(4, this.getKillsLine());
//        this.setLine(5, this.getDeathsLine());
        this.setLine(4, "   ");
        this.setLine(5, "    ");
    }

    public void update() {
        this.addLines();

        this.updateLines();
    }

    private HyriLGPlayer getGamePlayer() {
        return this.plugin.getGame().getPlayer(this.player.getUniqueId());
    }

    private String getKillsLine() {
        return this.getLinePrefix("kills") + ChatColor.AQUA + this.getGamePlayer().getKills();
    }

    private String getDeathsLine() {
        return this.getLinePrefix("deaths") + ChatColor.AQUA + this.getGamePlayer().getDeaths();
    }

    private String getTimeLine() {
        final SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");

        format.setTimeZone(TimeZone.getTimeZone("GMT"));

        final String line = format.format(this.plugin.getGame().getGameTime() * 1000);

        return this.getLinePrefix("time") + ChatColor.AQUA + (line.startsWith("00:") ? line.substring(3) : line);
    }

    private String getLinePrefix(String prefix) {
        return this.plugin.getHyrame().getLanguageManager().getValue(this.player, "scoreboard." + prefix + ".display");
    }

    private String getDateLine() {
        final SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm");

        format.setTimeZone(TimeZone.getTimeZone("GMT+1"));

        return ChatColor.GRAY + format.format(new Date());
    }
}
