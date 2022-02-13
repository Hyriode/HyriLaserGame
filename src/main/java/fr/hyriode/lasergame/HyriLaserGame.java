package fr.hyriode.lasergame;

import fr.hyriode.hyrame.HyrameLoader;
import fr.hyriode.hyrame.IHyrame;
import fr.hyriode.hyrame.language.IHyriLanguageManager;
import fr.hyriode.lasergame.configuration.HyriLGConfiguration;
import fr.hyriode.lasergame.game.HyriLGGame;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.*;
import java.util.function.Supplier;
import java.util.logging.Level;

public class HyriLaserGame extends JavaPlugin {

    public static final Supplier<World> WORLD = () -> Bukkit.getWorld("world");

    private static IHyriLanguageManager languageManager;

    private IHyrame hyrame;
    private HyriLGGame game;

    private Image mapImage;
    private Font minecraftFont;

    private HyriLGConfiguration configuration;

    @Override
    public void onEnable() {
        this.getLogger().log(Level.INFO, "Starting " + this.getClass().getSimpleName() + "...");
        this.configuration = new HyriLGConfiguration(this);
        this.configuration.create();
        this.configuration.load();
        this.hyrame = HyrameLoader.load(new HyriLGProvider(this));

        languageManager = this.hyrame.getLanguageManager();

        this.game = new HyriLGGame(this.hyrame, this);
        this.hyrame.getGameManager().registerGame(this.game);

        try {
            mapImage = ImageIO.read(getRessource("map/map-lasergame-background.png"));
            minecraftFont = Font.createFont(0, getRessource("font/minecraft.ttf"));
        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDisable() { }

    public Font getMinecraftFont() {
        return minecraftFont;
    }

    public Image getMapImage() {
        return mapImage;
    }

    public HyriLGGame getGame() {
        return this.game;
    }

    public IHyrame getHyrame() {
        return hyrame;
    }

    public HyriLGConfiguration getConfiguration() {
        return configuration;
    }

    public InputStream getRessource(String path) {
        return getClass().getClassLoader().getResourceAsStream(path);
    }

    public static IHyriLanguageManager getLanguageManager() {
        return languageManager;
    }
}
