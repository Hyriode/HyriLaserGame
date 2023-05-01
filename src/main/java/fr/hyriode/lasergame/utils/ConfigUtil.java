package fr.hyriode.lasergame.utils;

import fr.hyriode.hyrame.game.waitingroom.HyriWaitingRoom;
import fr.hyriode.hyrame.utils.AreaWrapper;
import fr.hyriode.hyrame.utils.LocationWrapper;
import fr.hyriode.lasergame.HyriLaserGame;
import fr.hyriode.lasergame.configuration.LGConfiguration;

import java.util.Arrays;

public class ConfigUtil {

    public static LGConfiguration getNexus() {
        HyriWaitingRoom.Config wr = new HyriWaitingRoom.Config(
                new LocationWrapper(-0.5, 160, -1000.5, -90, 0),
                new LocationWrapper(21, 175, -1016),
                new LocationWrapper(-15, 159, -985),
                new LocationWrapper(-4.5, 160, -1000.5, 0, 0));
        wr.addLeaderboard(new HyriWaitingRoom.Config.Leaderboard(HyriLaserGame.ID, "lasergame-experience", new LocationWrapper(-5.5, 189, -12.5)));
        wr.addLeaderboard(new HyriWaitingRoom.Config.Leaderboard(HyriLaserGame.ID, "kills", new LocationWrapper(-1.5, 189, -6.5)));
        wr.addLeaderboard(new HyriWaitingRoom.Config.Leaderboard(HyriLaserGame.ID, "victories", new LocationWrapper(-1.5, 189, 7.5)));
        wr.addLeaderboard(new HyriWaitingRoom.Config.Leaderboard(HyriLaserGame.ID, "points", new LocationWrapper(-5.5, 189, 13.5)));

        return new LGConfiguration(Arrays.asList(
                new LGConfiguration.Team(
                        "red",
                        Arrays.asList(
                                new AreaWrapper(
                                        new LocationWrapper(-60, 146, -6),
                                        new LocationWrapper(-62, 144, -6)
                                ),
                                new AreaWrapper(
                                        new LocationWrapper(-60, 146, 8),
                                        new LocationWrapper(-62, 144, 8)
                                )
                        ),
                        new AreaWrapper(
                                new LocationWrapper(-63, 147, -6),
                                new LocationWrapper(-51, 143, 8)
                        ),
                        new LocationWrapper(-53.5, 146, 1.5, 90, 0), //spawn loc
                        new LocationWrapper(-48.5, 146, 1.5, -90, 0) //spawn close
                ),
                new LGConfiguration.Team(
                        "blue",
                        Arrays.asList(
                                new AreaWrapper(
                                        new LocationWrapper(52, 146, -6),
                                        new LocationWrapper(54, 144, -6)
                                ),
                                new AreaWrapper(
                                        new LocationWrapper(54, 146, 8),
                                        new LocationWrapper(52, 144, 8)
                                )
                        ), //doors
                        new AreaWrapper(
                                new LocationWrapper(54, 147, 8), //area
                                new LocationWrapper(43, 143, -6)
                        ),
                        new LocationWrapper(46.5, 146, 1.5, -90, 0), //spawn loc
                        new LocationWrapper(41.5, 146, 1.5, 90, 0) //spawn close
                )
        ), wr, Arrays.asList(
                new LocationWrapper(-3.5, 145, -3.5),
                new LocationWrapper(-3.5, 145, 6.5),
                new LocationWrapper(-3.5, 150, 1.5),
                new LocationWrapper(-3.5, 145, 20.5),
                new LocationWrapper(-3.5, 145, -17.5)
        ));
    }
}
