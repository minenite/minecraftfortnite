// 
// Decompiled by Procyon v0.5.34
// 

package me.matt.pvpgunplus.utils;

import java.util.ArrayList;
import java.util.Random;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_14_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftFirework;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;

public class Explosion {

    private final Location location;

    public Explosion(Location location) {
        this.location = location;
    }

    public void explode() {
        World world = location.getWorld();
        CraftWorld craftWorld = (CraftWorld) world;
        Firework bfirework = (Firework) location.getWorld().spawn(location, (Class) Firework.class);
        bfirework.setFireworkMeta((FireworkMeta) getFirework().getItemMeta());
        CraftFirework a = (CraftFirework) bfirework;
        craftWorld.getHandle().broadcastEntityEffect(a.getHandle(), (byte) 17);
        bfirework.remove();
    }

    private ItemStack getFirework() {
        Random rand = new Random();
        FireworkEffect.Type type = FireworkEffect.Type.BALL_LARGE;
        if (rand.nextInt(2) == 0) {
            type = FireworkEffect.Type.BURST;
        }
        ItemStack i = new ItemStack(Material.FIREWORK_ROCKET, 1);
        FireworkMeta fm = (FireworkMeta) i.getItemMeta();
        ArrayList<Color> c = new ArrayList<>();
        c.add(Color.RED);
        c.add(Color.RED);
        c.add(Color.RED);
        c.add(Color.ORANGE);
        c.add(Color.ORANGE);
        c.add(Color.ORANGE);
        c.add(Color.BLACK);
        c.add(Color.GRAY);
        FireworkEffect e = FireworkEffect.builder().flicker(true).withColor(c).withFade(c).with(type).trail(true).build();
        fm.addEffect(e);
        fm.setPower(3);
        i.setItemMeta(fm);
        return i;
    }
}
