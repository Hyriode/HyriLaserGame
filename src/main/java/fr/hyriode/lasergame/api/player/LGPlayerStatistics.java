package fr.hyriode.lasergame.api.player;

import fr.hyriode.api.mongodb.MongoDocument;
import fr.hyriode.api.mongodb.MongoSerializable;
import fr.hyriode.api.mongodb.MongoSerializer;
import fr.hyriode.api.player.IHyriPlayer;
import fr.hyriode.api.player.model.IHyriStatistics;
import fr.hyriode.hyrame.game.HyriGameType;
import fr.hyriode.lasergame.game.LGGameType;
import org.bson.Document;

import java.util.HashMap;
import java.util.Map;

public class LGPlayerStatistics implements IHyriStatistics {

    private final Map<LGGameType, Data> data = new HashMap<>();;

    public void update(IHyriPlayer player) {
        player.getStatistics().add("lasergame", this);
        player.update();
    }

    @Override
    public void save(MongoDocument document) {
        for (Map.Entry<LGGameType, Data> entry : this.data.entrySet()) {
            document.append(entry.getKey().name(), MongoSerializer.serialize(entry.getValue()));
        }
    }

    @Override
    public void load(MongoDocument document) {
        for (Map.Entry<String, Object> entry : document.entrySet()) {
            final MongoDocument dataDocument = MongoDocument.of((Document) entry.getValue());
            final Data data = new Data();

            data.load(dataDocument);

            this.data.put(LGGameType.valueOf(entry.getKey()), data);
        }
    }

    public Data getData(LGGameType gameType) {
        Data data = this.data.get(gameType);

        if (data == null) {
            data = new Data();
            this.data.put(gameType, data);
        }

        return data;
    }

    public static class Data implements MongoSerializable {

        private long kills;
        private long deaths;
        private long bestScore;
        private long totalWins;
        private long bestKillStreak;
        private long bestWinStreak;
        private long currentWinStreak;

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

        public void addCurrentWinStreak(int i) {
            this.currentWinStreak += i;
        }

        public void addWins(int i) {
            this.totalWins += i;
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

        public long getTotalWins() {
            return this.totalWins;
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

        @Override
        public void save(MongoDocument document) {
            document.append("kills", this.kills);
            document.append("deaths", this.deaths);
            document.append("bestScore", this.bestScore);
            document.append("totalWins", this.totalWins);
            document.append("bestKillStreak", this.bestKillStreak);
            document.append("bestWinStreak", this.bestWinStreak);
            document.append("currentWinStreak", this.currentWinStreak);
        }

        @Override
        public void load(MongoDocument document) {
            this.kills = document.getLong("kills");
            this.deaths = document.getLong("deaths");
            this.bestScore = document.getLong("bestScore");
            this.totalWins = document.getLong("totalWins");
            this.bestKillStreak = document.getLong("bestKillStreak");
            this.bestWinStreak = document.getLong("bestWinStreak");
            this.currentWinStreak = document.getLong("currentWinStreak");
        }
    }
}
