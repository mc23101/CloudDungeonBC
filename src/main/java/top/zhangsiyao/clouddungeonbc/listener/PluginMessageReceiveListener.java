package top.zhangsiyao.clouddungeonbc.listener;

import com.google.gson.Gson;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;
import top.zhangsiyao.clouddungeonbc.command.DLCommand;
import top.zhangsiyao.clouddungeonbc.util.DungeonUtil;
import top.zhangsiyao.clouddungeonbc.util.MessageDataPacket;

import java.nio.charset.StandardCharsets;

public class PluginMessageReceiveListener implements Listener {


    private final Plugin plugin;

    public PluginMessageReceiveListener(Plugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void process(PluginMessageEvent event){
        if(event.getTag().equals("dungeonplus:bungeecore")){
            byte[] data = event.getData();
            Gson gson=new Gson();
            MessageDataPacket messageDataPacket = gson.fromJson(new String(data, StandardCharsets.UTF_8), MessageDataPacket.class);
            String cmd=new String(messageDataPacket.data,StandardCharsets.UTF_8);
            if(cmd.startsWith("start")){
                String[] split = cmd.split(":");
                DungeonUtil.start(messageDataPacket.players.get(0), split[1]);
                DLCommand.refreshTeamTimeOut(messageDataPacket.players.get(0));
            }else if(cmd.startsWith("end")){
                String[] split = cmd.split(":");
                DungeonUtil.end(messageDataPacket.players.get(0),split[1]);
            }
        }
    }

}
