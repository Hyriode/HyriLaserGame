package fr.hyriode.lasergame;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.server.IHyriServer;
import fr.hyriode.hyggdrasil.api.server.HyggServer;
import fr.hyriode.hyrame.HyrameLoader;
import fr.hyriode.hyrame.IHyrame;
import fr.hyriode.hyrame.game.waitingroom.HyriWaitingRoom;
import fr.hyriode.hyrame.utils.AreaWrapper;
import fr.hyriode.hyrame.utils.LocationWrapper;
import fr.hyriode.lasergame.configuration.LGConfiguration;
import fr.hyriode.lasergame.game.LGGame;
import fr.hyriode.lasergame.game.teleport.LGMapChunk;
import fr.hyriode.lasergame.utils.ConfigUtil;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.logging.Level;

public class HyriLaserGame extends JavaPlugin {

    public static final String ID = "lasergame";
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
        } else {
            this.configuration = ConfigUtil.getNexus();
        }

        this.hyrame = HyrameLoader.load(new LGProvider(this));

        this.game = new LGGame(this.hyrame, this);
        this.hyrame.getGameManager().registerGame(() -> this.game);

        try {
            mapImage = ImageIO.read(new ByteArrayInputStream(Base64.getDecoder().decode("iVBORw0KGgoAAAANSUhEUgAAAIAAAACACAYAAADDPmHLAAAEEElEQVR4nO3ZTW8TVxjF8XPvHXtM/AJOnKQEkQKmqI3SIhYsAalIlF3ZsOOTse0XYFMJiR2bShXQ0hbxopKipgRCFBIH5LGdeUFzgyKlGwbYZHzOT3ISORs/mf/cuTMx3W43g9CyOvTcFAA5BUBOAZBTAOQUADkFQE4BkFMA5BQAOQVATgGQUwDkFAA5BUBOAZBTAOQUADkFQE4BkFMA5BQAOQVATgGQUwDkFAA5BUBOAZBTAOQUADkFQE4BkFMA5BQAOQVATgGQUwDkFAA5BUBOAZBTAOQUADkFQE4BkFMA5BQAOQVATgGQUwDkFAA5BUBOAZBTAOQUADkFQE4BkFMA5BQAOQVATgGQUwDkFAA5BUBOAZBTAOQUADkFQE4BkFMA5BQAOQVATgGQUwDkFAA5BUBOAZBTAOQUADkFQE4BkFMA5BQAOQVATgGQUwDkFAA5BUBOAZBTAOQUADkFQE4BkFMA5BQAOQVATgGQUwDkFAA5BUBOAZBTAOQUADkFQE4BkFMA48YYdL76BsfPXUQQ1j44nAIYM7ML32Hx6jX019cQDwcfHE4BjJHaoTZOnL+EJIqwvvSk0GAKoMTqM4cx0ZnZHaB97CQOTE6ht/wP0jgpNJgCKKl29xS6l6+gMTvnBzDGoDE9C2cthr0eskQBjC0X1tBZOO2X/CzdOdDGOlRrB2CCANuDPrIsLTR+wP7HLJNqo4k0jgFksMb61x7OwRr/68K0ApRI5+tFHDz6JdIkgckDcOZ9EECaxPkXWGv95aAorQAlEm2sY/RmC64SIj/5rXWYmJr271nnEDZa/r08gNYXc6jWGz6ILE0RbfUw6G0iHg33DKwA9rH5Cz8gS2PEUQRXrcJVqkhHIwx6G8ji2B/skxcuAd9bBEEFlWrFH/D8GrBw+UfMfXvGPxjK14NXTx7i0a2fsfb08Z6BFcA+VmtPIh5E/owOm4eQxtvIKhUko50HPPnB3nj6CP3VFb8qzCyeRvvoPIy1WP7tV7x+tgTjrD/r366t+tf/KYB9bOnmDX+9z5IYxgVoHZlHMhr6JX/qxCm/7L/463esPvzT7wnC9iSm5o/7MJ7fv1toMG0C97H87E+3R/4ann8PWwf9ZSAeRn75t9bABjvnsMl/zu8C8k2gdYWH0gpQIvkmMI76/kA7Z2H8bd/7c9gYuCDwyz+y4veBCqBEtpaf+Q8bNlsIJur+qd/2IPLv5SuCCyrYWvkPq48fFB5KAZSQq4aoT3b8nUB/Y313gH/v/ILN58vob74uPJQCKKFas4Ww0cTLP+7tBpDv9Fce3P/oYbQJLJl8g3fkzFmM3vTw9+1bu08CP5UCKJl6ZxrxcIg7P13H21cvP/vDm263+xH/OpBxoxWAnAIgpwCYAXgHh2frKYWX+hoAAAAASUVORK5CYII=")));
            minecraftFont = Font.createFont(0, getRessource("font/minecraft.ttf"));

        } catch (Exception e) {
            e.printStackTrace();
        }

        this.loadChunks();

        HyriAPI.get().getServer().setState(HyggServer.State.READY);
    }

    public void loadChunks() {
        Location location = this.getConfiguration().getTeams().get(0).getSpawnLocation();
        List<LGMapChunk> chunks = this.getChunksAround(location.getChunk(), Bukkit.getViewDistance());

        for (LGMapChunk chunk : chunks) {
            chunk.asBukkit(IHyrame.WORLD.get()).load(false);
        }
    }

    private List<LGMapChunk> getChunksAround(Chunk origin, int radius) {
        final int length = (radius * 2) + 1;
        final List<LGMapChunk> chunks = new ArrayList<>(length * length);

        final int cX = origin.getX();
        final int cZ = origin.getZ();

        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                chunks.add(new LGMapChunk(cX + x, cZ + z));
            }
        }
        return chunks;
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
