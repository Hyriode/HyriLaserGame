package fr.hyriode.lasergame.game.bonus;

import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

public interface ILGBonusAnimation {

    void start();

    void stop();

    class Default implements ILGBonusAnimation {

        private final ArmorStand armorStand;
        private BukkitTask task;

        private double rotate = 0;
        private double up = 0;
        private boolean down = false;

        private final JavaPlugin plugin;
        private final Location location;

        public Default(JavaPlugin plugin, Location location, ArmorStand armorStand) {
            this.plugin = plugin;
            this.location = location;
            this.armorStand = armorStand;
        }

        @Override
        public void start() {
            this.task = new BukkitRunnable() {
                @Override
                public void run() {
                    rotate();
                }
            }.runTaskTimerAsynchronously(this.plugin, 0L, 1L);
        }

        private void rotate() {
            this.armorStand.setHeadPose(new EulerAngle(Math.toRadians(rotate += 4), Math.toRadians(rotate), 0));

            if(down){
                this.armorStand.teleport(location.clone().add(new Vector().setY(up -= 0.05D)));
                if(up < -0.5D)
                    down = false;
            }else{
                this.armorStand.teleport(location.clone().add(new Vector().setY(up += 0.05D)));
                if(up > 0.5D)
                    down = true;
            }
        }

        @Override
        public void stop() {
            this.task.cancel();
            this.armorStand.remove();
        }
    }

}
