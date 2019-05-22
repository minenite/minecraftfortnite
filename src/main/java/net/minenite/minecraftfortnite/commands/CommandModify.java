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
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.minenite.minecraftfortnite.MinecraftFortnite;
import net.minenite.minecraftfortnite.storage.EnumDataDirection;
import org.bukkit.Location;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;

@CommandAlias("mfmodify|mfm")
@CommandPermission("minecraftfortnite.modify")
public class CommandModify extends BaseCommand {

    private final MinecraftFortnite plugin;

    public CommandModify(MinecraftFortnite main) {
        plugin = main;
    }

    @Subcommand("spawnLoc")
    public void modifySpawn(Player player) {
        plugin.getStorage().serialize(EnumDataDirection.TO_SPAWN_LOCATION, player.getLocation());
        player.spigot().sendMessage(new ComponentBuilder("Set successful!").color(ChatColor.GREEN).create());
    }

    @Subcommand("chestLoc")
    public void addChest(Player player) {
        boolean found = false;
        Location blockLocation = null;
        for (BlockState state : player.getLocation().getBlock().getChunk().getTileEntities()) {
            if (state.getBlock() instanceof Chest) {
                found = true;
                blockLocation = state.getBlock().getLocation();
            }
        }
        if (!found) {
            player.spigot().sendMessage(new ComponentBuilder("No chest found in the chunk you " +
                    "are in.").color(ChatColor.RED).create());
            return;
        }
        plugin.getStorage().serialize(EnumDataDirection.TO_CHEST_LOCATION, blockLocation);
        player.spigot().sendMessage(new ComponentBuilder("Chest coordinates: " + blockLocation.toString() + ". Run /mfmodify removeChestLoc for undo")
                .color(ChatColor.GREEN).create());
    }

    @Subcommand("removeChestLoc")
    public void removeChest(Player player) {
        boolean found = false;
        Location blockLocation = null;
        for (BlockState state : player.getLocation().getBlock().getChunk().getTileEntities()) {
            if (state.getBlock() instanceof Chest) {
                found = true;
                blockLocation = state.getBlock().getLocation();
            }
        }
        if (!found) {
            player.spigot().sendMessage(new ComponentBuilder("No chest found in the chunk you " +
                    "are in.").color(ChatColor.RED).create());
            return;
        }
        if (!plugin.getStorage().contains(EnumDataDirection.TO_CHEST_LOCATION, blockLocation)) {
            player.spigot().sendMessage(new ComponentBuilder("Found chest is not registered. Run " +
                    "/mfmodify chestLoc to save it.").color(ChatColor.YELLOW).create());
            return;
        }
        plugin.getStorage().remove(EnumDataDirection.TO_CHEST_LOCATION, blockLocation);
        player.spigot().sendMessage(new ComponentBuilder("Remove success!").color(ChatColor.GREEN).create());
    }
}
