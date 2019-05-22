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
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

public class PeopleChecker {

    private final int requiredToStart;
    private final MinecraftFortnite plugin;
    private BukkitTask task;
    private boolean currentlyHas;

    public PeopleChecker(MinecraftFortnite plugin, int requiredToStart) {
        this.requiredToStart = requiredToStart;
        this.plugin = plugin;
        currentlyHas = false;
    }

    public void startChecking() {
        new Runnable() {

            BukkitTask task = Bukkit.getServer().getScheduler().runTaskTimer(plugin, this, 0, 20);

            @Override
            public void run() {
                task = task;
                int playerSize = Bukkit.getOnlinePlayers().size();
                if (playerSize == requiredToStart) {
                    currentlyHas = true;
                } else {
                    currentlyHas = false;
                }
            }
        };
    }

    public void stopChecking() {
        task.cancel();
    }

    public boolean isPlayersInside() {
        return currentlyHas;
    }

}