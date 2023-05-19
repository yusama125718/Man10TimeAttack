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
import java.util.*;
import java.util.stream.Collectors;

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
        saved = mta.getConfig().getBoolean("saved");
        Config.LoadFile();
        Config.LoadRecordFile();
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
        if (saved) Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "replay start " + p.getName() + "_" +  t.name + " " + p.getName());
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
        HashMap<UUID, Long> stagerecord = new HashMap<>();
        if (rank == null || rankname == null || !rankname.equals(target.name)){
            for (UUID uuid : record.keySet()){
                if (!record.get(uuid).containsKey(target.name)) continue;
                long time = record.get(uuid).get(target.name).time;
                stagerecord.put(uuid, time);
            }
            rankname = target.name;
            rank = stagerecord.entrySet().stream()
                    .sorted(java.util.Map.Entry.comparingByValue(Long::compareTo))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
        }
        if (rank.size() <= (page - 1) * 10){
            p.sendMessage(prefix + "データがありません");
            return;
        }
        p.sendMessage(prefix + target.display + "のランキング " + page);
        int i = 0;
        for (UUID user : rank.keySet()){
            if (i < (page - 1) * 10){
                i++;
                continue;
            }
            else if (i >= rank.size() || i >= page * 10) break;
            p.sendMessage("§l§e" + (i + 1) + "位§r§l:§b§l" + Bukkit.getOfflinePlayer(user).getName() + " §e§lタイム§r§l:§c§l" + GetTime(rank.get(user)));
            i++;
        }
        if (page != 1) p.sendMessage(Component.text(prefix + "§b前のページ").clickEvent(runCommand("/mta ranking " + target.name + " " + (page - 1))));
        p.sendMessage(Component.text(prefix + "§b次のページ").clickEvent(runCommand("/mta ranking " + target.name + " " + (page + 1))));
    }
}
