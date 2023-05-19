package yusama125718.man10timeattack;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.*;

public final class Man10TimeAttack extends JavaPlugin {

    public static JavaPlugin mta;
    public static Boolean system;
    public static Boolean saved;
    public static List<StageData> stages = new ArrayList<>();
    public static HashMap<UUID, HashMap<String, RecordData>> record = new HashMap<>();
    public static LinkedHashMap<UUID, Long> rank = new LinkedHashMap<>();
    public static String rankname;
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
        public List<String> desc;

        public StageData(String NAME, String DISPLAY, Location SPAWN, ItemStack ICON, List<String> DESC){
            name = NAME;
            display = DISPLAY;
            spawn = SPAWN;
            icon = ICON;
            desc = DESC;
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
