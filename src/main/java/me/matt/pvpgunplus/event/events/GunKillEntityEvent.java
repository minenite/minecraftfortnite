// 
// Decompiled by Procyon v0.5.34
// 

package me.matt.pvpgunplus.event.events;

import me.matt.pvpgunplus.event.GunEvent;
import me.matt.pvpgunplus.gun.Gun;
import me.matt.pvpgunplus.gun.GunPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class GunKillEntityEvent extends GunEvent {

    private final Gun gun;
    private final GunPlayer shooter;
    private final Entity shot;

    public GunKillEntityEvent(GunPlayer shooter, Gun gun, Entity killed) {
        this.gun = gun;
        this.shooter = shooter;
        shot = killed;
    }

    public GunPlayer getKiller() {
        return shooter;
    }

    public Player getKillerAsPlayer() {
        return shooter.getPlayer();
    }

    public Entity getKilled() {
        return shot;
    }

    public Gun getGun() {
        return gun;
    }
}
