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
import net.minenite.minecraftfortnite.storage.EnumDataDirection;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

public class Game {

    private final MinecraftFortnite plugin;
    private final List<ItemStack> items;
    private final int peopleToStart;
    private PeopleChecker peopleChecker;
    private boolean gameFired;

    public Game(MinecraftFortnite plugin, List<ItemStack> items, int peopleToStart) {
        this.plugin = plugin;
        this.items = items;
        gameFired = false;
        this.peopleToStart = peopleToStart;
    }

    public boolean isCurrentlyPlaying() {
        return gameFired;
    }

    public void startChecker() {
        // todo: timer
        peopleChecker = new PeopleChecker(plugin, peopleToStart);
        peopleChecker.startChecking();
    }

    public void stopChecker() {
        // todo: timer
        if (peopleChecker != null) {
            peopleChecker.stopChecking();
        }
    }

    public void start() {
        // todo
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
}
