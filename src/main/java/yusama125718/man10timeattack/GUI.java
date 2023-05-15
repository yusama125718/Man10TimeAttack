package yusama125718.man10timeattack;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import static yusama125718.man10timeattack.Function.GetItem;
import static yusama125718.man10timeattack.Man10TimeAttack.stages;

public class GUI {

    public static void OpenMenu(Player p, Integer page){
        Inventory inv = Bukkit.createInventory(null,54, Component.text("[Man10TimeAttack] コース選択"+ page));
        for (int i = 51;i < 54;i++){
            inv.setItem(i,GetItem(Material.BLUE_STAINED_GLASS_PANE,1,"次のページへ",1));
            inv.setItem(i - 3,GetItem(Material.WHITE_STAINED_GLASS_PANE,1,"",1));
            inv.setItem(i - 6,GetItem(Material.RED_STAINED_GLASS_PANE,1,"前のページへ",1));
        }
        for (int i = 0;i < stages.size();i++){
            if (i == 45 || stages.size() == i + 45 * (page - 1)) break;
            Man10TimeAttack.StageData t = stages.get(i + 45 * (page));
            inv.setItem(i, t.icon.clone());
        }
        p.openInventory(inv);
    }
}
