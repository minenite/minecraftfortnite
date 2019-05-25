// 
// Decompiled by Procyon v0.5.34
// 

package me.matt.pvpgunplus.gun;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

import me.matt.pvpgunplus.utils.Explosion;
import net.minenite.minecraftfortnite.MinecraftFortnite;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class Bullet {

    private final int releaseTime;
    private final Location startLocation;
    private final double maxDistanceSquared;
    private final MinecraftFortnite plugin;
    private int ticks;
    private boolean dead;
    private boolean active;
    private boolean destroyNextTick;
    private boolean released;
    private Entity projectile;
    private Vector velocity;
    private Location lastLocation;
    private GunPlayer shooter;
    private Gun shotFrom;

    public Bullet(GunPlayer owner, Vector vec, Gun gun, MinecraftFortnite plugin) {
        dead = false;
        active = true;
        destroyNextTick = false;
        released = false;
        shotFrom = gun;
        shooter = owner;
        velocity = vec;
        maxDistanceSquared = shotFrom.getMaxDistance() * shotFrom.getMaxDistance();
        if (gun.isThrowable()) {
            ItemStack thrown = new ItemStack(gun.getGunType(), 1);
            projectile = owner.getPlayer().getWorld().dropItem(owner.getPlayer().getEyeLocation(), thrown);
            ((Item) projectile).setPickupDelay(9999999);
            startLocation = projectile.getLocation();
        } else {
            Class<? extends Projectile> mclass = Snowball.class;
            String check = gun.projType.replace(" ", "").replace("_", "");
            if (check.equalsIgnoreCase("egg")) {
                mclass = Egg.class;
            }
            if (check.equalsIgnoreCase("arrow")) {
                mclass = Arrow.class;
            }
            projectile = owner.getPlayer().launchProjectile((Class) mclass);
            ((Projectile) projectile).setShooter(owner.getPlayer());
            startLocation = projectile.getLocation();
        }
        if (shotFrom.getReleaseTime() == -1) {
            releaseTime = 79 + (gun.isThrowable() ? 0 : 1) * 63;
        } else {
            releaseTime = shotFrom.getReleaseTime();
        }
        this.plugin = plugin;
    }

    public void tick() {
        if (!dead) {
            ++ticks;
            if (projectile != null) {
                lastLocation = projectile.getLocation();
                if (ticks > releaseTime) {
                    dead = true;
                    return;
                }
                if (shotFrom.hasSmokeTrail()) {
                    lastLocation.getWorld().playEffect(lastLocation, Effect.SMOKE, 1);
                }
                if (active) {
                    if (lastLocation.getWorld().equals(startLocation.getWorld())) {
                        double dis = lastLocation.distanceSquared(startLocation);
                        if (dis > maxDistanceSquared) {
                            active = false;
                            if (!shotFrom.isThrowable() && !shotFrom.canGoPastMaxDistance()) {
                                velocity.multiply(0.26);
                            }
                        }
                    }
                    projectile.setVelocity(velocity);
                }
            } else {
                dead = true;
            }
            if (ticks > 190) {
                dead = true;
            }
        } else {
            remove();
        }
        if (destroyNextTick) {
            dead = true;
        }
    }

    public Gun getGun() {
        return shotFrom;
    }

    public GunPlayer getShooter() {
        return shooter;
    }

    public Vector getVelocity() {
        return velocity;
    }

    public void remove() {
        dead = true;
        plugin.getGunManager().removeBullet(this);
        projectile.remove();
        onHit();
        destroy();
    }

    public void onHit() {
        if (released) {
            return;
        }
        released = true;
        if (projectile != null) {
            lastLocation = projectile.getLocation();
            if (shotFrom != null) {
                int rad3;
                int rad2 = rad3 = (int) shotFrom.getExplodeRadius();
                if (shotFrom.getFireRadius() > rad3) {
                    rad3 = (int) shotFrom.getFireRadius();
                    int srad = rad3 * rad3;
                    rad2 = 2;
                    for (int i = -rad3; i <= rad3; ++i) {
                        for (int ii = -rad2 / 2; ii <= rad2 / 2; ++ii) {
                            for (int iii = -rad3; iii <= rad3; ++iii) {
                                Location nloc = lastLocation.clone().add((double) i, (double) ii, (double) iii);
                                if (nloc.distanceSquared(lastLocation) <= srad && ThreadLocalRandom.current().nextInt(5) == 1) {
                                    lastLocation.getWorld().playEffect(nloc, Effect.MOBSPAWNER_FLAMES, 2);
                                }
                            }
                        }
                    }
                } else if (rad3 > 0) {
                    int srad = rad3 * rad3;
                    for (int i = -rad3; i <= rad3; ++i) {
                        for (int ii = -rad2 / 2; ii <= rad2 / 2; ++ii) {
                            for (int iii = -rad3; iii <= rad3; ++iii) {
                                Location nloc = lastLocation.clone().add((double) i, (double) ii, (double) iii);
                                if (nloc.distanceSquared(lastLocation) <= srad && ThreadLocalRandom.current().nextInt(10) == 1) {
                                    new Explosion(nloc).explode();
                                }
                            }
                        }
                    }
                    new Explosion(lastLocation).explode();
                }
                explode();
                fireSpread();
                flash();
            }
        }
    }

    public void explode() {
        if (shotFrom.getExplodeRadius() > 0.0) {
            lastLocation.getWorld().createExplosion(lastLocation, 0.0f);
            if (shotFrom.isThrowable()) {
                projectile.teleport(projectile.getLocation().add(0.0, 1.0, 0.0));
            }
            int c = (int) shotFrom.getExplodeRadius();
            ArrayList<Entity> entities = (ArrayList<Entity>) projectile.getNearbyEntities((double) c, (double) c, (double) c);
            for (int i = 0; i < entities.size(); ++i) {
                if (entities.get(i) instanceof LivingEntity && ((LivingEntity) entities.get(i)).hasLineOfSight(projectile)) {
                    double dmg = shotFrom.getExplosionDamage();
                    if (dmg == -1.0) {
                        dmg = shotFrom.getGunDamage();
                    }
                    ((LivingEntity) entities.get(i)).setLastDamage(0.0);
                    ((LivingEntity) entities.get(i)).damage(dmg, shooter.getPlayer());
                    ((LivingEntity) entities.get(i)).setLastDamage(0.0);
                }
            }
        }
    }

    public void fireSpread() {
        if (shotFrom.getFireRadius() > 0.0) {
            lastLocation.getWorld().playSound(lastLocation, Sound.BLOCK_GLASS_BREAK,
                    20.0f,
                    20.0f);
            int c = (int) shotFrom.getFireRadius();
            ArrayList<Entity> entities = (ArrayList<Entity>) projectile.getNearbyEntities((double) c, (double) c, (double) c);
            for (int i = 0; i < entities.size(); ++i) {
                if (entities.get(i) instanceof LivingEntity) {
                    EntityDamageByEntityEvent e = new EntityDamageByEntityEvent(shooter.getPlayer(), entities.get(i), EntityDamageEvent.DamageCause.CUSTOM, 0);
                    Bukkit.getServer().getPluginManager().callEvent(e);
                    if (!e.isCancelled() && ((LivingEntity) entities.get(i)).hasLineOfSight(projectile)) {
                        entities.get(i).setFireTicks(140);
                        ((LivingEntity) entities.get(i)).setLastDamage(0.0);
                        ((LivingEntity) entities.get(i)).damage(1.0, shooter.getPlayer());
                    }
                }
            }
        }
    }

    public void flash() {
        if (shotFrom.getFlashRadius() > 0.0) {
            lastLocation.getWorld().playSound(lastLocation, Sound.ENTITY_GENERIC_SPLASH, 20.0f,
                    20.0f);
            int c = (int) shotFrom.getFlashRadius();
            ArrayList<Entity> entities = (ArrayList<Entity>) projectile.getNearbyEntities((double) c, (double) c, (double) c);
            for (int i = 0; i < entities.size(); ++i) {
                if (entities.get(i) instanceof LivingEntity) {
                    EntityDamageByEntityEvent e = new EntityDamageByEntityEvent(shooter.getPlayer(), entities.get(i), EntityDamageEvent.DamageCause.CUSTOM, 0);
                    Bukkit.getServer().getPluginManager().callEvent(e);
                    if (!e.isCancelled() && ((LivingEntity) entities.get(i)).hasLineOfSight(projectile)) {
                        ((LivingEntity) entities.get(i)).addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 140, 1));
                    }
                }
            }
        }
    }

    public void destroy() {
        projectile = null;
        velocity = null;
        shotFrom = null;
        shooter = null;
    }

    public Entity getProjectile() {
        return projectile;
    }

    public void setNextTickDestroy() {
        destroyNextTick = true;
    }
}
