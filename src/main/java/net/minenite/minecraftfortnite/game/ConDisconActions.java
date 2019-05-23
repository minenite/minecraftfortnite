/*
 * Copyright 2019 Ivan Pekov (MrIvanPlays)

 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in the
 * Software without restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

 * The above copyright notice and this permission notice shall be included in all copies
 * or substantial portions of the Software.

 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 **/
package net.minenite.minecraftfortnite.game;

import java.util.ArrayList;
import java.util.List;

import net.minenite.minecraftfortnite.MinecraftFortnite;
import net.minenite.minecraftfortnite.storage.EnumDataDirection;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;

public class ConDisconActions {

    public static void connectActions(Player player, MinecraftFortnite plugin) {
        Location teleportTo = plugin.getStorage().deserialize(EnumDataDirection.TO_SPAWN_LOCATION, 0);
        if (teleportTo == null) {
            return;
        }
        player.teleport(teleportTo);
        Bukkit.getOnlinePlayers().forEach(online -> {
            online.hidePlayer(plugin, player);
            player.hidePlayer(plugin, online);
        });
        ItemStack mapItem = new ItemStack(Material.FILLED_MAP, 1);
        MapMeta mapMeta = (MapMeta) mapItem.getItemMeta();
        mapMeta.setMapView(plugin.getMapView());
        mapMeta.setDisplayName(plugin.colorize("&aView map"));
        mapItem.setItemMeta(mapMeta);
        player.getInventory().setItem(0, mapItem);
    }

    public static void disconnectActions(Player player, MinecraftFortnite plugin) {
        List<Player> toUnhide = new ArrayList<>();
        player.spigot().getHiddenPlayers().forEach(hidden -> {
            toUnhide.add(hidden);
        });
        for (Player hidden : toUnhide) {
            player.showPlayer(plugin, hidden);
            hidden.showPlayer(plugin, player);
        }
        toUnhide.clear();
        player.getInventory().clear();
    }
}
