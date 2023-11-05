package top.zhangsiyao.clouddungeonbc.command;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.TabExecutor;
import net.md_5.bungee.api.scheduler.ScheduledTask;
import top.zhangsiyao.clouddungeonbc.CloudDungeonBC;

import java.util.*;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.TimeUnit;

public class DLCommand extends Command implements TabExecutor {

    private final Plugin plugin;
    public DLCommand(Plugin plugin) {
        super("cdt");
        this.plugin = plugin;
    }


    @Override
    public void execute(CommandSender commandSender, String[] strings) {
        if(!(commandSender instanceof ProxiedPlayer player)){
            return;
        }
        if(strings.length>1&&strings[0].equals("team")){
            if(strings.length==2&&strings[1].equals("create")){
                //创建队伍
                if(CloudDungeonBC.playerTeam.containsKey(player.getUniqueId())){
                    player.sendMessage(new TextComponent("§f[§e§lCloudTeam§f] §c你当前已经在队伍中了！"));
                    return;
                }
                createTeam(player);
                refreshTeamTimeOut(player.getUniqueId());
                return;
            }else if(strings.length==2&&strings[1].equals("disband")){
                if(!CloudDungeonBC.playerTeam.containsKey(player.getUniqueId())){
                    player.sendMessage(new TextComponent("§f[§e§lCloudTeam§f] §c您当前不在队伍中，无法解散队伍！"));
                    return;
                }
                if(!CloudDungeonBC.teams.containsKey(player.getUniqueId())){
                    player.sendMessage(new TextComponent("§f[§e§lCloudTeam§f] §c您不是此队伍的队长，无法解散队伍"));
                    return;
                }
                Set<UUID> members=CloudDungeonBC.teams.get(player.getUniqueId());
                for(UUID mem:members){
                    CloudDungeonBC.playerTeam.remove(mem);
                }
                CloudDungeonBC.teams.remove(player.getUniqueId());
                player.sendMessage(new TextComponent("§f[§e§lCloudTeam§f] §f队伍已经解散啦！"));
                CloudDungeonBC.teamTimeOutTask.get(player.getUniqueId()).cancel();
                CloudDungeonBC.teamTimeOutTask.remove(player.getUniqueId());
                return;
            }else if(strings.length==2&&strings[1].equals("quit")){
                if(CloudDungeonBC.teams.containsKey(player.getUniqueId())){
                    player.sendMessage(new TextComponent("§f[§e§lCloudTeam§f] §c请先将队长转移给别人，或者解散队伍！"));
                }else if(CloudDungeonBC.playerTeam.containsKey(player.getUniqueId())){
                    UUID leader=CloudDungeonBC.playerTeam.get(player.getUniqueId());
                    ConcurrentSkipListSet<UUID> uuids = CloudDungeonBC.teams.get(leader);
                    ProxiedPlayer leaderp=plugin.getProxy().getPlayer(leader);
                    if(leader!=null){
                        leaderp.sendMessage(new TextComponent("§f[§e§lCloudTeam§f] §c玩家：§e§l"+player.getName()+"§c离开队伍啦！"));
                    }
                    uuids.remove(player.getUniqueId());
                    if (leader != null) {
                        CloudDungeonBC.teams.put(leader,uuids);
                    }
                    CloudDungeonBC.playerTeam.remove(player.getUniqueId());
                    player.sendMessage(new TextComponent("§f[§e§lCloudTeam§f] §c你已经成功退出退伍啦"));
                }
                return;
            }else if(strings.length==3&&strings[1].equals("kick")){
                if(!CloudDungeonBC.playerTeam.containsKey(player.getUniqueId())){
                    player.sendMessage(new TextComponent("§f[§e§lCloudTeam§f] §c你当前不在队伍中！无法执行此操作。"));
                    return;
                }
                if(!CloudDungeonBC.teams.containsKey(player.getUniqueId())){
                    player.sendMessage(new TextComponent("§f[§e§lCloudTeam§f] §c你不是小队队长，无法执行此操作！"));
                    return;
                }
                ConcurrentSkipListSet<UUID> players = CloudDungeonBC.teams.get(player.getUniqueId());
                String memName=strings[2];
                ProxiedPlayer mem=plugin.getProxy().getPlayer(memName);
                if(mem==null){
                    player.sendMessage(new TextComponent("§f[§e§lCloudTeam§f] §c玩家不在线或不存在！"));
                    return;
                }
                UUID memUUID=mem.getUniqueId();
                if(memUUID.equals(player.getUniqueId())){
                    player.sendMessage(new TextComponent("§f[§e§lCloudTeam§f] §c你不能把你自己踢出队伍哦！"));
                    return;
                }
                if(!players.contains(memUUID)){
                    player.sendMessage(new TextComponent("§f[§e§lCloudTeam§f] §c玩家：§e§l"+memName+"§c不在你的队伍中！"));
                }
                players.remove(memUUID);
                CloudDungeonBC.teams.put(player.getUniqueId(),players);
                player.sendMessage(new TextComponent("§f[§e§lCloudTeam§f] §c玩家：§e§l"+memName+"§c离开队伍啦！"));
                mem.sendMessage(new TextComponent("§f[§e§lCloudTeam§f] §c你已经被队长踢出队伍啦。"));
                refreshTeamTimeOut(player.getUniqueId());
                return;
            }
            else if(strings.length==3&&strings[1].equals("invite")){
                if(!CloudDungeonBC.playerTeam.containsKey(player.getUniqueId())){
                    player.sendMessage(new TextComponent("§f[§e§lCloudTeam§f] §c你当前不在队伍中！无法执行此操作。"));
                    return;
                }
                if(!CloudDungeonBC.teams.containsKey(player.getUniqueId())){
                    player.sendMessage(new TextComponent("§f[§e§lCloudTeam§f] §c你不是小队队长，无法执行此操作！"));
                    return;
                }

                String memName=strings[2];
                ProxiedPlayer mem=plugin.getProxy().getPlayer(memName);
                if(mem==null){
                    player.sendMessage(new TextComponent("§f[§e§lCloudTeam§f] §c玩家不在线或不存在！"));
                    return;
                }
                ConcurrentSkipListSet<UUID> players = CloudDungeonBC.teams.get(player.getUniqueId());
                UUID memUUID=mem.getUniqueId();
                if(players.contains(memUUID)){
                    player.sendMessage(new TextComponent("§f[§e§lCloudTeam§f] §c玩家：§e§l"+memName+"§c已经在队伍中啦！"));
                    return;
                }
                if(CloudDungeonBC.teamRequest.containsKey(memUUID)){
                    player.sendMessage(new TextComponent("§f[§e§lCloudTeam§f] §c玩家：§e§l"+memName+"§c有一个邀请请求啦，请稍后再邀请！"));
                    return;
                }
                CloudDungeonBC.teamRequest.put(memUUID,player.getUniqueId());
                ScheduledTask schedule = plugin.getProxy().getScheduler().schedule(plugin, () -> {
                    CloudDungeonBC.teamRequest.remove(memUUID);
                }, 30, TimeUnit.SECONDS);
                CloudDungeonBC.teamRequestTimeOutTask.put(memUUID,schedule);
                player.sendMessage(new TextComponent("§f[§e§lCloudTeam§f] §f队伍邀请请求已经发送给玩家：§e§l"+memName));
                TextComponent acceptMessage=new TextComponent("§e§l[✔]");
                acceptMessage.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,"/cdt team accept"));
                acceptMessage.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,new ComponentBuilder("点击同意加入队伍").create()));
                mem.sendMessage(new TextComponent("§f[§e§lCloudTeam§f] §f玩家：§e§l"+player.getName()+"§f向你发来一个队伍邀请请求！"),acceptMessage);
                refreshTeamTimeOut(player.getUniqueId());
                return;
            }
            else if(strings.length==2&&strings[1].equals("accept")){
                if(CloudDungeonBC.playerTeam.containsKey(player.getUniqueId())){
                    player.sendMessage(new TextComponent("§f[§e§lCloudTeam§f] §c你当已经队伍中！无法执行此操作。"));
                    return;
                }
                if(!CloudDungeonBC.teamRequest.containsKey(player.getUniqueId())){
                    player.sendMessage(new TextComponent("§f[§e§lCloudTeam§f] §c你当前没有组队邀请请求！"));
                    return;
                }
                UUID leader=CloudDungeonBC.teamRequest.get(player.getUniqueId());
                CloudDungeonBC.playerTeam.put(player.getUniqueId(),leader);
                ConcurrentSkipListSet<UUID> players = CloudDungeonBC.teams.get(leader);
                players.add(player.getUniqueId());
                CloudDungeonBC.teams.put(leader,players);
                ProxiedPlayer leaderP=plugin.getProxy().getPlayer(leader);
                if(leaderP!=null){
                    leaderP.sendMessage(new TextComponent("§f[§e§lCloudTeam§f] §f玩家：§e§l"+player.getName()+"§f加入队伍啦！"));
                }
                CloudDungeonBC.teamRequestTimeOutTask.get(player.getUniqueId()).cancel();
                CloudDungeonBC.teamRequest.remove(player.getUniqueId());
                player.sendMessage(new TextComponent("§f[§e§lCloudTeam§f] §f你已经成功加入队伍！"));
                refreshTeamTimeOut(leader);
                return;
            }else if(strings.length==2&&strings[1].equals("list")){
                if(!CloudDungeonBC.playerTeam.containsKey(player.getUniqueId())){
                    player.sendMessage(new TextComponent("§f[§e§lCloudTeam§f] §c你当前不在队伍中！无法执行此操作。"));
                    return;
                }
                UUID leader=CloudDungeonBC.playerTeam.get(player.getUniqueId());
                ConcurrentSkipListSet<UUID> players = CloudDungeonBC.teams.get(leader);
                player.sendMessage(new TextComponent("§f=================[§e§lCloudTeam§f]================"));
                ProxiedPlayer leaderPlayer = plugin.getProxy().getPlayer(leader);
                if(leaderPlayer!=null){
                    player.sendMessage(new TextComponent("§f队长："+leaderPlayer.getName()));
                }else {
                    player.sendMessage(new TextComponent("§f队长：离线玩家"));
                }
                for(UUID p:players){
                    if(!p.equals(leader)){
                        ProxiedPlayer player1 = plugin.getProxy().getPlayer(p);
                        if(player1!=null){
                            player.sendMessage(new TextComponent("成员："+player1.getName()));
                        }else {
                            player.sendMessage(new TextComponent("成员：离线玩家"));
                        }
                    }
                }
                return;
            }
        }
        player.sendMessage(new TextComponent("§f====================[§e§lCloudTeam§f]==================="));
        player.sendMessage(new TextComponent("§b/cdt team create             §f创建队伍"));
        player.sendMessage(new TextComponent("§b/cdt team disband            §f解散队伍"));
        player.sendMessage(new TextComponent("§b/cdt team quit               §f退出队伍"));
        player.sendMessage(new TextComponent("§b/cdt team kick <player>      §f请离队伍"));
        player.sendMessage(new TextComponent("§b/cdt team invite <player>    §f邀请队员"));
        player.sendMessage(new TextComponent("§b/cdt team list               §f查看队员"));
        player.sendMessage(new TextComponent("§b/cdt team accept             §f同意队伍邀请"));
        player.sendMessage(new TextComponent("§f==================================================="));
    }

    private void createTeam(ProxiedPlayer player){
        ConcurrentSkipListSet<UUID> listSet = new ConcurrentSkipListSet<>();
        listSet.add(player.getUniqueId());
        CloudDungeonBC.teams.put(player.getUniqueId(),listSet);
        CloudDungeonBC.playerTeam.put(player.getUniqueId(),player.getUniqueId());
        player.sendMessage(new TextComponent("§f[§e§lCloudTeam§f] §f队伍创建成功"));
    }

    public static void refreshTeamTimeOut(UUID uuid){
        if(!CloudDungeonBC.teams.containsKey(uuid)){
            return;
        }
        if(CloudDungeonBC.teamTimeOutTask.containsKey(uuid)){
            CloudDungeonBC.teamTimeOutTask.get(uuid).cancel();
        }
        ScheduledTask task = CloudDungeonBC.plugin.getProxy().getScheduler().schedule(CloudDungeonBC.plugin,
                () -> {
                    ConcurrentSkipListSet<UUID> team = CloudDungeonBC.teams.get(uuid);
                    for(UUID uid:team){
                        CloudDungeonBC.playerTeam.remove(uid);
                        ProxiedPlayer player = CloudDungeonBC.plugin.getProxy().getPlayer(uid);
                        if(player!=null){
                            player.sendMessage(new TextComponent("§f[§e§lCloudTeam§f] §c队伍长时间未操作，已经自动解散啦！"));
                        }
                    }
                    CloudDungeonBC.teams.remove(uuid);
                }
                , 10, TimeUnit.MINUTES);
        CloudDungeonBC.teamTimeOutTask.put(uuid,task);

    }

    @Override
    public Iterable<String> onTabComplete(CommandSender commandSender, String[] strings) {
        if(strings.length==1){
            return List.of("team");
        }else if(strings.length==2){
            if(strings[0].equals("team")){
                return Arrays.asList("create","disband","quit","kick","invite","accept","list");
            }
        }else if(strings.length==3){
            List<String> list=new ArrayList<>();
            if(strings[1].equals("kick")){

            }else if(strings[1].equals("invite")){
              for(ProxiedPlayer player:plugin.getProxy().getPlayers()){
                  list.add(player.getName());
              }
            }
            return list;
        }
        return new ArrayList<>();
    }
}
