package yusama125718.man10timeattack;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import static yusama125718.man10timeattack.Man10TimeAttack.*;

public class Config {

    private static final File folder = new File(mta.getDataFolder().getAbsolutePath() + File.separator + "stages");

    public static void LoadFile(){
        if (mta.getDataFolder().listFiles() != null){
            for (File file : Objects.requireNonNull(mta.getDataFolder().listFiles())) {
                if (file.getName().equals("recipes")) {
                    configfile = file;
                    return;
                }
            }
        }
        if (folder.mkdir()) {
            Bukkit.broadcast(prefix + "§rステージフォルダを作成しました", "magri.op");
            configfile = folder;
        } else {
            Bukkit.broadcast(prefix + "§rステージフォルダの作成に失敗しました", "magri.op");
        }
    }

    public static void LoadYaml(){
        if (configfile.listFiles() == null) return;
        for (File file : configfile.listFiles()){
            YamlConfiguration yml =  YamlConfiguration.loadConfiguration(file);
            if (yml.get("name") == null || yml.get("spawn") == null || yml.get("display") == null) continue;
            String name = yml.getString("name");
            String display = yml.getString("display");
            Location spawn = yml.getLocation("spawn");
            ItemStack icon = yml.getItemStack("icon");
            stages.add(new StageData(name, display, spawn, icon));
        }
    }

    public static void SaveYaml(StageData stage) throws IOException {
        File folder = new File(configfile.getAbsolutePath() + File.separator + stage.name + ".yml");
        YamlConfiguration yml = new YamlConfiguration();
        yml.set("name", stage.name);
        yml.set("display", stage.display);
        yml.set("spawn", stage.spawn);
        yml.set("icon", stage.icon);
        yml.save(folder);
    }
}
