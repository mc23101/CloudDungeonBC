package top.zhangsiyao.clouddungeonbc.util;

import java.util.List;
import java.util.UUID;

public class DungeonDataPacket {
    public String dungeon;
    public UUID leader;
    public List<UUID> players;
    public String[] params;

    public DungeonDataPacket(String dungeon, UUID leader, List<UUID> players, String[] params) {
        this.dungeon = dungeon;
        this.leader = leader;
        this.players = players;
        this.params = params;
    }
}
