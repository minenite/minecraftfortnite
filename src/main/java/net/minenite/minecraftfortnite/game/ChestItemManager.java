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
import java.util.concurrent.ThreadLocalRandom;

import net.minenite.minecraftfortnite.util.GunManager;
import org.bukkit.inventory.ItemStack;

public class ChestItemManager {

    private final List<ItemStack> allPossible;
    private final ThreadLocalRandom random;

    public ChestItemManager(List<ItemStack> additionalItems) {
        allPossible = new ArrayList<>();
        allPossible.addAll(additionalItems);
        GunManager gunManager = new GunManager();
        allPossible.addAll(gunManager.getGunItems());
        random = ThreadLocalRandom.current();
    }

    public List<ItemStack> getRandom() {
        List<ItemStack> list = new ArrayList<>();
        int randomMinusNumber = random.nextInt(0, allPossible.size());
        for (int i = 0; i < allPossible.size() - randomMinusNumber; i++) {
            int anotherRandom = random.nextInt(i);
            list.add(allPossible.get(anotherRandom));
        }
        return list;
    }
}