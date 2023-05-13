package yusama125718.man10timeattack;

import static yusama125718.man10timeattack.Man10TimeAttack.*;

public class Function {

    public static void LoadConfig(){
        stages.clear();
        mta.saveDefaultConfig();
        system = mta.getConfig().getBoolean("system");
        prefix = mta.getConfig().getString("prefix");
        Config.LoadFile();
        Config.LoadYaml();
    }
}
