package fr.hyriode.lasergame.game.teleport;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;

public class LGMapChunk {

    private Chunk handle;

    private int x;
    private int z;

    public LGMapChunk(int x, int z) {
        this.x = x;
        this.z = z;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getZ() {
        return z;
    }

    public void setZ(int z) {
        this.z = z;
    }

    public Chunk asBukkit(World world) {
        return this.handle == null ? this.handle = world.getChunkAt(this.x, this.z) : this.handle;
    }

}