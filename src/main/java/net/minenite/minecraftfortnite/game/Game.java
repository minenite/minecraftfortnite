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

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import net.minenite.minecraftfortnite.MinecraftFortnite;
import net.minenite.minecraftfortnite.game.timer.TimeParser;
import net.minenite.minecraftfortnite.game.timer.Timer;
import net.minenite.minecraftfortnite.storage.EnumDataDirection;
import net.minenite.minecraftfortnite.util.PlayerUtil;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

public class Game {

    private final MinecraftFortnite plugin;
    private final int peopleToStart;
    private final Timer timer;
    private final Map<UUID, BukkitTask> tasks;
    private PeopleChecker peopleChecker;
    private boolean gameFired;

    public Game(MinecraftFortnite plugin, int peopleToStart) {
        this.plugin = plugin;
        gameFired = false;
        this.peopleToStart = peopleToStart;
        timer = new Timer(plugin, TimeParser.parseActualTime("10m"));
        tasks = new ConcurrentHashMap<>();
    }

    public boolean isCurrentlyPlaying() {
        return gameFired;
    }

    public void startChecker() {
        timer.startTimer();
        peopleChecker = new PeopleChecker(plugin, peopleToStart);
        peopleChecker.startChecking();
        new Runnable() {

            final BukkitTask task = plugin.getServer().getScheduler().runTaskTimer(plugin, this, 0, 20);

            @Override
            public void run() {
                if ((timer.isTimeElapsed() || timer.isStopped()) && !peopleChecker.isPlayersInside()) {
                    task.cancel();
                    plugin.getServer().getOnlinePlayers().forEach(player -> {
                        tasks.get(player.getUniqueId()).cancel();
                        tasks.remove(player.getUniqueId());
                        kickPlayer(player);
                    });
                }
            }
        };
    }

    public void stopChecker() {
        timer.stopTimer();
        if (peopleChecker != null) {
            peopleChecker.stopChecking();
        }
    }

    public boolean hasPlayersToStart() {
        return peopleChecker.isPlayersInside();
    }

    public void sendTimeToStart() {
        plugin.getServer().getOnlinePlayers().forEach(player -> {
            new Runnable() {

                final BukkitTask task = plugin.getServer().getScheduler().runTaskTimer(plugin, this, 0,
                        20);

                @Override
                public void run() {
                    if (!tasks.containsKey(player.getUniqueId())) {
                        tasks.put(player.getUniqueId(), task);
                    }
                    PlayerUtil.sendActionbarMessage(player, "&aTime " +
                            "to start: " + timer.getFormattedTime());
                }
            };
        });
    }

    public void start() {
        // todo
        plugin.getServer().getOnlinePlayers().forEach(player -> player.getInventory().clear());
        setItemsInsideChests();
        gameFired = true;
    }

    private void setItemsInsideChests() {
        List<Location> chestLocations =
                plugin.getStorage().deserialize(EnumDataDirection.TO_CHEST_LOCATION);
        ItemStack notchApple = new ItemStack(Material.ENCHANTED_GOLDEN_APPLE, 1);
        ItemStack goldenApple = new ItemStack(Material.GOLDEN_APPLE, 1);
        ItemStack porkchop = new ItemStack(Material.COOKED_PORKCHOP, 1);
        List<ItemStack> otherItems = Arrays.asList(notchApple, goldenApple, porkchop);
        ChestItemManager itemManager = new ChestItemManager(otherItems);
        for (int i = 0; i < chestLocations.size(); i++) {
            ChestRandomizer randomizer = new ChestRandomizer(plugin, i, itemManager.getRandom());
            randomizer.set();
        }
        chestLocations.clear(); // get rid of memory
    }

    private void kickPlayer(Player player) {
        player.kickPlayer(ChatColor.YELLOW + "Connected players were not equal for start of game." +
                " \n" + ChatColor.YELLOW + "We need to empty the server and restart");
    }
}
