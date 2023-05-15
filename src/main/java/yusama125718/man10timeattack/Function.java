package yusama125718.man10timeattack;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static yusama125718.man10timeattack.Man10TimeAttack.*;

public class Function {

    public static void LoadConfig(){
        stages.clear();
        mta.saveDefaultConfig();
        system = mta.getConfig().getBoolean("system");
        prefix = mta.getConfig().getString("prefix");
        world = mta.getConfig().getString("world");
        Config.LoadFile();
        Config.LoadYaml();
        Bukkit.broadcast(Component.text(prefix + "ロード完了"), "mta.op");
    }

    public static ItemStack GetItem(Material mate, Integer amount, String name, Integer cmd){
        ItemStack item = new ItemStack(mate,amount);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text(name));
        meta.setCustomModelData(cmd);
        item.setItemMeta(meta);
        return item;
    }

    public static void StartStage(Player p, StageData t){
        long time = LocalDateTime.now().atZone(ZoneOffset.ofHours(+9)).toInstant().toEpochMilli();
        p.setMetadata("mta.time", new FixedMetadataValue(mta,time));
        p.setMetadata("mta.stage", new FixedMetadataValue(mta,t.name));
        p.teleport(t.spawn);
        p.sendMessage(prefix + t.display + "をスタートしました。");
    }
}
