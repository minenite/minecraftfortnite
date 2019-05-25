// 
// Decompiled by Procyon v0.5.34
// 

package me.matt.pvpgunplus.event.events;

import me.matt.pvpgunplus.event.GunEvent;
import me.matt.pvpgunplus.gun.Gun;
import me.matt.pvpgunplus.gun.GunPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class GunDamageEntityEvent extends GunEvent {

    private final Gun gun;
    private final GunPlayer shooter;
    private final Entity shot;
    private boolean isHeadshot;
    private final double damage;
    private EntityDamageByEntityEvent event;

    public GunDamageEntityEvent(EntityDamageByEntityEvent event, GunPlayer shooter,
                                Gun gun, Entity shot, boolean headshot) {
        this.gun = gun;
        this.shooter = shooter;
        this.shot = shot;
        isHeadshot = headshot;
        damage = gun.getGunDamage();
    }

    public EntityDamageByEntityEvent getEntityDamageEntityEvent() {
        return event;
    }

    public boolean isHeadshot() {
        return isHeadshot;
    }

    public void setHeadshot(boolean b) {
        isHeadshot = b;
    }

    public GunPlayer getShooter() {
        return shooter;
    }

    public Entity getEntityDamaged() {
        return shot;
    }

    public Player getKillerAsPlayer() {
        return shooter.getPlayer();
    }

    public Gun getGun() {
        return gun;
    }

    public double getDamage() {
        return damage;
    }
}
