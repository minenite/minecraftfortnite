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
package net.minenite.minecraftfortnite.listeners;

import java.util.Set;
import java.util.stream.Collectors;

import net.minenite.minecraftfortnite.MinecraftFortnite;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class InventoryClickListener implements Listener {

    private final MinecraftFortnite plugin;

    public InventoryClickListener(MinecraftFortnite main) {
        plugin = main;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!plugin.getGame().isCurrentlyPlaying()) {
            Set<String> names =
                    plugin.getServer().getWhitelistedPlayers().stream().map(OfflinePlayer::getName).collect(Collectors.toSet());
            System.out.println(names);
            System.out.println(event.getWhoClicked() instanceof Player);
            if (event.getWhoClicked() instanceof Player) {
                Player player = (Player) event.getWhoClicked();
                names.forEach(name -> {
                    System.out.println(player.getName().toLowerCase().equalsIgnoreCase(name.toLowerCase()));
                    if (!player.getName().toLowerCase().equalsIgnoreCase(name.toLowerCase())) {
                        event.setCancelled(true);
                    }
                });
            }
        }
    }
}
