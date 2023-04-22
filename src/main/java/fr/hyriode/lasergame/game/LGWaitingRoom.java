package fr.hyriode.lasergame.game;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.language.HyriLanguage;
import fr.hyriode.api.language.HyriLanguageMessage;
import fr.hyriode.api.leaderboard.HyriLeaderboardScope;
import fr.hyriode.api.leveling.NetworkLeveling;
import fr.hyriode.api.player.IHyriPlayer;
import fr.hyriode.hyrame.game.waitingroom.HyriWaitingRoom;
import fr.hyriode.hyrame.utils.DurationFormatter;
import fr.hyriode.lasergame.HyriLaserGame;
import fr.hyriode.lasergame.api.player.LGPlayerStatistics;
import fr.hyriode.lasergame.configuration.LGConfiguration;
import fr.hyriode.lasergame.game.player.LGGamePlayer;
import org.bukkit.Material;

import java.util.function.Supplier;

public class LGWaitingRoom extends HyriWaitingRoom {
    public LGWaitingRoom(LGGame game, Supplier<LGConfiguration> configuration) {
        super(game, Material.IRON_HOE, () -> configuration.get().getWaitingRoom());

        this.addLeaderboard(new Leaderboard(NetworkLeveling.LEADERBOARD_TYPE, "lasergame-experience",
                player -> HyriLanguageMessage.get("leaderboard.experience.display").getValue(player))
                .withScopes(HyriLeaderboardScope.DAILY, HyriLeaderboardScope.WEEKLY, HyriLeaderboardScope.MONTHLY));
        this.addLeaderboard(new Leaderboard(HyriLaserGame.ID, "kills", player -> HyriLanguageMessage.get("leaderboard.kills.display").getValue(player)));
        this.addLeaderboard(new Leaderboard(HyriLaserGame.ID, "victories", player -> HyriLanguageMessage.get("leaderboard.victories.display").getValue(player)));
        this.addLeaderboard(new Leaderboard(HyriLaserGame.ID, "points", player -> HyriLanguageMessage.get("leaderboard.points.display").getValue(player)));

        this.addAllStatistics(22);
    }

    private void addAllStatistics(int slot) {
        final NPCCategory normal = new NPCCategory(new HyriLanguageMessage("").addValue(HyriLanguage.EN, "All"));

        normal.addData(new NPCData(this.getDisplayStatistics("kills"), account -> String.valueOf(this.getAllStatistics(account).getKills())));
        normal.addData(new NPCData(this.getDisplayStatistics("deaths"), account -> String.valueOf(this.getAllStatistics(account).getDeaths())));
        normal.addData(new NPCData(this.getDisplayStatistics("points"), account -> String.valueOf(this.getAllStatistics(account).getBestScore())));
        normal.addData(NPCData.voidData());
        normal.addData(new NPCData(this.getDisplayStatistics("bestWinStreak"), account -> String.valueOf(this.getAllStatistics(account).getBestWinStreak())));
        normal.addData(new NPCData(this.getDisplayStatistics("currentWinStreak"), account -> String.valueOf(this.getAllStatistics(account).getCurrentWinStreak())));
        normal.addData(new NPCData(this.getDisplayStatistics("totalWins"), account -> String.valueOf(this.getAllStatistics(account).getTotalWins())));
        normal.addData(new NPCData(this.getDisplayStatistics("totalDefeats"), account -> String.valueOf(this.getAllStatistics(account).getDefeats())));
        normal.addData(NPCData.voidData());
        normal.addData(new NPCData(this.getDisplayStatistics("games-played"), account -> String.valueOf(this.getAllStatistics(account).getPlayedGames())));
        normal.addData(new NPCData(this.getDisplayStatistics("played-time"), account -> this.formatPlayedTime(account, account.getStatistics().getPlayTime(HyriLaserGame.ID + "#" + this.game.getType().getName()))));

        this.addNPCCategory(slot, normal);
    }

    private HyriLanguageMessage getDisplayStatistics(String name){
        return HyriLanguageMessage.get("statistics." + name + ".name");
    }

    private LGPlayerStatistics.Data getAllStatistics(IHyriPlayer account) {
        return ((LGGamePlayer) this.game.getPlayer(account.getUniqueId())).getAllStatistics();
    }

    private String formatPlayedTime(IHyriPlayer account, long playedTime) {
        if(playedTime == 0)
            return HyriLanguageMessage.get("statistics.played-time.none").getValue(account);
        return new DurationFormatter().format(account.getSettings().getLanguage(), playedTime);
    }
}
