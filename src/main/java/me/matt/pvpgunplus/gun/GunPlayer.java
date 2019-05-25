// 
// Decompiled by Procyon v0.5.34
// 

package me.matt.pvpgunplus.gun;

import java.util.ArrayList;
import java.util.List;

import net.minenite.minecraftfortnite.MinecraftFortnite;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class GunPlayer {

    private final List<Gun> guns;
    private final MinecraftFortnite plugin;
    private final boolean enabled;
    private int ticks;
    private Player controller;
    private ItemStack lastHeldItem;
    private Gun currentlyFiring;
    private Gun lastGun;
    private int cooldown;

    public GunPlayer(MinecraftFortnite plugin, Player player) {
        enabled = true;
        controller = player;
        guns = plugin.getGunManager().getLoadedGuns();
        for (Gun gun : guns) {
            gun.owner = this;
        }
        cooldown = 0;
        this.plugin = plugin;
    }

    public boolean isAimedIn() {
        return controller != null && controller.isOnline() && controller.hasPotionEffect(PotionEffectType.SLOW);
    }

    public boolean onClick(String clickType) {
        if (!enabled) {
            return false;
        }
        Gun holding = null;
        ItemStack hand = controller.getInventory().getItemInMainHand();
        ArrayList<Gun> tempgun = getGunsByType(hand);
        ArrayList<Gun> canFire = new ArrayList<>();
        for (Gun gun : tempgun) {
            if (controller.hasPermission(gun.node) || !gun.needsPermission) {
                canFire.add(gun);
            }
        }
        if (tempgun.size() > canFire.size() && canFire.size() == 0) {
            if (tempgun.get(0).permissionMessage != null && tempgun.get(0).permissionMessage.length() > 0) {
                controller.sendMessage(tempgun.get(0).permissionMessage);
            }
            return false;
        }
        tempgun.clear();
        for (Gun check : canFire) {
            if (check.getGunType() == hand.getType() || check.ignoreItemData) {
                holding = check;
            }
        }
        canFire.clear();
        if (holding != null) {
            if ((holding.canClickRight || holding.canAimRight()) && clickType.equals("right")) {
                if (!holding.canAimRight()) {
                    holding.click();
                    if (currentlyFiring == null) {
                        fireGun(holding);
                    }
                } else {
                    checkAim();
                }
            } else if ((holding.canClickLeft || holding.canAimLeft()) && clickType.equals("left")) {
                if (!holding.canAimLeft()) {
                    if (currentlyFiring == null) {
                        fireGun(holding);
                    }
                } else {
                    checkAim();
                }
            }
        }
        return true;
    }

    private void checkAim() {
        if (isAimedIn()) {
            controller.removePotionEffect(PotionEffectType.SLOW);
        } else {
            controller.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 12000, 4));
        }
    }

    private void fireGun(Gun gun) {
        if (controller.hasPermission(gun.node) || !gun.needsPermission) {
            if (gun.timer <= 0) {
                currentlyFiring = gun;
                gun.firing = true;
            }
        } else if (gun.permissionMessage != null && gun.permissionMessage.length() > 0) {
            controller.sendMessage(gun.permissionMessage);
        }
    }

    public void tick() {
        ++ticks;
        --cooldown;
        if (controller != null) {
            ItemStack hand = controller.getInventory().getItemInMainHand();
            lastHeldItem = hand;
            if (ticks % 10 == 0) {
                Gun g = plugin.getGunManager().getGun(hand.getType());
                if (g == null) {
                    controller.removePotionEffect(PotionEffectType.SLOW);
                }
            }
            for (int i = guns.size() - 1; i >= 0; --i) {
                Gun g2 = guns.get(i);
                if (g2 != null) {
                    g2.tick();
                    if (controller.isDead()) {
                        g2.finishReloading();
                    }
                    if (g2.getGunType() == hand.getType() && isAimedIn() && !g2.canAimLeft() && !g2.canAimRight()) {
                        controller.removePotionEffect(PotionEffectType.SLOW);
                    }
                    if (currentlyFiring != null && g2.timer <= 0 && currentlyFiring.equals(g2)) {
                        currentlyFiring = null;
                    }
                }
            }
        }
        renameGuns(controller);
    }

    private void renameGuns(Player p) {
        Inventory inv = p.getInventory();
        ItemStack[] items = inv.getContents();
        for (ItemStack item : items) {
            if (item != null) {
                String name = getGunName(item);
                if (name != null && name.length() > 0) {
                    setName(item, name);
                }
            }
        }
    }

    private ArrayList<Gun> getGunsByType(ItemStack item) {
        ArrayList<Gun> ret = new ArrayList<>();
        for (Gun gun : guns) {
            if (gun.getGunType() == item.getType()) {
                ret.add(gun);
            }
        }
        return ret;
    }

    private String getGunName(ItemStack item) {
        String ret = "";
        ArrayList<Gun> tempgun = getGunsByType(item);
        int amtGun = tempgun.size();
        if (amtGun > 0) {
            for (Gun gun : tempgun) {
                if (controller.hasPermission(gun.node) || !gun.needsPermission) {
                    if (gun.getGunType() != null && gun.getGunType() == item.getType()) {
                        return getGunName(gun);
                    }
                }
            }
        }
        return ret;
    }

    private String getGunName(Gun current) {
        String add = "";
        String refresh;
        if (current.hasClip) {
            int leftInClip;
            int maxInClip = current.maxClipSize;
            int currentAmmo = 0;
            for (ItemStack item : controller.getInventory().getContents()) {
                if (item != null && item.getType() == current.getAmmoType()) {
                    currentAmmo = item.getAmount();
                }
            }
            int ammoLeft = currentAmmo - maxInClip + current.roundsFired;
            if (ammoLeft < 0) {
                ammoLeft = 0;
            }
            leftInClip = currentAmmo - ammoLeft;
            // todo: fix message
            add = ChatColor.YELLOW + "    �" + leftInClip + " \u2590 " + ammoLeft + "�";
            if (current.reloading) {
                int reloadSize = 4;
                double reloadFrac = (current.getReloadTime() - current.gunReloadTimer) / (double) current.getReloadTime();
                int amt = (int) (reloadFrac * reloadSize);
                StringBuilder refreshBuilder = new StringBuilder();
                for (int ii = 0; ii < amt; ++ii) {
                    refreshBuilder.append("\u25aa");
                }
                for (int ii = 0; ii < reloadSize - amt; ++ii) {
                    refreshBuilder.append("\u25ab");
                }
                refresh = refreshBuilder.toString();
                add = ChatColor.RED + "    " + new StringBuffer(refresh).reverse() + "RELOADING" + refresh;
            }
        }
        String name = current.getName();
        return name + add;
    }

    private void setName(ItemStack item, String name) {
        ItemMeta im = item.getItemMeta();
        im.setDisplayName(name);
        item.setItemMeta(im);
    }

    public Player getPlayer() {
        return controller;
    }

    public void unload() {
        controller = null;
        currentlyFiring = null;
        for (Gun gun : guns) {
            gun.clear();
        }
    }

    public void reloadAllGuns() {
        for (int i = guns.size() - 1; i >= 0; --i) {
            Gun g = guns.get(i);
            if (g != null) {
                g.reloadGun();
                g.finishReloading();
            }
        }
    }

    boolean checkAmmo(Gun gun, int amount) {
        ItemStack item = new ItemStack(gun.getAmmoType());
        return controller.getInventory().containsAtLeast(item, amount);
    }

    void removeAmmo(Gun gun, int amount) {
        if (amount == 0) {
            return;
        }
        ItemStack item = new ItemStack(gun.getAmmoType(), amount);
        controller.getInventory().removeItem(item);
    }

    public ItemStack getLastItemHeld() {
        return lastHeldItem;
    }

    public Gun getGun(Material typeId) {
        for (int i = guns.size() - 1; i >= 0; --i) {
            Gun check = guns.get(i);
            if (check.getGunType() == typeId) {
                return check;
            }
        }
        return null;
    }

    int getWait(Gun gun) {
        if (lastGun == gun) {
            return 0;
        }
        return (cooldown > 0) ? cooldown : 0;
    }

    void shot(Gun gun) {
        if (lastGun != null && lastGun != gun) {
            cooldown = 9;
            lastGun.changedGun();
        }
        lastGun = gun;
    }
}
