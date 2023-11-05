package top.zhangsiyao.clouddungeonbc.listener;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import top.zhangsiyao.clouddungeonbc.CloudDungeonBC;

public class ServerChangeListener implements Listener {


    @EventHandler()
    public void process(ServerSwitchEvent event){
        ProxiedPlayer player = event.getPlayer();
    }
}
