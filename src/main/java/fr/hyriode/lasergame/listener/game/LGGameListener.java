package fr.hyriode.lasergame.listener.game;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.event.HyriEventHandler;
import fr.hyriode.hyrame.game.HyriGamePlayer;
import fr.hyriode.hyrame.game.HyriGameSpectator;
import fr.hyriode.hyrame.game.event.player.HyriGameDeathEvent;
import fr.hyriode.hyrame.game.event.player.HyriGameReconnectEvent;
import fr.hyriode.hyrame.game.event.player.HyriGameReconnectedEvent;
import fr.hyriode.hyrame.game.event.player.HyriGameSpectatorEvent;
import fr.hyriode.hyrame.game.protocol.HyriDeathProtocol;
import fr.hyriode.hyrame.listener.HyriListener;
import fr.hyriode.lasergame.HyriLaserGame;
import fr.hyriode.lasergame.game.LGGame;
import fr.hyriode.lasergame.game.LGGameTeam;
import fr.hyriode.lasergame.game.player.LGGamePlayer;
import fr.hyriode.lasergame.game.scoreboard.LGScoreboard;
import org.bukkit.entity.Player;

public class LGGameListener extends HyriListener<HyriLaserGame> {

    public LGGameListener(HyriLaserGame plugin) {
        super(plugin);

        HyriAPI.get().getEventBus().register(this);
    }


//    @HyriEventHandler
//    public void onReconnect(HyriGameReconnectEvent event) {
//        final LGGamePlayer player = (LGGamePlayer) event.getGamePlayer();
//        final LGGameTeam team = (LGGameTeam) player.getTeam();
//
//        if (!team.hasBed()) {
//            event.disallow();
//        }
//    }

    @HyriEventHandler
    public void onReconnected(HyriGameReconnectedEvent event) {
        final LGGamePlayer player = (LGGamePlayer) event.getGamePlayer();
        player.getPlayer().getInventory().clear();

        player.respawn();

        this.plugin.getGame().getProtocolManager().getProtocol(HyriDeathProtocol.class)
                .runDeath(HyriGameDeathEvent.Reason.VOID, player.getPlayer());
    }

    @HyriEventHandler
    public void onSpectator(HyriGameSpectatorEvent event) {
        final LGGame game = (LGGame) event.getGame();
        final HyriGameSpectator spectator = event.getSpectator();
        final Player player = spectator.getPlayer();

        if (!(spectator instanceof HyriGamePlayer)) { // Player is an outside spectator
            player.teleport(this.plugin.getConfiguration().getWaitingRoom().getSpawn().asBukkit());

            new LGScoreboard(this.plugin, player).show();
        }
    }
}