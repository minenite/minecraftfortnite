// 
// Decompiled by Procyon v0.5.34
// 

package me.matt.pvpgunplus.gun;

import java.util.ArrayList;
import java.util.Random;

import me.matt.pvpgunplus.event.events.FireGunEvent;
import me.matt.pvpgunplus.utils.VectorUtility;
import net.minenite.minecraftfortnite.MinecraftFortnite;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class Gun {

    private final int bulletDelay;
    private final MinecraftFortnite plugin;
    public boolean hasClip;
    public boolean reloadGunOnDrop;
    public boolean changed;
    public String node;
    public String reloadType;
    String projType;
    String permissionMessage;
    boolean needsPermission;
    boolean canClickRight;
    boolean canClickLeft;
    boolean ignoreItemData;
    int maxClipSize;
    int bulletDelayTime;
    int roundsFired;
    int gunReloadTimer;
    int timer;
    boolean firing;
    boolean reloading;
    GunPlayer owner;
    private int bulletsPerClick;
    private ArrayList<String> gunSound;
    private String outOfAmmoMessage;
    private int lastClick;
    private int lastFired;
    private int ticks;
    private int heldDownClicks;
    private boolean holdingFire;
    private boolean canHeadshot;
    private boolean isThrowable;
    private boolean hasSmokeTrail;
    private boolean localGunSound;
    private boolean canAimLeft;
    private boolean canAimRight;
    private boolean canGoPastMaxDistance;
    private Material gunType;
    private Material ammoType;
    private int ammoAmtNeeded;
    private double gunDamage;
    private int explosionDamage;
    private int roundsPerBurst;
    private int reloadTime;
    private int maxDistance;
    private int bulletsShot;
    private int armorPenetration;
    private int releaseTime;
    private double bulletSpeed;
    private double accuracy;
    private double accuracy_aimed;
    private double accuracy_crouched;
    private double explodeRadius;
    private double fireRadius;
    private double flashRadius;
    private double knockback;
    private double recoil;
    private double gunVolume;
    private String gunName;
    private String fileName;

    public Gun(String name, MinecraftFortnite plugin) {
        explosionDamage = -1;
        bulletDelay = 2;
        releaseTime = -1;
        accuracy_aimed = -1.0;
        accuracy_crouched = -1.0;
        gunVolume = 1.0;
        projType = "";
        gunSound = new ArrayList<>();
        outOfAmmoMessage = "";
        permissionMessage = "";
        hasClip = true;
        ignoreItemData = false;
        reloadGunOnDrop = true;
        maxClipSize = 30;
        bulletDelayTime = 10;
        firing = false;
        changed = false;
        reloadType = "NORMAL";
        gunName = name;
        fileName = name;
        outOfAmmoMessage = "Out of ammo!";
        this.plugin = plugin;
    }

    public void shoot() {
        if (owner != null && owner.getPlayer().isOnline() && !reloading) {
            FireGunEvent event = new FireGunEvent(owner, this);
            plugin.getServer().getPluginManager().callEvent(event);
            if (!event.isCancelled()) {
                owner.shot(this);
                if ((owner.checkAmmo(this, event.getAmountAmmoNeeded()) && event.getAmountAmmoNeeded() > 0) || event.getAmountAmmoNeeded() == 0) {
                    owner.removeAmmo(this, event.getAmountAmmoNeeded());
                    if (roundsFired >= maxClipSize && hasClip) {
                        reloadGun();
                        return;
                    }
                    doRecoil(owner.getPlayer());
                    changed = true;
                    ++roundsFired;
                    for (String s : gunSound) {
                        Sound sound = plugin.getGunManager().getSound(s);
                        if (sound != null) {
                            if (localGunSound) {
                                owner.getPlayer().playSound(owner.getPlayer().getLocation(), sound, (float) gunVolume, 2.0f);
                            } else {
                                owner.getPlayer().getWorld().playSound(owner.getPlayer().getLocation(), sound, (float) gunVolume, 2.0f);
                            }
                        }
                    }
                    for (int i = 0; i < bulletsPerClick; ++i) {
                        int acc = (int) (event.getGunAccuracy() * 1000.0);
                        if (acc <= 0) {
                            acc = 1;
                        }
                        Location ploc = owner.getPlayer().getLocation();
                        Random rand = new Random();
                        double dir = -ploc.getYaw() - 90.0f;
                        double pitch = -ploc.getPitch();
                        double xwep = (rand.nextInt(acc) - rand.nextInt(acc) + 0.5) / 1000.0;
                        double ywep = (rand.nextInt(acc) - rand.nextInt(acc) + 0.5) / 1000.0;
                        double zwep = (rand.nextInt(acc) - rand.nextInt(acc) + 0.5) / 1000.0;
                        double xd = Math.cos(Math.toRadians(dir)) * Math.cos(Math.toRadians(pitch)) + xwep;
                        double yd = Math.sin(Math.toRadians(pitch)) + ywep;
                        double zd = -Math.sin(Math.toRadians(dir)) * Math.cos(Math.toRadians(pitch)) + zwep;
                        Vector vec = new Vector(xd, yd, zd);
                        vec.multiply(bulletSpeed);
                        Bullet bullet = new Bullet(owner, vec, this, plugin);
                        plugin.getGunManager().addBullet(bullet);
                    }
                    if (roundsFired >= maxClipSize && hasClip) {
                        reloadGun();
                    }
                } else {
                    owner.getPlayer().playSound(owner.getPlayer().getLocation(),
                            Sound.ENTITY_ITEM_BREAK, 20.0f, 20.0f);
                    owner.getPlayer().sendMessage(outOfAmmoMessage);
                    finishShooting();
                }
            }
        }
    }

    public void tick() {
        ++ticks;
        ++lastFired;
        --timer;
        --gunReloadTimer;
        if (gunReloadTimer <= 1) {
            if (reloading) {
                finishReloading();
            }
            reloading = false;
        }
        gunSounds();
        if (ticks - lastClick > 4) {
            --heldDownClicks;
            if (heldDownClicks <= 0) {
                heldDownClicks = 0;
                holdingFire = false;
            }
        }
        if ((holdingFire && timer <= 0) || (firing && !reloading)) {
            if (owner.getWait(this) == 0) {
                if (roundsPerBurst > 1) {
                    if (ticks % bulletDelay == 0) {
                        ++bulletsShot;
                        if (bulletsShot <= roundsPerBurst) {
                            shoot();
                        } else {
                            finishShooting();
                        }
                    }
                } else {
                    shoot();
                    finishShooting();
                }
            } else if (owner.getWait(this) >= 5) {
                holdingFire = false;
                heldDownClicks = 1;
            }
        }
        if (reloading) {
            firing = false;
        }
    }

    public void click() {
        ++heldDownClicks;
        if (heldDownClicks >= 2) {
            holdingFire = true;
            heldDownClicks = 2;
        }
        lastClick = ticks;
    }

    public void changedGun() {
    }

    public Gun copy() {
        Gun g = new Gun(gunName, plugin);
        g.gunName = gunName;
        g.gunType = gunType;
        g.ammoAmtNeeded = ammoAmtNeeded;
        g.ammoType = ammoType;
        g.roundsPerBurst = roundsPerBurst;
        g.bulletsPerClick = bulletsPerClick;
        g.bulletSpeed = bulletSpeed;
        g.accuracy = accuracy;
        g.accuracy_aimed = accuracy_aimed;
        g.accuracy_crouched = accuracy_crouched;
        g.maxDistance = maxDistance;
        g.gunVolume = gunVolume;
        g.gunDamage = gunDamage;
        g.explodeRadius = explodeRadius;
        g.fireRadius = fireRadius;
        g.flashRadius = flashRadius;
        g.canHeadshot = canHeadshot;
        g.reloadTime = reloadTime;
        g.canAimLeft = canAimLeft;
        g.canAimRight = canAimRight;
        g.canClickLeft = canClickLeft;
        g.canClickRight = canClickRight;
        g.hasSmokeTrail = hasSmokeTrail;
        g.armorPenetration = armorPenetration;
        g.isThrowable = isThrowable;
        g.ignoreItemData = ignoreItemData;
        g.outOfAmmoMessage = outOfAmmoMessage;
        g.projType = projType;
        g.needsPermission = needsPermission;
        g.node = node;
        g.gunSound = gunSound;
        g.bulletDelayTime = bulletDelayTime;
        g.hasClip = hasClip;
        g.maxClipSize = maxClipSize;
        g.reloadGunOnDrop = reloadGunOnDrop;
        g.localGunSound = localGunSound;
        g.fileName = fileName;
        g.explosionDamage = explosionDamage;
        g.recoil = recoil;
        g.knockback = knockback;
        g.reloadType = reloadType;
        g.releaseTime = releaseTime;
        g.canGoPastMaxDistance = canGoPastMaxDistance;
        g.permissionMessage = permissionMessage;
        return g;
    }

    public void reloadGun() {
        reloading = true;
        gunReloadTimer = reloadTime;
    }

    private void gunSounds() {
        if (reloading) {
            int amtReload = reloadTime - gunReloadTimer;
            if (reloadType.equalsIgnoreCase("bolt")) {
                if (amtReload == 6) {
                    owner.getPlayer().playSound(owner.getPlayer().getLocation(),
                            Sound.BLOCK_WOODEN_DOOR_OPEN, 2.0f, 1.5f);
                }
                if (amtReload == reloadTime - 4) {
                    owner.getPlayer().playSound(owner.getPlayer().getLocation(),
                            Sound.BLOCK_WOODEN_DOOR_CLOSE, 1.0f, 1.5f);
                }
            } else if (reloadType.equalsIgnoreCase("pump") || reloadType.equals("INDIVIDUAL_BULLET")) {
                int rep = (reloadTime - 10) / maxClipSize;
                if (amtReload >= 5 && amtReload <= reloadTime - 5 && amtReload % rep == 0) {
                    owner.getPlayer().playSound(owner.getPlayer().getLocation(),
                            Sound.BLOCK_NOTE_BLOCK_HAT, 1.0f, 1.0f);
                    owner.getPlayer().playSound(owner.getPlayer().getLocation(),
                            Sound.BLOCK_NOTE_BLOCK_SNARE, 1.0f, 2.0f);
                }
                if (amtReload == reloadTime - 3) {
                    owner.getPlayer().playSound(owner.getPlayer().getLocation(),
                            Sound.BLOCK_PISTON_EXTEND, 1.0f, 2.0f);
                }
                if (amtReload == reloadTime - 1) {
                    owner.getPlayer().playSound(owner.getPlayer().getLocation(),
                            Sound.BLOCK_PISTON_CONTRACT, 1.0f, 2.0f);
                }
            } else {
                if (amtReload == 6) {
                    owner.getPlayer().playSound(owner.getPlayer().getLocation(),
                            Sound.BLOCK_FIRE_AMBIENT, 2.0f, 2.0f);
                    owner.getPlayer().playSound(owner.getPlayer().getLocation(),
                            Sound.BLOCK_WOODEN_DOOR_OPEN, 1.0f, 2.0f);
                }
                if (amtReload == reloadTime / 2) {
                    owner.getPlayer().playSound(owner.getPlayer().getLocation(), Sound.BLOCK_PISTON_CONTRACT,
                            0.33f, 2.0f);
                }
                if (amtReload == reloadTime - 4) {
                    owner.getPlayer().playSound(owner.getPlayer().getLocation(), Sound.BLOCK_FIRE_AMBIENT, 2.0f, 2.0f);
                    owner.getPlayer().playSound(owner.getPlayer().getLocation(),
                            Sound.BLOCK_WOODEN_DOOR_CLOSE, 1.0f, 2.0f);
                }
            }
        } else {
            if (reloadType.equalsIgnoreCase("pump")) {
                if (timer == 8) {
                    owner.getPlayer().playSound(owner.getPlayer().getLocation(),
                            Sound.BLOCK_PISTON_EXTEND, 1.0f, 2.0f);
                }
                if (timer == 6) {
                    owner.getPlayer().playSound(owner.getPlayer().getLocation(),
                            Sound.BLOCK_PISTON_CONTRACT, 1.0f, 2.0f);
                }
            }
            if (reloadType.equalsIgnoreCase("bolt")) {
                if (timer == bulletDelayTime - 4) {
                    owner.getPlayer().playSound(owner.getPlayer().getLocation(),
                            Sound.BLOCK_WOODEN_DOOR_OPEN, 2.0f, 1.25f);
                }
                if (timer == 6) {
                    owner.getPlayer().playSound(owner.getPlayer().getLocation(),
                            Sound.BLOCK_WOODEN_DOOR_CLOSE, 1.0f, 1.25f);
                }
            }
        }
    }

    private void doRecoil(Player player) {
        if (recoil != 0.0) {
            Location ploc = player.getLocation();
            double dir = -ploc.getYaw() - 90.0f;
            double pitch = -ploc.getPitch() - 180.0f;
            double xd = Math.cos(Math.toRadians(dir)) * Math.cos(Math.toRadians(pitch));
            double yd = Math.sin(Math.toRadians(pitch));
            double zd = -Math.sin(Math.toRadians(dir)) * Math.cos(Math.toRadians(pitch));
            Vector vec = new Vector(xd, yd, zd);
            vec.multiply(recoil / 2.0).setY(0);
            player.setVelocity(player.getVelocity().add(vec));
        }
    }

    public void doKnockback(LivingEntity entity, Vector speed) {
        if (knockback > 0.0) {
            entity.setVelocity(VectorUtility.normalizeIn(speed).setY(0.8).multiply(knockback / 4.0));
        }
    }

    public void finishReloading() {
        bulletsShot = 0;
        roundsFired = 0;
        changed = false;
        gunReloadTimer = 0;
    }

    public Material getGunType() {
        return gunType;
    }

    public void setGunType(String val) {
        gunType = Material.getMaterial(val);
    }

    private void finishShooting() {
        bulletsShot = 0;
        timer = bulletDelayTime;
        firing = false;
    }

    public String getName() {
        return gunName;
    }

    public void setName(String val) {
        val = ChatColor.translateAlternateColorCodes('&', val);
        gunName = val;
    }

    public Material getAmmoType() {
        return ammoType;
    }

    public void setAmmoType(String val) {
        ammoType = Material.getMaterial(val);
    }

    public int getAmmoAmtNeeded() {
        return ammoAmtNeeded;
    }

    public double getExplodeRadius() {
        return explodeRadius;
    }

    public void setExplodeRadius(double parseDouble) {
        explodeRadius = parseDouble;
    }

    public double getFireRadius() {
        return fireRadius;
    }

    public void setFireRadius(double parseDouble) {
        fireRadius = parseDouble;
    }

    public boolean isThrowable() {
        return isThrowable;
    }

    public void setAmmoAmountNeeded(int parseInt) {
        ammoAmtNeeded = parseInt;
    }

    public void setRoundsPerBurst(int parseInt) {
        roundsPerBurst = parseInt;
    }

    public void setBulletsPerClick(int parseInt) {
        bulletsPerClick = parseInt;
    }

    public void setBulletSpeed(double parseDouble) {
        bulletSpeed = parseDouble;
    }

    public void setAccuracyAimed(double parseDouble) {
        accuracy_aimed = parseDouble;
    }

    public void setAccuracyCrouched(double parseDouble) {
        accuracy_crouched = parseDouble;
    }

    public void setCanHeadshot(boolean parseBoolean) {
        canHeadshot = parseBoolean;
    }

    public void setCanClickLeft(boolean parseBoolean) {
        canClickLeft = parseBoolean;
    }

    public void setCanClickRight(boolean parseBoolean) {
        canClickRight = parseBoolean;
    }

    public void clear() {
        owner = null;
    }

    public int getReloadTime() {
        return reloadTime;
    }

    public void setReloadTime(int parseInt) {
        reloadTime = parseInt;
    }

    public double getGunDamage() {
        return gunDamage;
    }

    public void setGunDamage(Double parseInt) {
        gunDamage = parseInt;
    }

    public double getMaxDistance() {
        return maxDistance;
    }

    public void setMaxDistance(int i) {
        maxDistance = i;
    }

    public boolean canAimLeft() {
        return canAimLeft;
    }

    public boolean canAimRight() {
        return canAimRight;
    }

    public void setCanAimLeft(boolean parseBoolean) {
        canAimLeft = parseBoolean;
    }

    public void setCanAimRight(boolean parseBoolean) {
        canAimRight = parseBoolean;
    }

    public void setOutOfAmmoMessage(String val) {
        val = ChatColor.translateAlternateColorCodes('&', val);
        outOfAmmoMessage = val;
    }

    public void setPermissionMessage(String val) {
        val = ChatColor.translateAlternateColorCodes('&', val);
        permissionMessage = val;
    }

    public double getFlashRadius() {
        return flashRadius;
    }

    public void setFlashRadius(double parseDouble) {
        flashRadius = parseDouble;
    }

    public void setIsThrowable(boolean b) {
        isThrowable = b;
    }

    public boolean canHeadShot() {
        return canHeadshot;
    }

    public boolean hasSmokeTrail() {
        return hasSmokeTrail;
    }

    public void setSmokeTrail(boolean b) {
        hasSmokeTrail = b;
    }

    public boolean isLocalGunSound() {
        return localGunSound;
    }

    public void setLocalGunSound(boolean b) {
        localGunSound = b;
    }

    public int getArmorPenetration() {
        return armorPenetration;
    }

    public void setArmorPenetration(int parseInt) {
        armorPenetration = parseInt;
    }

    public int getExplosionDamage() {
        return explosionDamage;
    }

    public void setExplosionDamage(int i) {
        explosionDamage = i;
    }

    public String getFilename() {
        return fileName;
    }

    public void setFilename(String string) {
        fileName = string;
    }

    public double getRecoil() {
        return recoil;
    }

    public void setRecoil(double d) {
        recoil = d;
    }

    public double getKnockback() {
        return knockback;
    }

    public void setKnockback(double d) {
        knockback = d;
    }

    public void addGunSounds(String val) {
        String[] sounds = val.split(",");
        for (int i = 0; i < sounds.length; ++i) {
            gunSound.add(sounds[i]);
        }
    }

    public int getReleaseTime() {
        return releaseTime;
    }

    public void setReleaseTime(int v) {
        releaseTime = v;
    }

    public void setCanGoPastMaxDistance(boolean parseBoolean) {
        canGoPastMaxDistance = parseBoolean;
    }

    public boolean canGoPastMaxDistance() {
        return canGoPastMaxDistance;
    }

    public double getGunVolume() {
        return gunVolume;
    }

    public void setGunVolume(double parseDouble) {
        gunVolume = parseDouble;
    }

    public double getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(double parseDouble) {
        accuracy = parseDouble;
    }

    public double getAccuracy_aimed() {
        return accuracy_aimed;
    }

    public double getAccuracy_crouched() {
        return accuracy_crouched;
    }
}
