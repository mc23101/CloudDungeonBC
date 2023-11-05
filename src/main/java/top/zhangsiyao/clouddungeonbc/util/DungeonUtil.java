package top.zhangsiyao.clouddungeonbc.util;

import com.google.gson.Gson;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import top.zhangsiyao.clouddungeonbc.CloudDungeonBC;

import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.TimeUnit;

public class DungeonUtil {
    public static void start(UUID uuid,String name){
        // 创建副本
        ProxiedPlayer player=CloudDungeonBC.plugin.getProxy().getPlayer(uuid);
        if(player==null){
            return;
        }
        if(!CloudDungeonBC.playerTeam.containsKey(uuid)){
            ConcurrentSkipListSet<UUID> listSet = new ConcurrentSkipListSet<>();
            listSet.add(uuid);
            CloudDungeonBC.teams.put(uuid,listSet);
            CloudDungeonBC.playerTeam.put(uuid,uuid);
            player.sendMessage(new TextComponent("队伍创建成功"));
        }
        if(!CloudDungeonBC.teams.containsKey(uuid)){
            player.sendMessage(new TextComponent("您不是此队伍的队长，无法创建地牢！"));
            return;
        }
        if(CloudDungeonBC.dpServers.contains(player.getServer().getInfo().getName())){
            player.sendMessage(new TextComponent("你当前已经在地牢中啦，无法创建地牢！"));
            return;
        }
        String serverName;
        if(CloudDungeonBC.pluginManger.getDungeon().containsKey(name)){
            serverName=CloudDungeonBC.pluginManger.getDungeon().get(name);
        }else {
            serverName=CloudDungeonBC.pluginManger.getDungeon().get("default");
        }
        ServerInfo server = CloudDungeonBC.plugin.getProxy().getServers().get(serverName);
        Set<UUID> players=CloudDungeonBC.teams.get(player.getUniqueId());
        for(UUID uid:players){
            ProxiedPlayer member =CloudDungeonBC.plugin.getProxy().getPlayer(uid);
            member.connect(server);
            member.sendMessage(new TextComponent("连接至副本服务器中，需要等待其他玩家加入自动创建副本！"));
        }
        DungeonDataPacket dungeonDataPacket=new DungeonDataPacket(name,player.getUniqueId(), new ArrayList<>(players),new String[0]);
        Gson gson = new Gson();
        String jsonString = gson.toJson(dungeonDataPacket);
        // 将JSON字符串转为byte数组
        byte[] byteArray = jsonString.getBytes();
        CloudDungeonBC.plugin.getProxy().getScheduler().schedule(CloudDungeonBC.plugin,
                ()->{
                    server.sendData("dungeonplus:dungeon",byteArray);
                },
                CloudDungeonBC.pluginManger.getDelay(), TimeUnit.SECONDS
        );
    }

    public static void end(UUID uuid,String dungeon){
        ProxiedPlayer player=CloudDungeonBC.plugin.getProxy().getPlayer(uuid);
        if(player==null){
            return;
        }
        String serverName=CloudDungeonBC.pluginManger.getMainServer();
        ServerInfo server = CloudDungeonBC.plugin.getProxy().getServers().get(serverName);
        Set<UUID> players=CloudDungeonBC.teams.get(player.getUniqueId());
        if(players!=null){
            for(UUID uid:players){
                ProxiedPlayer member =CloudDungeonBC.plugin.getProxy().getPlayer(uid);
                member.sendMessage(new TextComponent("返回主服务器中！"));
                member.connect(server);
            }
        }
        //CloudDungeonBC.plugin.getProxy().broadcast(new TextComponent("玩家"+player.getName()+"在副本子服中通关"+dungeon+"副本"));
    }
}
