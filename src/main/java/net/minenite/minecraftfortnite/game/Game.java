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

import java.util.List;

import net.minenite.minecraftfortnite.MinecraftFortnite;
import net.minenite.minecraftfortnite.game.timer.TimeParser;
import net.minenite.minecraftfortnite.game.timer.Timer;
import net.minenite.minecraftfortnite.storage.EnumDataDirection;
import net.minenite.minecraftfortnite.util.PlayerUtil;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

public class Game {

    private final MinecraftFortnite plugin;
    private final List<ItemStack> items;
    private final int peopleToStart;
    private final Timer timer;
    private PeopleChecker peopleChecker;
    private boolean gameFired;

    public Game(MinecraftFortnite plugin, List<ItemStack> items, int peopleToStart) {
        this.plugin = plugin;
        this.items = items;
        gameFired = false;
        this.peopleToStart = peopleToStart;
        timer = new Timer(plugin, TimeParser.parseActualTime("10m"));
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
                if (timer.isTimeElapsed()) {
                    task.cancel();
                    plugin.getServer().getOnlinePlayers().forEach(player -> {
                        PlayerUtil.stopSendingAnimatedActionbar(player,
                                "&aTime to start: " + timer.getFormattedTime());
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
        plugin.getServer().getOnlinePlayers().forEach(player -> PlayerUtil.sendAnimatedActionbar(player, "&aTime to start: " + timer.getFormattedTime()));
    }

    public void start() {
        // todo
        setItemsInsideChests();
        gameFired = true;
    }

    private void setItemsInsideChests() {
        List<Location> chestLocations =
                plugin.getStorage().deserialize(EnumDataDirection.TO_CHEST_LOCATION);
        for (int i = 0; i < chestLocations.size(); i++) {
            ChestRandomizer randomizer = new ChestRandomizer(plugin, i, items);
            randomizer.set();
        }
        chestLocations.clear(); // get rid of memory
    }

    private void kickPlayer(Player player) {
        player.kickPlayer(ChatColor.YELLOW + "Connected players were not equal for start of game." +
                " \n" + ChatColor.YELLOW + "We need to empty the server and restart");
    }
}
