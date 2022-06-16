package emu.grasscutter.permission;


import emu.grasscutter.game.player.Player;
import emu.grasscutter.server.game.GameServer;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.LinkedHashMap;

@Getter
@Setter
public class PermissionManger {

    private HashMap<Player, PermissionGroup> groups = new LinkedHashMap<>();
    private final GameServer gameServer;

    public PermissionManger(GameServer gameServer) {
        this.gameServer = gameServer;
    }

    public void addGroup(Player player, PermissionGroup group) {
        groups.put(player, group);
    }

    public void removeGroup(Player player) {
        groups.remove(player);
    }

    public PermissionGroup getGroup(Player player) {
        return groups.get(player);
    }

    public boolean hasGroup(Player player) {
        return groups.containsKey(player);
    }

    public int getPermissionLevel(Player player) {
        return getGroup(player).getNumber();
    }
}
