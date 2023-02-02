package fr.hyriode.lasergame.api.player;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.player.HyriPlayerData;
import fr.hyriode.api.player.IHyriPlayer;
import fr.hyriode.lasergame.game.player.LGGamePlayer;

public class HyriLGPlayer extends HyriPlayerData {

    private long kills;
    private long deaths;
    private long bestScore;
    private long playTime;
    private long bestKillStreak;
    private long bestWinStreak;
    private long currentWinStreak;

    public HyriLGPlayer() {
    }

    public void setKills(long kills) {
        this.kills = kills;
    }

    public void addKills(int kills){
        this.kills += kills;
    }

    public void setDeaths(long deaths) {
        this.deaths = deaths;
    }

    public void addDeaths(int deaths){
        this.deaths += deaths;
    }

    public void setBestScore(long bestScore) {
        this.bestScore = bestScore;
    }

    public void setPlayTime(long playTime) {
        this.playTime = playTime;
    }

    public void addPlayedTime(long playTime){
        this.playTime += playTime;
    }

    public void setBestKillStreak(long bestKillStreak) {
        this.bestKillStreak = bestKillStreak;
    }

    public void setBestWinStreak(long bestWinStreak) {
        if(bestWinStreak + 1 > this.bestWinStreak)
            this.bestWinStreak = bestWinStreak;
    }

    public void setCurrentWinStreak(long currentWinStreak) {
        this.currentWinStreak = currentWinStreak;
    }

    public long getKills() {
        return this.kills;
    }

    public long getDeaths() {
        return this.deaths;
    }

    public long getBestScore() {
        return this.bestScore;
    }

    public long getPlayTime() {
        return this.playTime;
    }

    public long getBestKillStreak() {
        return this.bestKillStreak;
    }

    public long getBestWinStreak() {
        return this.bestWinStreak;
    }

    public long getCurrentWinStreak() {
        return this.currentWinStreak;
    }

    public void update(LGGamePlayer gamePlayer) {
        IHyriPlayer player = HyriAPI.get().getPlayerManager().getPlayer(gamePlayer.getUniqueId());
        this.addPlayedTime(gamePlayer.getPlayTime());
        this.addKills(gamePlayer.getKills());
        this.addDeaths(gamePlayer.getDeaths());
        this.setBestKillStreak(gamePlayer.getDeaths());
        player.addStatistics("bedwars", this);
        player.update();
    }
}
