package yusama125718.man10timeattack;

import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public final class Man10TimeAttack extends JavaPlugin {

    public static JavaPlugin mta;
    public static Boolean system;
    public static List<StageData> stages = new ArrayList<>();
    public static String prefix;
    public static File configfile;

    public static class StageData{
        public String name;
        public Location spawn;
        public String display;

        public StageData(String NAME, String DISPLAY, Location SPAWN){
            name = NAME;
            display = DISPLAY;
            spawn = SPAWN;
        }
    }

    @Override
    public void onEnable() {
        getCommand("mta").setExecutor(new Command());
        mta = this;
        Function.LoadConfig();
    }
}
