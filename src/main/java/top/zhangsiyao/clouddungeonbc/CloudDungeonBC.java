package top.zhangsiyao.clouddungeonbc;

import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.scheduler.ScheduledTask;
import top.zhangsiyao.clouddungeonbc.command.DLCommand;
import top.zhangsiyao.clouddungeonbc.listener.PluginMessageReceiveListener;
import top.zhangsiyao.clouddungeonbc.listener.ServerChangeListener;
import top.zhangsiyao.clouddungeonbc.manger.PluginManger;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.logging.Level;

public class CloudDungeonBC extends Plugin{


    public static ConcurrentHashMap<UUID, ConcurrentSkipListSet<UUID>> teams;

    public static  ConcurrentHashMap<UUID,UUID> playerTeam;


    public static ConcurrentSkipListSet<String> dpServers;

    public static ConcurrentHashMap<UUID,UUID> teamRequest;

    public static ConcurrentHashMap<UUID,ScheduledTask> teamTimeOutTask;

    public static ConcurrentHashMap<UUID, ScheduledTask> teamRequestTimeOutTask;

    public static PluginManger pluginManger;

    public static Plugin plugin;

    @Override
    public void onEnable() {
        plugin=this;
        getLogger().log(Level.INFO,"加载地牢BC插件中。。");
        init();
        getProxy().getPluginManager().registerListener(this, new PluginMessageReceiveListener(this));
        getProxy().getPluginManager().registerListener(this,new ServerChangeListener());
        registerCommand();
        getLogger().log(Level.INFO,"注册通道");
        getProxy().registerChannel("dungeonplus:bungeecore");
        pluginManger=new PluginManger(this);
    }


    private void init(){
        teams=new ConcurrentHashMap<>();
        playerTeam=new ConcurrentHashMap<>();
        teamRequest=new ConcurrentHashMap<>();
        teamTimeOutTask=new ConcurrentHashMap<>();
        teamRequestTimeOutTask=new ConcurrentHashMap<>();
        dpServers=new ConcurrentSkipListSet<>();
    }

    private void registerCommand(){
        getProxy().getPluginManager().registerCommand(this, new DLCommand(this));
    }



}
