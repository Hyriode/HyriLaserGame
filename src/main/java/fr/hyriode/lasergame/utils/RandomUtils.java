package fr.hyriode.lasergame.utils;

import org.bukkit.util.Vector;

import java.util.Random;

public class RandomUtils {

    public static final Random random = new Random(System.nanoTime());

    public static Vector getRandomVector() {
        double x, y, z;
        x = random.nextDouble() * 2 - 1;
        y = random.nextDouble() * 2 - 1;
        z = random.nextDouble() * 2 - 1;

        return new Vector(x, y, z).normalize();
    }

}
