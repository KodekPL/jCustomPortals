package jcraft.customportals;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

public class PortalLocation {

    private World world;
    private int minX, minY, minZ;
    private int maxX, maxY, maxZ;

    public PortalLocation(World world, Vector v1, Vector v2) {
        this.world = world;
        this.minX = Math.min(v1.getBlockX(), v2.getBlockX());
        this.minY = Math.min(v1.getBlockY(), v2.getBlockY());
        this.minZ = Math.min(v1.getBlockZ(), v2.getBlockZ());
        this.maxX = Math.max(v1.getBlockX(), v2.getBlockX());
        this.maxY = Math.max(v1.getBlockY(), v2.getBlockY());
        this.maxZ = Math.max(v1.getBlockZ(), v2.getBlockZ());
    }

    public void setWorld(World world) {
        this.world = world;
    }

    public World getWorld() {
        return this.world;
    }

    public Vector getMinimum() {
        return new Vector(minX, minY, minZ);
    }

    public Vector getMaximum() {
        return new Vector(maxX, maxY, maxZ);
    }

    public boolean intersect(Location location) {
        return this.intersect(location.getWorld(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    public boolean intersect(World world, int x, int y, int z) {
        if (this.world != world) {
            return false;
        }
        if (!(x >= minX && x <= maxX)) {
            return false;
        }
        if (!(z >= minZ && z <= maxZ)) {
            return false;
        }
        if (!(y >= minY && y <= maxY)) {
            return false;
        }
        return true;
    }

    public String serializeLocation() {
        return minX + "," + minY + "," + minZ + ":" + maxX + "," + maxY + "," + maxZ;
    }

    public static PortalLocation parseLocation(String location) {
        try {
            final String[] XYZ = location.split(":");
            final String[] minXYZ = XYZ[0].split(",");
            final String[] maxXYZ = XYZ[1].split(",");
            return new PortalLocation(null, new Vector(parseInt(minXYZ[0]), parseInt(minXYZ[1]), parseInt(minXYZ[2])), new Vector(
                    parseInt(maxXYZ[0]), parseInt(maxXYZ[1]), parseInt(maxXYZ[2])));
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static int parseInt(String number) {
        return Integer.parseInt(number);
    }
}
