package fr.hyriode.lasergame.api.player;

import java.util.UUID;

public class HyriLGPlayer {

    private final UUID uniqueId;
    private long kills;
    private long deaths;
    private long bestScore;
    private long playTime;
    private long bestKillStreak;
    private long bestWinStreak;
    private long currentWinStreak;

    public HyriLGPlayer(UUID uniqueId) {
        this.uniqueId = uniqueId;
        this.kills = 0;
        this.deaths = 0;
        this.bestScore = 0;
        this.playTime = 0;
        this.bestKillStreak = 0;
        this.bestWinStreak = 0;
        this.currentWinStreak = 0;
    }

    public UUID getUniqueId() {
        return this.uniqueId;
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

}
