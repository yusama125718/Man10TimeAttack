package yusama125718.man10timeattack;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

import static java.lang.Integer.parseInt;
import static net.kyori.adventure.text.event.ClickEvent.runCommand;
import static yusama125718.man10timeattack.Man10TimeAttack.*;

public class Command implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, org.bukkit.command.@NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)){
            sender.sendMessage(prefix + "コンソールからは実行できません");
            return true;
        }
        if (!sender.hasPermission("mta.p")) return true;
        switch (args.length){
            case 0:
                if (!system){
                    sender.sendMessage(prefix + "現在停止中です");
                    return true;
                }
                GUI.OpenMenu((Player) sender, 1);
                break;

            case 1:
                if (args[0].equals("help")){
                    sender.sendMessage(prefix + " §7/mta §rメインメニューを開きます");
                    sender.sendMessage(prefix + " §7/mta cancel §rゲームを中断し、終了します");
                    sender.sendMessage(prefix + " §7/mta record §r自分の記録を表示します");
                    sender.sendMessage(prefix + " §7/mta record [MCID] §r他人の記録を表示します");
                    if (sender.hasPermission("mta.op")){
                        sender.sendMessage("===== 運営コマンド =====");
                        sender.sendMessage("==== 全体設定 ====");
                        sender.sendMessage(prefix + " §7/mta on §rシステムをonにします");
                        sender.sendMessage(prefix + " §7/mta off §rシステムをoffにします");
                        sender.sendMessage(prefix + " §7/mta setworld §rステージに使用するワールドを設定します");
                        sender.sendMessage(prefix + " §7/mta setlobby §r現在地をロビーのスポーン地点にします");
                        sender.sendMessage("==== ゲーム設定 ====");
                        sender.sendMessage(prefix + " §7/mta new [内部名] [名前] §r新しいコースを作ります");
                        sender.sendMessage(prefix + " ※手に持っている物がアイコンになります");
                        sender.sendMessage(prefix + " ※作成したら内部名と同名のRegionをWorldGuardで作成してください");
                        sender.sendMessage(prefix + " §7/mta setspawn [内部名] §rステージのスポーン地点を変更します");
                        sender.sendMessage(prefix + " §7/mta seticon [内部名] §rステージのアイコンを手に持っている物に変更します");
                        sender.sendMessage("==== ゲーム動作 ====");
                        sender.sendMessage(prefix + " §7/mta start [内部名] §rゲームを始めます");
                        sender.sendMessage(prefix + " ※プレイヤーも実行可能");
                        sender.sendMessage(prefix + " §7/mta ranking [内部名] [ページ]§rランキングを表示します");
                        sender.sendMessage(prefix + " ※プレイヤーも実行可能");
                    }
                    return true;
                }
                else if (sender.hasPermission("mta.op") && args[0].equals("on")){
                    system = true;
                    mta.getConfig().set("system", system);
                    mta.saveConfig();
                    sender.sendMessage(prefix + "ONにしました");
                    return true;
                }
                else if (sender.hasPermission("mta.op") && args[0].equals("off")){
                    system = false;
                    mta.getConfig().set("system", system);
                    mta.saveConfig();
                    sender.sendMessage(prefix + "OFFにしました");
                    return true;
                }
                else if (sender.hasPermission("mta.op") && args[0].equals("setlobby")){
                    lobby = ((Player) sender).getLocation();
                    mta.getConfig().set("lobby", lobby);
                    mta.saveConfig();
                    sender.sendMessage(prefix + "設定しました");
                    return true;
                }
                else if (sender.hasPermission("mta.op") && args[0].equals("setworld")){
                    world = ((Player) sender).getWorld().getName();
                    mta.getConfig().set("world", world);
                    mta.saveConfig();
                    sender.sendMessage(prefix + "設定しました");
                    return true;
                }
                else if (sender.hasPermission("mta.op") && args[0].equals("reload")){
                    Function.LoadConfig();
                    return true;
                }
                else if (args[0].equals("cancel")){
                    Player p = (Player) sender;
                    if (!p.hasMetadata("mta.stage")){
                        sender.sendMessage(prefix + "ゲーム中ではありません");
                        return true;
                    }
                    p.removeMetadata("mta.stage", mta);
                    p.removeMetadata("mta.time", mta);
                    p.teleport(lobby);
                    sender.sendMessage(prefix + "中断しました");
                    return true;
                }
                else if (args[0].equals("record")){
                    UUID p = ((Player) sender).getUniqueId();
                    if (!record.containsKey(p)){
                        sender.sendMessage(prefix + sender.getName() + "のデータはありません");
                        return true;
                    }
                    sender.sendMessage(prefix + sender.getName() + "の記録");
                    for (String s : record.get(p).keySet()){
                        RecordData d = record.get(p).get(s);
                        sender.sendMessage(prefix + d.display + "：" + Function.GetTime(d.time));
                    }
                    return true;
                }
                else if (args[0].equals("ranking")){
                    GUI.OpenRankMenu((Player) sender, 1);
                    return true;
                }
                break;

            case 2:
                if (sender.hasPermission("mta.op") && args[0].equals("setspawn")){
                    StageData target = null;
                    for (Man10TimeAttack.StageData s : stages) {
                        if (!s.name.equals(args[1])) continue;
                        target = s;
                        break;
                    }
                    if (target == null){
                        sender.sendMessage(prefix + args[1] + "は存在しません");
                        return true;
                    }
                    target.spawn = ((Player) sender).getLocation();
                    try {
                        Config.SaveYaml(target);
                        sender.sendMessage(prefix + "設定しました");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return true;
                }
                else if (sender.hasPermission("mta.op") && args[0].equals("seticon")){
                    StageData target = null;
                    for (Man10TimeAttack.StageData s : stages) {
                        if (!s.name.equals(args[1])) continue;
                        target = s;
                        break;
                    }
                    if (target == null){
                        sender.sendMessage(prefix + args[1] + "は存在しません");
                        return true;
                    }
                    ItemStack icon = ((Player) sender).getInventory().getItemInMainHand();
                    if (icon == null) icon = new ItemStack(Material.WHITE_STAINED_GLASS_PANE);
                    target.icon = icon;
                    try {
                        Config.SaveYaml(target);
                        sender.sendMessage(prefix + "設定しました");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return true;
                }
                else if (args[0].equals("start")){
                    if (!system){
                        sender.sendMessage(prefix + "現在停止中です");
                        return true;
                    }
                    StageData target = null;
                    for (Man10TimeAttack.StageData s : stages) {
                        if (!s.name.equals(args[1])) continue;
                        target = s;
                        break;
                    }
                    if (target == null){
                        sender.sendMessage(prefix + args[1] + "は存在しません");
                        return true;
                    }
                    long time = LocalDateTime.now().atZone(ZoneOffset.ofHours(+9)).toInstant().toEpochMilli();
                    ((Player) sender).setMetadata("mta.time", new FixedMetadataValue(mta,time));
                    ((Player) sender).setMetadata("mta.stage", new FixedMetadataValue(mta,target.name));
                    ((Player) sender).teleport(target.spawn);
                    sender.sendMessage(prefix + target.display + "をスタートしました。");
                    return true;
                }
                else if (args[0].equals("ranking")){
                    StageData target = null;
                    for (Man10TimeAttack.StageData s : stages) {
                        if (!s.name.equals(args[1])) continue;
                        target = s;
                        break;
                    }
                    if (target == null){
                        sender.sendMessage(prefix + args[1] + "は存在しません");
                        return true;
                    }
                    Function.SendRank((Player) sender, target, 1);
                    return true;
                }
                else if (args[0].equals("record")){
                    Player player = Bukkit.getPlayerExact(args[1]);
                    if (player == null){
                        sender.sendMessage(prefix + "そのプレイヤーは存在しません");
                        return true;
                    }
                    UUID p = player.getUniqueId();
                    if (!record.containsKey(p)){
                        sender.sendMessage(prefix + player.getName() + "のデータはありません");
                        return true;
                    }
                    sender.sendMessage(prefix + player.getName() + "の記録");
                    for (String s : record.get(p).keySet()){
                        RecordData d = record.get(p).get(s);
                        sender.sendMessage(prefix + d.display + "：" + Function.GetTime(d.time));
                    }
                    return true;
                }
                break;

            case 3:
                if (sender.hasPermission("mta.op") && args[0].equals("new")){
                    List<String> namelist = new ArrayList<>();
                    for (Man10TimeAttack.StageData s : stages) namelist.add(s.name);
                    if (namelist.contains(args[1])){
                        sender.sendMessage(prefix + "その名前は既に使われています");
                        return true;
                    }
                    Location spawn = ((Player) sender).getLocation();
                    ItemStack icon = ((Player) sender).getInventory().getItemInMainHand();
                    if (icon == null) icon = new ItemStack(Material.WHITE_STAINED_GLASS_PANE);
                    StageData content = new StageData(args[1], args[2], spawn, icon, null);
                    stages.add(content);
                    try {
                        Config.SaveYaml(content);
                        File folder = new File(recordfile.getAbsolutePath() + File.separator + args[1] + ".yml");
                        YamlConfiguration yml = new YamlConfiguration();
                        yml.save(folder);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    sender.sendMessage(prefix + "作成しました");
                    return true;
                }
                else if (args[0].equals("ranking")){
                    boolean isNumeric = args[2].matches("-?\\d+");
                    if (!isNumeric) {
                        sender.sendMessage(prefix + "整数を入力してください");
                        return true;
                    }
                    int page = parseInt(args[2]);
                    StageData target = null;
                    for (Man10TimeAttack.StageData s : stages) {
                        if (!s.name.equals(args[1])) continue;
                        target = s;
                        break;
                    }
                    if (target == null){
                        sender.sendMessage(prefix + args[1] + "は存在しません");
                        return true;
                    }
                    Function.SendRank((Player) sender, target, page);
                    return true;
                }
                break;

            default:
                sender.sendMessage(prefix + "/mta helpでコマンドを確認");
                break;

        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, org.bukkit.command.@NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return null;
    }
}
