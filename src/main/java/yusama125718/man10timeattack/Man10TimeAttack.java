package yusama125718.man10timeattack;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public final class Man10TimeAttack extends JavaPlugin {

    public static JavaPlugin mta;
    public static Boolean system;
    public static List<StageData> stages = new ArrayList<>();
    public static HashMap<UUID, HashMap<String, RecordData>> record = new HashMap<>();
    public static String prefix;
    public static String world;
    public static File configfile;
    public static File recordfile;
    public static Location lobby;

    public static class StageData{
        public String name;
        public String display;
        public Location spawn;
        public ItemStack icon;

        public StageData(String NAME, String DISPLAY, Location SPAWN, ItemStack ICON){
            name = NAME;
            display = DISPLAY;
            spawn = SPAWN;
            icon = ICON;
        }
    }

    public static class RecordData{
        public String display;
        public Long time;

        public RecordData(String DISPLAY, Long TIME){
            display = DISPLAY;
            time = TIME;
        }
    }

    @Override
    public void onEnable() {
        mta = this;
        getCommand("mta").setExecutor(new Command());
        new Event(this);
        Function.LoadConfig();
    }
}
