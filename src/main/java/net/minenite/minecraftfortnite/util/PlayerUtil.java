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

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.UUID;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import net.minenite.minecraftfortnite.MinecraftFortnite;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

public class PlayerUtil {

    private static final MinecraftFortnite plugin = MinecraftFortnite.getPlugin(MinecraftFortnite.class);
    private static final Multimap<UUID, Pair<String, BukkitTask>> animatedBars =
            ArrayListMultimap.create();

    public static void sendAnimatedActionbar(Player player, String message) {
        new Runnable() {

            final BukkitTask task = plugin.getServer().getScheduler().runTaskTimer(plugin, this, 0, 20);

            @Override
            public void run() {
                Pair<String, BukkitTask> pair = new Pair<>(message, task);
                if (!animatedBars.containsValue(pair)) {
                    animatedBars.put(player.getUniqueId(), pair);
                }
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                        TextComponent.fromLegacyText(plugin.colorize(message)));
            }
        };
    }

    public static void stopSendingAnimatedActionbar(Player player, String message) {
        String colorizedMessage = plugin.colorize(message);
        Collection<Pair<String, BukkitTask>> collection = animatedBars.get(player.getUniqueId());
        Iterator<Pair<String, BukkitTask>> iterator = collection.iterator();
        while (iterator.hasNext()) {
            Pair<String, BukkitTask> pair = iterator.next();
            if (pair.getKey().toLowerCase().equalsIgnoreCase(colorizedMessage.toLowerCase())) {
                pair.getValue().cancel();
                iterator.remove();
            }
        }
        collection.clear();
        Collection<Pair<String, BukkitTask>> remaining = new HashSet<>();
        iterator.forEachRemaining(remaining::add);
        animatedBars.replaceValues(player.getUniqueId(), remaining);
    }

    public static void sendActionbarMessage(Player player, String message) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                TextComponent.fromLegacyText(plugin.colorize(message)));
    }
}
