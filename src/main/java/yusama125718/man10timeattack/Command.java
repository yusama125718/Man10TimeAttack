package yusama125718.man10timeattack;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
                break;

            case 1:
                if (args[0].equals("help")){
                    sender.sendMessage(prefix + " §7/mta §rメインメニューを開きます");
                    if (sender.hasPermission("mta.op")){
                        sender.sendMessage("===== 運営コマンド =====");
                        sender.sendMessage(prefix + " §7/mta new [内部名] [名前] §r新しいコースを作ります");
                        sender.sendMessage(prefix + " §7/mta setspawn [内部名] §rステージのスポーン地点を変更します");
                        sender.sendMessage(prefix + " ※作成したら内部名と同名のRegionをWorldGuardで作成してください");
                    }
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
                    } catch (IOException e) {
                        e.printStackTrace();
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
                    StageData content = new StageData(args[1], args[2], ((Player) sender).getLocation());
                    stages.add(content);
                    try {
                        Config.SaveYaml(content);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    sender.sendMessage(prefix + "作成しました");
                    return true;
                }
                break;

            default:
                break;

        }
        sender.sendMessage(prefix + "/mta helpでコマンドを確認");
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, org.bukkit.command.@NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return null;
    }
}
