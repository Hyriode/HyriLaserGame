package fr.hyriode.lasergame.api.manager.player;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.redis.IHyriRedisProcessor;
import fr.hyriode.lasergame.api.HyriLGAPI;
import fr.hyriode.lasergame.api.player.HyriLGPlayer;
import redis.clients.jedis.Jedis;

import java.util.UUID;
import java.util.function.Function;

public class HyriLGPlayerManager {

    private static final Function<UUID, String> REDIS_KEY = uuid -> HyriLGAPI.REDIS_KEY + "players:" + uuid.toString();

    private final HyriLGAPI api;
    private final IHyriRedisProcessor redisProcessor;

    public HyriLGPlayerManager(HyriLGAPI api) {
        this.api = api;
        this.redisProcessor = HyriAPI.get().getRedisProcessor();
    }

    public HyriLGPlayer getPlayer(UUID uuid) {
        try (final Jedis jedis = HyriAPI.get().getRedisResource()) {
            return HyriLGAPI.GSON.fromJson(jedis.get(REDIS_KEY.apply(uuid)), HyriLGPlayer.class);
        }
    }

    public void sendPlayer(HyriLGPlayer player) {
        this.redisProcessor.process(jedis -> jedis.set(REDIS_KEY.apply(player.getUniqueId()), HyriLGAPI.GSON.toJson(player)));
    }

    public void removePlayer(UUID uuid) {
        this.redisProcessor.process(jedis -> jedis.del(REDIS_KEY.apply(uuid)));
    }

    public HyriLGPlayer createPlayer(UUID uuid){
        final HyriLGPlayer player = new HyriLGPlayer(uuid);

        this.sendPlayer(player);

        return player;
    }

}
