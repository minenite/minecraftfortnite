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
package net.minenite.minecraftfortnite.util;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import net.minenite.minecraftfortnite.MinecraftFortnite;
import org.bukkit.entity.Player;

public class PlayerUtil {

    private static final MinecraftFortnite plugin = MinecraftFortnite.getPlugin(MinecraftFortnite.class);
    private static final Map<UUID, Pair<String, Integer>> animatedBars = new ConcurrentHashMap<>();

    public static void sendAnimatedActionbar(Player player, String message) {
        animatedBars.put(player.getUniqueId(), new Pair<>(message, 0));
       plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
           sendActionbarMessage(player, message);
           // todo
       }, 0, 20);
    }

    public static void stopSendingAnimatedActionbar(Player player, String message) {
        // todo
    }

    public static void sendActionbarMessage(Player player, String message) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                TextComponent.fromLegacyText(plugin.colorize(message)));
    }
}
