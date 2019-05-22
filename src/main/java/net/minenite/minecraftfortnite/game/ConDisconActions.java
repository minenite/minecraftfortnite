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

import net.minenite.minecraftfortnite.MinecraftFortnite;
import net.minenite.minecraftfortnite.storage.EnumDataDirection;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

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
        // todo: add rendered map to slot 0
    }

    public static void disconnectActions(Player player, MinecraftFortnite plugin) {
        player.spigot().getHiddenPlayers().forEach(hidden -> {
            player.showPlayer(plugin, hidden);
            hidden.showPlayer(plugin, player);
        });
        player.getInventory().clear();
    }
}