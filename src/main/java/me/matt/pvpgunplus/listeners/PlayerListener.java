// 
// Decompiled by Procyon v0.5.34
// 

package me.matt.pvpgunplus.listeners;

import me.matt.pvpgunplus.gun.Gun;
import me.matt.pvpgunplus.gun.GunPlayer;
import net.minenite.minecraftfortnite.MinecraftFortnite;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerListener implements Listener {

    private final MinecraftFortnite plugin;

    public PlayerListener(MinecraftFortnite plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerJoin(PlayerJoinEvent event) {
        plugin.getGunManager().onJoin(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerQuit(PlayerQuitEvent event) {
        plugin.getGunManager().onQuit(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Item dropped = event.getItemDrop();
        Player dropper = event.getPlayer();
        GunPlayer gp = plugin.getGunManager().getGunPlayer(dropper);
        if (gp != null) {
            ItemStack lastHold = gp.getLastItemHeld();
            if (lastHold != null) {
                Gun gun = gp.getGun(dropped.getItemStack().getType());
                if (gun != null && lastHold.equals(dropped.getItemStack()) && gun.hasClip && gun.changed && gun.reloadGunOnDrop) {
                    gun.reloadGun();
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onRespawn(PlayerRespawnEvent event) {
        GunPlayer gp = plugin.getGunManager().getGunPlayer(event.getPlayer());
        if (gp != null) {
            gp.reloadAllGuns();
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Action action = event.getAction();
        Player player = event.getPlayer();
        ItemStack itm1 = player.getInventory().getItemInMainHand();
        if (itm1 != null && (action.equals(Action.LEFT_CLICK_AIR) || action.equals(Action.LEFT_CLICK_BLOCK) || action.equals(Action.RIGHT_CLICK_AIR)
                || action.equals(Action.RIGHT_CLICK_BLOCK))) {
            String clickType = "left";
            if (action.equals(Action.RIGHT_CLICK_AIR) || action.equals(Action.RIGHT_CLICK_BLOCK)) {
                clickType = "right";
            }
            GunPlayer gp = plugin.getGunManager().getGunPlayer(player);
            if (gp != null) {
                gp.onClick(clickType);
            }
        }
    }

    // TODO: create this event on a command
//    @EventHandler(priority = EventPriority.NORMAL)
//    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
//        Player player = event.getPlayer();
//        String[] split = event.getMessage().split(" ");
//        split[0] = split[0].substring(1);
//        String label = split[0];
//        String[] args = new String[split.length - 1];
//        for (int i = 1; i < split.length; ++i) {
//            args[i - 1] = split[i];
//        }
//        if (label.equalsIgnoreCase("guns") && args.length == 0) {
//            player.sendMessage(ChatColor.DARK_AQUA + "----" + ChatColor.GRAY + "[" + ChatColor.YELLOW + "Guns" + ChatColor.GRAY + "]" + ChatColor.DARK_AQUA + "----");
//            player.sendMessage(ChatColor.GRAY + "/guns " + ChatColor.GREEN + "reload" + ChatColor.WHITE + " to reload the plugin.");
//        }
//        try {
//            if (label.equalsIgnoreCase("guns") && args[0].equals("reload") && Permissions.checkPermission(player, "guns.admin")) {
//                plugin.getGunManager().reload(true);
//                player.sendMessage(ChatColor.GREEN + "The plugin has successfully been reloaded.");
//            }
//        } catch (Exception ex) {
//        }
//    }
}
