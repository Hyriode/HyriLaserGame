package fr.hyriode.lasergame;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.server.IHyriServer;
import fr.hyriode.hyggdrasil.api.server.HyggServer;
import fr.hyriode.hyrame.HyrameLoader;
import fr.hyriode.hyrame.IHyrame;
import fr.hyriode.hyrame.utils.LocationWrapper;
import fr.hyriode.lasergame.configuration.LGConfiguration;
import fr.hyriode.lasergame.game.LGGame;
import org.bukkit.plugin.java.JavaPlugin;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Arrays;
import java.util.Base64;
import java.util.logging.Level;

public class HyriLaserGame extends JavaPlugin {

    private IHyrame hyrame;
    private LGGame game;

    private Image mapImage;
    private Font minecraftFont;

    private LGConfiguration configuration;

    @Override
    public void onEnable() {
        this.getLogger().log(Level.INFO, "Starting " + this.getClass().getSimpleName() + "...");

        if(!HyriAPI.get().getConfig().isDevEnvironment()){
            this.configuration = HyriAPI.get().getServer().getConfig(LGConfiguration.class);
        }else {
//            HyriAPI.get().getHystiaAPI().getWorldManager().saveWorld("lasergame", LGGameType.FIVE_FIVE.getName(), "Evolution").whenComplete((aBoolean, throwable) -> System.out.println("world: " + aBoolean));
//            HyriAPI.get().getHystiaAPI().getConfigManager().saveConfig(
//                   HyriAPI.get().getHystiaAPI().getConfigManager().saveConfig(
                           this.configuration = new LGConfiguration(Arrays.asList(
                                  new LGConfiguration.Team(
                                          "red",
                                          Arrays.asList(
                                                  new LGConfiguration.Door(
                                                          new LocationWrapper(-60, 146, -6),
                                                          new LocationWrapper(-62, 144, -6)
                                                  ),
                                                  new LGConfiguration.Door(
                                                          new LocationWrapper(-60, 146, 8),
                                                          new LocationWrapper(-62, 144, 8)
                                                  )
                                          ),
                                          new LocationWrapper(-63, 147, -6), //first area
                                          new LocationWrapper(-51, 143, 8), //second area
                                          new LocationWrapper(-53.5, 146, 1.5, 90, 0), //spawn loc
                                          new LocationWrapper(-48.5, 146, 1.5, -90, 0) //spawn close
                                  ),
                                  new LGConfiguration.Team(
                                          "blue",
                                          Arrays.asList(
                                                  new LGConfiguration.Door(
                                                          new LocationWrapper(52, 146, -6),
                                                          new LocationWrapper(54, 144, -6)
                                                  ),
                                                  new LGConfiguration.Door(
                                                          new LocationWrapper(54, 146, 8),
                                                          new LocationWrapper(52, 144, 8)
                                                  )
                                          ), //doors
                                          new LocationWrapper(54, 147, 8), //first area
                                          new LocationWrapper(43, 143, -6), //second area
                                          new LocationWrapper(46.5, 146, 1.5, -90, 0), //spawn loc
                                          new LocationWrapper(41.5, 146, 1.5, 90, 0) //spawn close
                                  )
                          ), new LGConfiguration.WaitingRoom(
                                  new LocationWrapper(-0.5, 160, -1000.5, -90, 0),
                                  new LocationWrapper(21, 175, -1016),
                                  new LocationWrapper(-15, 159, -985)
                          ), Arrays.asList(
                                  new LocationWrapper(-3.5, 145, -3.5),
                                  new LocationWrapper(-3.5, 145, 6.5),
                                  new LocationWrapper(-3.5, 150, 1.5),
                                  new LocationWrapper(-3.5, 145, 20.5),
                                  new LocationWrapper(-3.5, 145, -17.5)
                          ), Arrays.asList(

                           ), 20, false, 30);
//                          , "lasergame", LGGameType.FIVE_FIVE.getName(), "Nexus").whenComplete((aBoolean, throwable) -> System.out.println("ui " + aBoolean));//, "lasergame", LGGameType.FIVE_FIVE.getName(), "Evolution").whenComplete((aBoolean, throwable) -> System.out.println(aBoolean));
////            HyriAPI.get().getServer().setSlots(50);
        }

        this.hyrame = HyrameLoader.load(new LGProvider(this));

        this.game = new LGGame(this.hyrame, this);
        this.hyrame.getGameManager().registerGame(() -> this.game);

        try {
            mapImage = ImageIO.read(new ByteArrayInputStream(Base64.getDecoder().decode("iVBORw0KGgoAAAANSUhEUgAAAIAAAACACAYAAADDPmHLAAAEEElEQVR4nO3ZTW8TVxjF8XPvHXtM/AJOnKQEkQKmqI3SIhYsAalIlF3ZsOOTse0XYFMJiR2bShXQ0hbxopKipgRCFBIH5LGdeUFzgyKlGwbYZHzOT3ISORs/mf/cuTMx3W43g9CyOvTcFAA5BUBOAZBTAOQUADkFQE4BkFMA5BQAOQVATgGQUwDkFAA5BUBOAZBTAOQUADkFQE4BkFMA5BQAOQVATgGQUwDkFAA5BUBOAZBTAOQUADkFQE4BkFMA5BQAOQVATgGQUwDkFAA5BUBOAZBTAOQUADkFQE4BkFMA5BQAOQVATgGQUwDkFAA5BUBOAZBTAOQUADkFQE4BkFMA5BQAOQVATgGQUwDkFAA5BUBOAZBTAOQUADkFQE4BkFMA5BQAOQVATgGQUwDkFAA5BUBOAZBTAOQUADkFQE4BkFMA5BQAOQVATgGQUwDkFAA5BUBOAZBTAOQUADkFQE4BkFMA5BQAOQVATgGQUwDkFAA5BUBOAZBTAOQUADkFQE4BkFMA48YYdL76BsfPXUQQ1j44nAIYM7ML32Hx6jX019cQDwcfHE4BjJHaoTZOnL+EJIqwvvSk0GAKoMTqM4cx0ZnZHaB97CQOTE6ht/wP0jgpNJgCKKl29xS6l6+gMTvnBzDGoDE9C2cthr0eskQBjC0X1tBZOO2X/CzdOdDGOlRrB2CCANuDPrIsLTR+wP7HLJNqo4k0jgFksMb61x7OwRr/68K0ApRI5+tFHDz6JdIkgckDcOZ9EECaxPkXWGv95aAorQAlEm2sY/RmC64SIj/5rXWYmJr271nnEDZa/r08gNYXc6jWGz6ILE0RbfUw6G0iHg33DKwA9rH5Cz8gS2PEUQRXrcJVqkhHIwx6G8ji2B/skxcuAd9bBEEFlWrFH/D8GrBw+UfMfXvGPxjK14NXTx7i0a2fsfb08Z6BFcA+VmtPIh5E/owOm4eQxtvIKhUko50HPPnB3nj6CP3VFb8qzCyeRvvoPIy1WP7tV7x+tgTjrD/r366t+tf/KYB9bOnmDX+9z5IYxgVoHZlHMhr6JX/qxCm/7L/463esPvzT7wnC9iSm5o/7MJ7fv1toMG0C97H87E+3R/4ann8PWwf9ZSAeRn75t9bABjvnsMl/zu8C8k2gdYWH0gpQIvkmMI76/kA7Z2H8bd/7c9gYuCDwyz+y4veBCqBEtpaf+Q8bNlsIJur+qd/2IPLv5SuCCyrYWvkPq48fFB5KAZSQq4aoT3b8nUB/Y313gH/v/ILN58vob74uPJQCKKFas4Ww0cTLP+7tBpDv9Fce3P/oYbQJLJl8g3fkzFmM3vTw9+1bu08CP5UCKJl6ZxrxcIg7P13H21cvP/vDm263+xH/OpBxoxWAnAIgpwCYAXgHh2frKYWX+hoAAAAASUVORK5CYII=")));
            minecraftFont = Font.createFont(0, getRessource("font/minecraft.ttf"));

        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
        }

        HyriAPI.get().getServer().setState(HyggServer.State.READY);
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
        try {
            return this.getDataFolder().getAbsoluteFile().toPath().resolve(path).toUri().toURL().openStream();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
