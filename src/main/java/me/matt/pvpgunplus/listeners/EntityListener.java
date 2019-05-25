// 
// Decompiled by Procyon v0.5.34
// 

package me.matt.pvpgunplus.listeners;

import me.matt.pvpgunplus.event.events.BulletCollideEvent;
import me.matt.pvpgunplus.event.events.GunDamageEntityEvent;
import me.matt.pvpgunplus.event.events.GunKillEntityEvent;
import me.matt.pvpgunplus.gun.Bullet;
import me.matt.pvpgunplus.gun.Gun;
import me.matt.pvpgunplus.gun.GunPlayer;
import net.minenite.minecraftfortnite.MinecraftFortnite;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;

public class EntityListener implements Listener {

    private final MinecraftFortnite plugin;

    public EntityListener(MinecraftFortnite plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onProjectileHit(ProjectileHitEvent event) {
        Projectile check = event.getEntity();
        Bullet bullet = plugin.getGunManager().getBullet(check);
        if (bullet != null) {
            bullet.onHit();
            bullet.setNextTickDestroy();
            Projectile p = event.getEntity();
            Block b = p.getLocation().getBlock();
            Material id = b.getType();
            for (double i = 0.2; i < 4.0; i += 0.2) {
                if (id == Material.AIR) {
                    b = p.getLocation().add(p.getVelocity().normalize().multiply(i)).getBlock();
                    id = b.getType();
                }
            }
            if (id != Material.AIR) {
                p.getLocation().getWorld().playEffect(b.getLocation(), Effect.STEP_SOUND, id);
            }
            BulletCollideEvent evv = new BulletCollideEvent(bullet.getShooter(), bullet.getGun(), b);
            plugin.getServer().getPluginManager().callEvent(evv);
            event.getEntity().remove();
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDeath(EntityDeathEvent event) {
        Entity dead = event.getEntity();
        if (dead.getLastDamageCause() != null) {
            EntityDamageEvent e = dead.getLastDamageCause();
            if (e instanceof EntityDamageByEntityEvent) {
                EntityDamageByEntityEvent ede = (EntityDamageByEntityEvent) e;
                Entity damager = ede.getDamager();
                if (damager instanceof Projectile) {
                    Projectile proj = (Projectile) damager;
                    Bullet bullet = plugin.getGunManager().getBullet(proj);
                    if (bullet != null) {
                        Gun used = bullet.getGun();
                        GunPlayer shooter = bullet.getShooter();
                        GunKillEntityEvent pvpgunkill = new GunKillEntityEvent(shooter, used, dead);
                        plugin.getServer().getPluginManager().callEvent(pvpgunkill);
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.isCancelled()) {
            return;
        }
        Entity damager = event.getDamager();
        if (event.getEntity() instanceof LivingEntity) {
            LivingEntity hurt = (LivingEntity) event.getEntity();
            if (damager instanceof Projectile) {
                Projectile proj = (Projectile) damager;
                Bullet bullet = plugin.getGunManager().getBullet(proj);
                if (bullet != null) {
                    boolean headshot = false;
                    if (isNear(proj.getLocation(), hurt.getEyeLocation(), 0.26) && bullet.getGun().canHeadShot()) {
                        headshot = true;
                    }
                    GunDamageEntityEvent pvpgundmg = new GunDamageEntityEvent(event, bullet.getShooter(), bullet.getGun(), event.getEntity(), headshot);
                    plugin.getServer().getPluginManager().callEvent(pvpgundmg);
                    if (!pvpgundmg.isCancelled()) {
                        double damage = pvpgundmg.getDamage();
                        double mult = 1.0;
                        if (pvpgundmg.isHeadshot()) {
                            plugin.getGunManager().playEffect(Effect.ZOMBIE_DESTROY_DOOR,
                                    hurt.getLocation(), 3);
                            mult = 1.85;
                        }
                        hurt.setLastDamage(0.0);
                        event.setDamage(Math.ceil(damage * mult));
                        int armorPenetration = bullet.getGun().getArmorPenetration();
                        if (armorPenetration > 0) {
                            double health = hurt.getHealth();
                            double newHealth = health - armorPenetration;
                            if (newHealth < 0.0) {
                                newHealth = 0.0;
                            }
                            if (newHealth > 20.0) {
                                newHealth = 20.0;
                            }
                            hurt.setHealth(newHealth);
                        }
                        bullet.getGun().doKnockback(hurt, bullet.getVelocity());
                        bullet.remove();
                    } else {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    private boolean isNear(Location location, Location eyeLocation, double d) {
        return Math.abs(location.getY() - eyeLocation.getY()) <= d;
    }
}
