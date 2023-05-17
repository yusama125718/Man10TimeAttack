package yusama125718.man10timeattack;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

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
            Man10TimeAttack.StageData t = stages.get(i + 45 * (page - 1));
            ItemStack item = t.icon.clone();
            if (t.desc != null){
                List<Component> lore = new ArrayList<>();
                for (String s : t.desc) lore.add(Component.text(s));
                item.lore(lore);
            }
            ItemMeta meta = item.getItemMeta();
            meta.displayName(Component.text(t.display));
            item.setItemMeta(meta);
            inv.setItem(i, item);
        }
        p.openInventory(inv);
    }

    public static void OpenRankMenu(Player p, Integer page){
        Inventory inv = Bukkit.createInventory(null,54, Component.text("[Man10TimeAttack] ランキング"+ page));
        for (int i = 51;i < 54;i++){
            inv.setItem(i,GetItem(Material.BLUE_STAINED_GLASS_PANE,1,"次のページへ",1));
            inv.setItem(i - 3,GetItem(Material.WHITE_STAINED_GLASS_PANE,1,"",1));
            inv.setItem(i - 6,GetItem(Material.RED_STAINED_GLASS_PANE,1,"前のページへ",1));
        }
        for (int i = 0;i < stages.size();i++){
            if (i == 45 || stages.size() == i + 45 * (page - 1)) break;
            Man10TimeAttack.StageData t = stages.get(i + 45 * (page - 1));
            ItemStack item = t.icon.clone();
            if (t.desc != null){
                List<Component> lore = new ArrayList<>();
                for (String s : t.desc) lore.add(Component.text(s));
                item.lore(lore);
            }
            ItemMeta meta = item.getItemMeta();
            meta.displayName(Component.text(t.display));
            item.setItemMeta(meta);
            inv.setItem(i, item);
        }
        p.openInventory(inv);
    }
}
