package yusama125718.man10timeattack;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static yusama125718.man10timeattack.Man10TimeAttack.*;

public class Config {

    private static final File folder = new File(mta.getDataFolder().getAbsolutePath() + File.separator + "stages");
    private static final File datafolder = new File(mta.getDataFolder().getAbsolutePath() + File.separator + "records");

    public static void LoadFile(){
        if (mta.getDataFolder().listFiles() != null){
            for (File file : Objects.requireNonNull(mta.getDataFolder().listFiles())) {
                if (file.getName().equals("stages")) {
                    configfile = file;
                    return;
                }
            }
        }
        if (folder.mkdir()) {
            Bukkit.broadcast(Component.text(prefix + "§rステージフォルダを作成しました"), "magri.op");
            configfile = folder;
        } else {
            Bukkit.broadcast(Component.text(prefix + "§rステージフォルダの作成に失敗しました"), "magri.op");
        }
    }

    public static void LoadRecordFile(){
        if (mta.getDataFolder().listFiles() != null){
            for (File file : Objects.requireNonNull(mta.getDataFolder().listFiles())) {
                if (file.getName().equals("records")) {
                    recordfile = file;
                    return;
                }
            }
        }
        if (datafolder.mkdir()) {
            Bukkit.broadcast(Component.text(prefix + "§rデータフォルダを作成しました"), "magri.op");
            recordfile = datafolder;
        } else {
            Bukkit.broadcast(Component.text(prefix + "§rデータフォルダの作成に失敗しました"), "magri.op");
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
            List<String> desc = yml.getStringList("desc");
            stages.add(new StageData(name, display, spawn, icon, desc));
        }
    }

    public static void SaveYaml(StageData stage) throws IOException {
        File folder = new File(configfile.getAbsolutePath() + File.separator + stage.name + ".yml");
        YamlConfiguration yml = new YamlConfiguration();
        yml.set("name", stage.name);
        yml.set("display", stage.display);
        yml.set("spawn", stage.spawn);
        yml.set("icon", stage.icon);
        yml.set("desc", stage.desc);
        yml.save(folder);
    }

    public static void GetRecord(){
        if (recordfile.listFiles() == null) return;
        for (File file : recordfile.listFiles()){
            YamlConfiguration yml =  YamlConfiguration.loadConfiguration(file);
            String filename = file.getName();
            String name = filename.substring(0,filename.lastIndexOf('.'));
            String display = yml.getString("display");
            for (String s : yml.getKeys(false)){
                if (s.equals("display")) continue;
                Long time = yml.getLong(s);
                UUID p = UUID.fromString(s);
                if (record.containsKey(p)) record.get(p).put(name, new RecordData(display, time));
                else {
                    HashMap<String, RecordData> data = new HashMap<>();
                    data.put(name, new RecordData(display, time));
                    record.put(p, data);
                }
            }
        }
    }
}
