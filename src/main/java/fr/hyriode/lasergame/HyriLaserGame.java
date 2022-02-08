package fr.hyriode.lasergame;

import fr.hyriode.hyrame.HyrameLoader;
import fr.hyriode.hyrame.IHyrame;
import fr.hyriode.hyrame.language.HyriLanguageMessage;
import fr.hyriode.lasergame.configuration.HyriLGConfiguration;
import fr.hyriode.lasergame.game.HyriLGGame;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.function.Supplier;
import java.util.logging.Level;

public class HyriLaserGame extends JavaPlugin {

    public static final Supplier<World> WORLD = () -> Bukkit.getWorld("world");

    private IHyrame hyrame;
    private HyriLGGame game;

    private HyriLGConfiguration configuration;

    @Override
    public void onEnable() {
        this.getLogger().log(Level.INFO, "Starting " + this.getClass().getSimpleName() + "...");
        this.configuration = new HyriLGConfiguration(this);
        this.configuration.create();
        this.configuration.load();

        this.hyrame = HyrameLoader.load(new HyriLGProvider(this));
        this.game = new HyriLGGame(this.hyrame, this);
        this.hyrame.getGameManager().registerGame(this.game);
    }

    @Override
    public void onDisable() {
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
}
