package fr.hyriode.lasergame.game.map;

import fr.hyriode.api.language.HyriLanguageMessage;
import fr.hyriode.hyrame.game.team.HyriGameTeam;
import fr.hyriode.lasergame.HyriLaserGame;
import fr.hyriode.lasergame.game.player.LGGamePlayer;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.map.*;

import java.awt.*;
import java.awt.image.BufferedImage;

public class LGMapRendererWin extends MapRenderer {

    private final HyriLaserGame plugin;

    private boolean hasRendered = false;

    public LGMapRendererWin(HyriLaserGame plugin){
        this.plugin = plugin;
    }

    @Override
    public void render(MapView mapView, MapCanvas mapCanvas, Player player) {
        if(!hasRendered) {
            final LGGamePlayer lgPlayer = this.plugin.getGame().getPlayer(player.getUniqueId());
            final HyriGameTeam winner = this.plugin.getGame().getWinner();
            final HyriGameTeam looser = this.plugin.getGame().getLooser();
            final boolean win = winner.contains(lgPlayer);

            final Font font = this.plugin.getMinecraftFont();
            final BufferedImage image = new BufferedImage(128, 128, 1);
            final Graphics graphics = image.getGraphics();

            graphics.drawImage(this.plugin.getMapImage(), 0, 0, null);

            graphics.setColor(Color.decode("#FFAA00"));

            String title = this.getKeyTitle(player, "victory");
            if (!win) {
                graphics.setColor(Color.decode("#FF0000"));
                title = this.getKeyTitle(player, "defeat");
            }

            //Title
            drawCenteredString(graphics, title, new Rectangle(128, 40), font.deriveFont(15F));

            //Score
            /*Winner*/
            String winnerTitle = winner.getDisplayName().getValue(player) + ": ";

            graphics.setColor(new Color(winner.getColor().getDyeColor().getColor().asRGB()));
            drawString(graphics, winnerTitle, 10, 50, font.deriveFont(8F));
            int posWinnerX = winnerTitle.length() * (graphics.getFont().getSize() / 2 + 1);

            graphics.setColor(Color.WHITE);
            drawString(graphics, ""+this.plugin.getGame().getTeamPoints(winner), 10 + posWinnerX, 50, font.deriveFont(8F));

            /*Looser*/
            String looserTitle = looser.getDisplayName().getValue(player) + ": ";

            graphics.setColor(new Color(looser.getColor().getDyeColor().getColor().asRGB()));
            drawString(graphics, looserTitle, 10, 60, font.deriveFont(8F));
            int posLooserX = looserTitle.length() * (graphics.getFont().getSize() / 2 + 1);
            graphics.setColor(Color.WHITE);
            drawString(graphics, ""+this.plugin.getGame().getTeamPoints(looser), 10 + posLooserX, 60, font.deriveFont(8F));

            /*Score of the Player*/

            graphics.setColor(Color.decode("#77B2BF"));
            String playerTitle = this.getKeyPlayer(lgPlayer.getPlayer(), "title") + ": ";
            drawString(graphics, playerTitle, 10, 80, font.deriveFont(8F));
            int posPlayerX = playerTitle.length() * (graphics.getFont().getSize() / 2 + 1);
            graphics.setColor(Color.WHITE);
            drawString(graphics, ""+lgPlayer.getPlayerPoints(), 10 + posPlayerX, 80, font.deriveFont(8F));

            drawString(graphics, "- " + this.getKeyPlayer(lgPlayer.getPlayer(), "kills") + ": " + lgPlayer.getKills(), 10, 90, font.deriveFont(8F));
            drawString(graphics, "- " + this.getKeyPlayer(lgPlayer.getPlayer(), "deaths") + ": " + lgPlayer.getDeaths(), 10, 100, font.deriveFont(8F));

            //Draw map
            mapCanvas.drawImage(0, 0, image);

            hasRendered = true;
        }

    }

    private String getKeyPlayer(Player player, String key){
        return HyriLanguageMessage.get("map.player." + key).getValue(player);
    }

    private String getKeyTitle(Player player, String key){
        return HyriLanguageMessage.get("map.title." + key).getValue(player);
    }

    public void drawCenteredString(Graphics g, String text, Rectangle rect, Font font) {
        // Get the FontMetrics
        FontMetrics metrics = g.getFontMetrics(font);
        // Determine the X coordinate for the text
        int x = rect.x + (rect.width - metrics.stringWidth(text)) / 2;
        // Determine the Y coordinate for the text (note we add the ascent, as in java 2d 0 is top of the screen)
        int y = rect.y + ((rect.height - metrics.getHeight()) / 2) + metrics.getAscent();
        // Set the font
        g.setFont(font);
        // Draw the String
        g.drawString(text, x, y);
    }

    public void drawString(Graphics g, String text, int x, int y, Font font) {
        // Set the font
        g.setFont(font);
        // Draw the String
        g.drawString(text, x, y);
    }
}
