package fr.hyriode.lasergame.api;

import com.google.gson.Gson;
import fr.hyriode.lasergame.api.manager.player.HyriLGPlayerManager;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;

public class HyriLGAPI {

    public static final String REDIS_KEY = "lg:";
    public static final Gson GSON = new Gson();

    private final HyriLGPlayerManager playerManager;

    public HyriLGAPI() {
        this.playerManager = new HyriLGPlayerManager(this);
    }

    public HyriLGPlayerManager getPlayerManager() {
        return this.playerManager;
    }

}
