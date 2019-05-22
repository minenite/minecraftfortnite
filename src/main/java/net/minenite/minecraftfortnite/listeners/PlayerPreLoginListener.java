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

import java.util.HashSet;
import java.util.Set;

import net.minenite.minecraftfortnite.MinecraftFortnite;
import net.minenite.minecraftfortnite.storage.EnumDataDirection;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

public class PlayerPreLoginListener implements Listener {

    private final MinecraftFortnite plugin;

    public PlayerPreLoginListener(MinecraftFortnite main) {
        plugin = main;
    }

    @EventHandler
    public void on(AsyncPlayerPreLoginEvent event) {
        Location tpLoc = plugin.getStorage().deserialize(EnumDataDirection.TO_SPAWN_LOCATION, 0);
        Set<String> names = new HashSet<>();
        plugin.getServer().getWhitelistedPlayers().forEach(offline -> names.add(offline.getName()));
        if (tpLoc == null) {
            names.forEach(name -> {
                if (!event.getName().equalsIgnoreCase(name)) {
                    event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, ChatColor.RED + "Sorry, " +
                            "but the setup is still not finished.");
                }
            });
        }
        if (plugin.getGame().isCurrentlyPlaying()) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, ChatColor.YELLOW + "There" +
                    "'s already running game...");
        }
    }
}
