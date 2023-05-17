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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static net.kyori.adventure.text.event.ClickEvent.runCommand;
import static yusama125718.man10timeattack.Man10TimeAttack.*;

public class Function {

    public static void LoadConfig(){
        stages.clear();
        mta.saveDefaultConfig();
        system = mta.getConfig().getBoolean("system");
        prefix = mta.getConfig().getString("prefix");
        world = mta.getConfig().getString("world");
        lobby = mta.getConfig().getLocation("lobby");
        Config.LoadFile();
        Config.LoadYaml();
        Config.GetRecord();
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

    public static String GetTime(Long t){
        double h = Math.floor(t / 3600000);
        double m = Math.floor((t - (h * 3600000)) / 60000);
        double s = Math.floor((t - (h * 3600000) - (m * 60000)) / 1000);
        double ms = t - (h * 3600000) - (m * 60000) - (s * 1000);
        String H = String.valueOf(h);
        String M = String.valueOf(m);
        String S = String.valueOf(s);
        String MS = String.valueOf(ms);
        if (h == 0 && m == 0){
            return (S.substring(0,S.lastIndexOf('.')) + "." + MS.substring(0,MS.lastIndexOf('.')) + "秒");
        }
        else if (h == 0){
            return (M.substring(0,M.lastIndexOf('.')) + "分" + S.substring(0,S.lastIndexOf('.')) + "." + MS.substring(0,MS.lastIndexOf('.')) + "秒");
        }
        else return (H.substring(0,H.lastIndexOf('.')) + "時間" + M.substring(0,M.lastIndexOf('.')) + "分" + S.substring(0,S.lastIndexOf('.')) + "." + MS.substring(0,MS.lastIndexOf('.')) + "秒");
    }

    public static void SendRank(Player p, StageData target, Integer page){
        if (rankname == null || !rankname.equals(target.name)){
            rank.clear();
            List<Long> ranktime = new ArrayList<>();
            for (UUID uuid : record.keySet()){
                System.out.println("aaa");
                if (!record.get(uuid).containsKey(target.name)) continue;
                long time = record.get(uuid).get(target.name).time;
                stagerecord.put(uuid, time);
                if (ranktime.size() == 0){
                    ranktime.add(time);
                    rank.add(uuid);
                    continue;
                }
                for (int i = 0; i < ranktime.size(); i++){
                    if (ranktime.get(i) < time) continue;
                    rank.add(i, uuid);
                    ranktime.add(i,time);
                    break;
                }
            }
            rankname = target.name;
        }
        System.out.println(rank);
        p.sendMessage(prefix + target.display + "のランキング " + page);
        for (int i = 10 * (page - 1); i < 10 + 10 * (page - 1); i++){
            System.out.println("iii");
            if (i >= rank.size()) break;
            p.sendMessage("§e" + (i + 1) + "位§r:" + Bukkit.getPlayer(rank.get(i)).getName() + " タイム:" + GetTime(stagerecord.get(rank.get(i))));
        }
        if (page != 1) p.sendMessage(Component.text(prefix + "§b前のページ").clickEvent(runCommand("/mta ranking " + target.name + " " + (page - 1))));
        p.sendMessage(Component.text(prefix + "§b次のページ").clickEvent(runCommand("/mta ranking " + target.name + " " + (page + 1))));
    }
}
