package top.zhangsiyao.clouddungeonbc.util;

import java.util.List;
import java.util.UUID;

public class MessageDataPacket {
    public byte[] data;
    public List<UUID> players;

    public MessageDataPacket(byte[] data, List<UUID> players) {
        this.data = data;
        this.players = players;
    }
}
