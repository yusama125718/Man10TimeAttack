package yusama125718.man10timeattack;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.metadata.MetadataValue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import static java.lang.Integer.parseInt;
import static yusama125718.man10timeattack.Man10TimeAttack.*;

public class Event {
    public Event(Man10TimeAttack plugin) {
        plugin.getServer().getPluginManager().registerEvents((Listener) this, plugin);
    }

    @EventHandler
    public void MenuGUIClick(InventoryClickEvent e) {     //メインメニュー
        if (e.getInventory().getSize() != 54) return;
        String title = null;
        Component component = e.getView().title();
        if (component instanceof TextComponent text) title = text.content();
        if (title == null || !title.startsWith("[Man10TimeAttack] コース選択")) return;
        if (e.getCurrentItem() == null) {
            e.setCancelled(true);
            return;
        }
        boolean isNumeric = title.substring(24).matches("-?\\d+");
        if (!isNumeric) return;
        int page = parseInt(title.substring(24));
        if (51 <= e.getRawSlot() && e.getRawSlot() <= 53 && e.getCurrentItem().getType().equals(Material.BLUE_STAINED_GLASS_PANE)){    //次のページへ
            if (stages.size() / 45 > page) GUI.OpenMenu((Player) e.getWhoClicked(),page + 1);
            e.setCancelled(true);
            return;
        }
        if (45 <= e.getRawSlot() && e.getRawSlot() <= 47 && e.getCurrentItem().getType().equals(Material.RED_STAINED_GLASS_PANE)){     //前のページへ
            if (page != 1) GUI.OpenMenu((Player) e.getWhoClicked(),page -1);
            e.setCancelled(true);
            return;
        }
        if (45 <= e.getRawSlot() && e.getRawSlot() <= 53 || e.getRawSlot() + 45 * (page) >= stages.size()) {
            e.setCancelled(true);
            return;
        }
        Function.StartStage((Player) e.getWhoClicked(), stages.get(e.getRawSlot() + 45 * (page)));
        e.getWhoClicked().closeInventory();
        e.setCancelled(true);
    }

    @EventHandler
    public void OnPlayerMove(PlayerMoveEvent e){        //ゴール判定処理
        if (!system || !e.getPlayer().hasMetadata("mta.stage") || !e.getPlayer().getLocation().getWorld().getName().equals(world)) return;
        List<MetadataValue> meta = e.getPlayer().getMetadata("mta.stage");
        String name = null;
        for (MetadataValue v : meta) {
            if (v.getOwningPlugin().getName().equals(mta.getName())) {
                name = v.asString();
                break;
            }
        }
        if (name == null) return;
        RegionManager region = WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(Bukkit.getWorld(world)));
        if (region == null) return;
        Location loc = e.getPlayer().getLocation();
        Set<ProtectedRegion> regions = region.getApplicableRegions(BlockVector3.at(loc.getX(), loc.getY(), loc.getZ())).getRegions();
        ProtectedRegion target = null;
        for (ProtectedRegion t : regions){
            if (t.getId().equals(name)){
                target = t;
                break;
            }
        }
        if (target == null) return;
        long start = 0;
        for (MetadataValue v : meta) {
            if (v.getOwningPlugin().getName().equals(mta.getName())) {
                start = v.asLong();
                break;
            }
        }
        if (start == 0) return;
        long now = LocalDateTime.now().atZone(ZoneOffset.ofHours(+9)).toInstant().toEpochMilli();
        long between = now - start;
        if (record.get(e.getPlayer().getUniqueId()).get(name) == null){
            if (record.get(e.getPlayer().getUniqueId()) == null){
                HashMap<String, Long> data = new HashMap<>();
                data.put(name, between);
                record.put(e.getPlayer().getUniqueId(), data);
            }
            else record.get(e.getPlayer().getUniqueId()).put(name, between);
            YamlConfiguration yml = YamlConfiguration.loadConfiguration(Paths.get(recordfile.getAbsolutePath() + File.pathSeparator + name + ".yml").toFile());
            yml.set(e.getPlayer().getUniqueId().toString(), between);
            try {
                yml.save(recordfile.getAbsolutePath() + File.pathSeparator + name + ".yml");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        else if (between < record.get(e.getPlayer().getUniqueId()).get(name)){
            record.get(e.getPlayer().getUniqueId()).put(name, between);
            YamlConfiguration yml = YamlConfiguration.loadConfiguration(Paths.get(recordfile.getAbsolutePath() + File.pathSeparator + name + ".yml").toFile());
            yml.set(e.getPlayer().getUniqueId().toString(), between);
            try {
                yml.save(recordfile.getAbsolutePath() + File.pathSeparator + name + ".yml");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    @EventHandler
    public void OnPlayerJoin(PlayerJoinEvent e){
        Config.GetRecord(e.getPlayer().getUniqueId());
    }

    @EventHandler
    public void OnPlayerLeft(PlayerQuitEvent e){
        record.remove(e.getPlayer().getUniqueId());
    }
}
