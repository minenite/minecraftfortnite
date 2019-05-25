// 
// Decompiled by Procyon v0.5.34
// 

package me.matt.pvpgunplus.event.events;

import me.matt.pvpgunplus.event.GunEvent;
import me.matt.pvpgunplus.gun.Gun;
import me.matt.pvpgunplus.gun.GunPlayer;
import org.bukkit.entity.Player;

public class FireGunEvent extends GunEvent {

    private final Gun gun;
    private final GunPlayer shooter;
    private int amountAmmoNeeded;
    private double accuracy;

    public FireGunEvent(GunPlayer shooter, Gun gun) {
        this.gun = gun;
        this.shooter = shooter;
        amountAmmoNeeded = gun.getAmmoAmtNeeded();
        accuracy = gun.getAccuracy();
        if (shooter.getPlayer().isSneaking() && gun.getAccuracy_crouched() > -1.0) {
            accuracy = gun.getAccuracy_crouched();
        }
        if (shooter.isAimedIn() && gun.getAccuracy_aimed() > -1.0) {
            accuracy = gun.getAccuracy_aimed();
        }
    }

    public GunEvent setAmountAmmoNeeded(int i) {
        amountAmmoNeeded = i;
        return this;
    }

    public int getAmountAmmoNeeded() {
        return amountAmmoNeeded;
    }

    public double getGunAccuracy() {
        return accuracy;
    }

    public void setGunAccuracy(double d) {
        accuracy = d;
    }

    public Gun getGun() {
        return gun;
    }

    public GunPlayer getShooter() {
        return shooter;
    }

    public Player getShooterAsPlayer() {
        return shooter.getPlayer();
    }
}
