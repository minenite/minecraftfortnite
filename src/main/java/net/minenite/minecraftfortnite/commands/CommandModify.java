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
package net.minenite.minecraftfortnite.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.minenite.minecraftfortnite.MinecraftFortnite;
import net.minenite.minecraftfortnite.storage.EnumDataDirection;
import org.bukkit.Location;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

@CommandAlias("mfmodify|mfm")
@CommandPermission("minecraftfortnite.modify")
public class CommandModify extends BaseCommand {

    private final MinecraftFortnite plugin;

    public CommandModify(MinecraftFortnite main) {
        plugin = main;
    }

    @Subcommand("spawnLoc")
    @CommandCompletion("spawnLoc")
    public void modifySpawn(Player player) {
        plugin.getStorage().serialize(EnumDataDirection.TO_SPAWN_LOCATION, player.getLocation());
        player.spigot().sendMessage(new ComponentBuilder("Set successful!").color(ChatColor.GREEN).create());
    }

    @Subcommand("chestLoc")
    @CommandCompletion("chestLoc")
    public void addChest(Player player) {
        Location location = player.getLocation();
        Vector direction = location.getDirection();
        Location blockLocation = new Location(player.getWorld(), direction.getBlockX(),
                direction.getBlockY(), direction.getBlockZ());
        if (!(blockLocation.getBlock() instanceof Chest)) {
            player.spigot().sendMessage(new ComponentBuilder("Facing block is not a chest").color(ChatColor.RED).create());
            return;
        }
        plugin.getStorage().serialize(EnumDataDirection.TO_CHEST_LOCATION, blockLocation);
        player.spigot().sendMessage(new ComponentBuilder("Set successful!").color(ChatColor.GREEN).create());
    }
}
