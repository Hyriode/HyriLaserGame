package fr.hyriode.lasergame;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.server.IHyriServer;
import fr.hyriode.hyrame.HyrameLoader;
import fr.hyriode.hyrame.IHyrame;
import fr.hyriode.hyrame.language.IHyriLanguageManager;
import fr.hyriode.lasergame.configuration.LGConfiguration;
import fr.hyriode.lasergame.game.LGGame;
import fr.hyriode.lasergame.game.LGGameType;
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
    private LGGame game;

    private Image mapImage;
    private Font minecraftFont;

    private LGConfiguration configuration;

    @Override
    public void onEnable() {
        this.getLogger().log(Level.INFO, "Starting " + this.getClass().getSimpleName() + "...");
        if(!HyriAPI.get().getConfiguration().isDevEnvironment()){
            this.configuration = HyriAPI.get().getServer().getConfig(LGConfiguration.class);
        }
//        CompletableFuture<Boolean> ok = HyriAPI.get().getHystiaAPI().getConfigManager().saveConfig(new LGConfiguration(Arrays.asList(
//                new LGConfiguration.Team(
//                        "red",
//                        Arrays.asList(
//                                new LGConfiguration.Door(
//                                        new LocationWrapper(IHyrame.WORLD.get().getUID(),-60, 146, -6),
//                                        new LocationWrapper(IHyrame.WORLD.get().getUID(), -62, 144, -6)
//                                ),
//                                new LGConfiguration.Door(
//                                        new LocationWrapper(IHyrame.WORLD.get().getUID(), -60, 146, 8),
//                                        new LocationWrapper(IHyrame.WORLD.get().getUID(), -62, 144, 8)
//                                )
//                        ), //doors
//                        new LocationWrapper(IHyrame.WORLD.get().getUID(), -63, 147, -6), //first area
//                        new LocationWrapper(IHyrame.WORLD.get().getUID(), -51, 144, 8), //second area
//                        new LocationWrapper(IHyrame.WORLD.get().getUID(), -53.5, 145, 1.5, 90, 0), //spawn loc
//                        new LocationWrapper(IHyrame.WORLD.get().getUID(), -48.5, 145, 1.5, -90, 0) //spawn close
//                ),
//                new LGConfiguration.Team(
//                        "blue",
//                        Arrays.asList(
//                                new LGConfiguration.Door(
//                                        new LocationWrapper(IHyrame.WORLD.get().getUID(), 52, 146, -6),
//                                        new LocationWrapper(IHyrame.WORLD.get().getUID(), 54, 144, -6)
//                                ),
//                                new LGConfiguration.Door(
//                                        new LocationWrapper(IHyrame.WORLD.get().getUID(), 54, 146, 8),
//                                        new LocationWrapper(IHyrame.WORLD.get().getUID(), 52, 144, 8)
//                                )
//                        ), //doors
//                        new LocationWrapper(IHyrame.WORLD.get().getUID(), 54, 147, 8), //first area
//                        new LocationWrapper(IHyrame.WORLD.get().getUID(), 43, 144, -6), //second area
//                        new LocationWrapper(IHyrame.WORLD.get().getUID(), 46.5, 145, 1.5, -90, 0), //spawn loc
//                        new LocationWrapper(IHyrame.WORLD.get().getUID(), 41.5, 145, 1.5, 90, 0) //spawn close
//                )
//        ), new LGConfiguration.WaitingRoom(
//                new LocationWrapper(IHyrame.WORLD.get().getUID(), -0.5, 160, -1000.5, -90, 0),
//                new LocationWrapper(IHyrame.WORLD.get().getUID(), 21, 175, -1016),
//                new LocationWrapper(IHyrame.WORLD.get().getUID(), -15, 159, -985)
//        ), Arrays.asList(
//                new LocationWrapper(IHyrame.WORLD.get().getUID(), -3.5, 145, -3.5),
//                new LocationWrapper(IHyrame.WORLD.get().getUID(), -3.5, 145, 6.5),
//                new LocationWrapper(IHyrame.WORLD.get().getUID(), -3.5, 150, 1.5),
//                new LocationWrapper(IHyrame.WORLD.get().getUID(), -3.5, 145, 20.5),
//                new LocationWrapper(IHyrame.WORLD.get().getUID(), -3.5, 145, -17.5)
//        ), 20, false, 180)
//        , "lasergame", LGGameType.SEXTUPLE.getName(), "Nexus");

        this.hyrame = HyrameLoader.load(new LGProvider(this));

        languageManager = this.hyrame.getLanguageManager();

        this.game = new LGGame(this.hyrame, this);
        this.hyrame.getGameManager().registerGame(() -> this.game);

        try {
            mapImage = ImageIO.read(getRessource("map/map-lasergame-background.png"));
            minecraftFont = Font.createFont(0, getRessource("font/minecraft.ttf"));
        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
        }

        HyriAPI.get().getServer().setState(IHyriServer.State.READY);
    }

    @Override
    public void onDisable() {
        this.hyrame.getGameManager().unregisterGame(this.game);
    }

    public Font getMinecraftFont() {
        return minecraftFont;
    }

    public Image getMapImage() {
        return mapImage;
    }

    public LGGame getGame() {
        return this.game;
    }

    public IHyrame getHyrame() {
        return hyrame;
    }

    public LGConfiguration getConfiguration() {
        return configuration;
    }

    public InputStream getRessource(String path) {
        return getClass().getClassLoader().getResourceAsStream(path);
    }

    public static IHyriLanguageManager getLanguageManager() {
        return languageManager;
    }

}
