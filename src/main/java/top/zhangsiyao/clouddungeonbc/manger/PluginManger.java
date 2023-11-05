package top.zhangsiyao.clouddungeonbc.manger;

import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import top.zhangsiyao.clouddungeonbc.CloudDungeonBC;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

public class PluginManger {
    private final Plugin plugin;

    private Map<String,String> dungeon;

    private Integer delay;

    private String mainServer;

    public PluginManger(Plugin plugin) {
        this.plugin = plugin;
        reload();
    }

    public void reload(){
        if (!plugin.getDataFolder().exists()){
            plugin.getDataFolder().mkdir();
        }
        File file = new File(plugin.getDataFolder(), "config.yml");


        if (!file.exists()) {
            try (InputStream in = plugin.getResourceAsStream("config.yml")) {
                Files.copy(in, file.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            Configuration configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
            this.delay=configuration.getInt("setting.delay");
            this.mainServer=configuration.getString("setting.main-server");
            this.dungeon=new HashMap<>();
            for(String key:configuration.getSection("dungeon").getKeys()){
                String serverName = configuration.getSection("dungeon").getString(key);
                dungeon.put(key,serverName);
                CloudDungeonBC.dpServers.add(serverName);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Map<String, String> getDungeon() {
        return dungeon;
    }

    public Integer getDelay() {
        return delay;
    }

    public String getMainServer() {
        return mainServer;
    }
}
