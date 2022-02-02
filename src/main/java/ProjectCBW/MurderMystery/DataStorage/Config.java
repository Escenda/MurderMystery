package ProjectCBW.MurderMystery.DataStorage;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;

public class Config extends YamlConfiguration {

    private final Plugin plugin;
    private final String fileName;

    public Config(Plugin plugin, String fileName){
        this.plugin = plugin;
        this.fileName = fileName;
        save();
    }

    public void save() {
        try {
            File file = new File(plugin.getDataFolder(), fileName);
            if (!file.exists()){
                if (plugin.getResource(fileName) != null){
                    plugin.saveResource(fileName, false);
                }else{
                    save(file);
                }
            }else{
                load(file);
                save(file);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}