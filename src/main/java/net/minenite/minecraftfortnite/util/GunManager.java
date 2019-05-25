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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import me.matt.pvpgunplus.gun.Bullet;
import me.matt.pvpgunplus.gun.Gun;
import me.matt.pvpgunplus.gun.GunPlayer;
import me.matt.pvpgunplus.gun.WeaponReader;
import net.minenite.minecraftfortnite.MinecraftFortnite;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

/**
 * This class represents the guns plugin main without some things
 */
public class GunManager {

    private final List<Bullet> bullets;
    private final List<Gun> loadedGuns;
    private final List<GunPlayer> players;
    private final MinecraftFortnite plugin;
    private int updateTimer;

    public GunManager(MinecraftFortnite plugin) {
        this.plugin = plugin;
        bullets = new ArrayList<>();
        loadedGuns = new ArrayList<>();
        players = new ArrayList<>();
    }

    public void callThisOnDisable() {
        clearMemory(true);
    }

    public void callThisOnEnable() {
        startup(true);
    }

    private void startup(boolean init) {
        updateTimer = plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new UpdateTimer(), 20L, 1L);
        String pluginFolder = plugin.getDataFolder().getAbsolutePath();
        File dir = new File(pluginFolder);
        if (!dir.exists()) {
            dir.mkdir();
        }
        File dir2 = new File(pluginFolder + "/guns");
        if (!dir2.exists()) {
            dir2.mkdir();
        }
        dir2 = new File(pluginFolder + "/projectile");
        if (!dir2.exists()) {
            dir2.mkdir();
        }
        if (init) {
            String projectilePath = pluginFolder + "/projectile";
            File projectileDir = new File(projectilePath);
            String[] projectileChildren = projectileDir.list();
            if (projectileChildren != null) {
                for (String filename : projectileChildren) {
                    WeaponReader f = new WeaponReader(plugin, new File(projectilePath + "/" + filename), "gun");
                    if (f.loaded) {
                        f.ret.node = "Guns." + filename.toLowerCase();
                        loadedGuns.add(f.ret);
                        f.ret.setIsThrowable(true);
                        plugin.getLogger().info("LOADED PROJECTILE: " + f.ret.getName());
                    } else {
                        plugin.getLogger().info("FAILED TO PROJECTILE GUN: " + f.ret.getName());
                    }
                }
            }
            String gunsPath = pluginFolder + "/guns";
            File gunsDir = new File(gunsPath);
            String[] gunChildren = gunsDir.list();
            if (gunChildren != null) {
                for (String filename : gunChildren) {
                    WeaponReader f = new WeaponReader(plugin, new File(gunsPath + "/" + filename), "gun");
                    if (f.loaded) {
                        f.ret.node = "Guns." + filename.toLowerCase();
                        loadedGuns.add(f.ret);
                        plugin.getLogger().info("LOADED GUN: " + f.ret.getName());
                    } else {
                        plugin.getLogger().info("FAILED TO LOAD GUN: " + f.ret.getName());
                    }
                }
            }
        }
    }

    public Sound getSound(String gunSound) {
        String snd = gunSound.toUpperCase().replace(" ", "_");
        return Sound.valueOf(snd);
    }

    public Gun getGun(Material material) {
        for (Gun gun : loadedGuns) {
            if (gun.getGunType() == material) {
                return gun;
            }
        }
        return null;
    }

    public List<Gun> getLoadedGuns() {
        return loadedGuns;
    }

    public GunPlayer getGunPlayer(Player player) {
        for (int i = players.size() - 1; i >= 0; --i) {
            if (players.get(i).getPlayer().equals(player)) {
                return players.get(i);
            }
        }
        return null;
    }

    public void onJoin(Player player) {
        if (getGunPlayer(player) == null) {
            GunPlayer gp = new GunPlayer(plugin, player);
            players.add(gp);
        }
    }

    public void onQuit(Player player) {
        for (int i = players.size() - 1; i >= 0; --i) {
            if (players.get(i).getPlayer().getName().equals(player.getName())) {
                players.remove(i);
            }
        }
    }

    public void removeBullet(Bullet bullet) {
        bullets.remove(bullet);
    }

    public void addBullet(Bullet bullet) {
        bullets.add(bullet);
    }

    public Bullet getBullet(Entity proj) {
        for (int i = bullets.size() - 1; i >= 0; --i) {
            if (bullets.get(i).getProjectile().getEntityId() == proj.getEntityId()) {
                return bullets.get(i);
            }
        }
        return null;
    }

    public void playEffect(Effect e, Location l, int num) {
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            player.playEffect(l, e, num);
        }
    }

    private void clearMemory(boolean init) {
        plugin.getServer().getScheduler().cancelTask(updateTimer);
        for (int i = bullets.size() - 1; i >= 0; --i) {
            bullets.get(i).destroy();
        }
        for (int i = players.size() - 1; i >= 0; --i) {
            players.get(i).unload();
        }
        if (init) {
            loadedGuns.clear();
        }
        bullets.clear();
        players.clear();
    }

    class UpdateTimer implements Runnable {
        @Override
        public void run() {
            for (int i = players.size() - 1; i >= 0; --i) {
                GunPlayer gp = players.get(i);
                if (gp != null) {
                    gp.tick();
                }
            }
            for (int i = bullets.size() - 1; i >= 0; --i) {
                Bullet t = bullets.get(i);
                if (t != null) {
                    t.tick();
                }
            }
        }
    }
}
