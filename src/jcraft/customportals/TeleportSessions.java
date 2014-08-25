package jcraft.customportals;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TeleportSessions {

    private static final Map<UUID, Long> lastTeleports = new HashMap<UUID, Long>();

    public static boolean canTeleport(UUID playerUUID) {
        if (CustomPortalsPlugin.teleportCooldown <= 0) {
            return true;
        }

        if (!lastTeleports.containsKey(playerUUID)) {
            lastTeleports.put(playerUUID, System.currentTimeMillis());
            return true;
        }

        final long lastTeleport = lastTeleports.get(playerUUID);

        if (System.currentTimeMillis() - lastTeleport >= CustomPortalsPlugin.teleportCooldown) {
            lastTeleports.put(playerUUID, System.currentTimeMillis());
            return true;
        } else {
            return false;
        }
    }

}
