// 
// Decompiled by Procyon v0.5.34
// 

package me.matt.pvpgunplus.gun;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import net.minenite.minecraftfortnite.MinecraftFortnite;

public class WeaponReader {

    public MinecraftFortnite plugin;
    public boolean loaded;
    public Gun ret;
    private final File file;

    public WeaponReader(MinecraftFortnite plugin, File file, String string) {
        loaded = false;
        this.plugin = plugin;
        this.file = file;
        (ret = new Gun(file.getName(), plugin)).setFilename(file.getName().toLowerCase());
        load();
    }

    private void computeData(String str) {
        try {
            if (str.indexOf("=") > 0) {
                String var = str.substring(0, str.indexOf("=")).toLowerCase();
                String val = str.substring(str.indexOf("=") + 1);
                if (var.equals("gunname")) {
                    ret.setName(val);
                }
                if (var.equals("guntype")) {
                    ret.setGunType(val);
                }
                if (var.equals("ammoamtneeded")) {
                    ret.setAmmoAmountNeeded(Integer.parseInt(val));
                }
                if (var.equals("reloadtime")) {
                    ret.setReloadTime(Integer.parseInt(val));
                }
                if (var.equals("gundamage")) {
                    ret.setGunDamage(Double.parseDouble(val));
                }
                if (var.equals("armorpenetration")) {
                    ret.setArmorPenetration(Integer.parseInt(val));
                }
                if (var.equals("ammotype")) {
                    ret.setAmmoType(val);
                }
                if (var.equals("roundsperburst")) {
                    ret.setRoundsPerBurst(Integer.parseInt(val));
                }
                if (var.equals("maxdistance")) {
                    ret.setMaxDistance(Integer.parseInt(val));
                }
                if (var.equals("bulletsperclick")) {
                    ret.setBulletsPerClick(Integer.parseInt(val));
                }
                if (var.equals("bulletspeed")) {
                    ret.setBulletSpeed(Double.parseDouble(val));
                }
                if (var.equals("accuracy")) {
                    ret.setAccuracy(Double.parseDouble(val));
                }
                if (var.equals("accuracy_aimed")) {
                    ret.setAccuracyAimed(Double.parseDouble(val));
                }
                if (var.equals("accuracy_crouched")) {
                    ret.setAccuracyCrouched(Double.parseDouble(val));
                }
                if (var.equals("exploderadius")) {
                    ret.setExplodeRadius(Double.parseDouble(val));
                }
                if (var.equals("gunvolume")) {
                    ret.setGunVolume(Double.parseDouble(val));
                }
                if (var.equals("fireradius")) {
                    ret.setFireRadius(Double.parseDouble(val));
                }
                if (var.equals("flashradius")) {
                    ret.setFlashRadius(Double.parseDouble(val));
                }
                if (var.equals("canheadshot")) {
                    ret.setCanHeadshot(Boolean.parseBoolean(val));
                }
                if (var.equals("canshootleft")) {
                    ret.setCanClickLeft(Boolean.parseBoolean(val));
                }
                if (var.equals("canshootright")) {
                    ret.setCanClickRight(Boolean.parseBoolean(val));
                }
                if (var.equals("canclickleft")) {
                    ret.setCanClickLeft(Boolean.parseBoolean(val));
                }
                if (var.equals("canclickright")) {
                    ret.setCanClickRight(Boolean.parseBoolean(val));
                }
                if (var.equals("knockback")) {
                    ret.setKnockback(Double.parseDouble(val));
                }
                if (var.equals("recoil")) {
                    ret.setRecoil(Double.parseDouble(val));
                }
                if (var.equals("canaim")) {
                    ret.setCanAimLeft(Boolean.parseBoolean(val));
                }
                if (var.equals("canaimleft")) {
                    ret.setCanAimLeft(Boolean.parseBoolean(val));
                }
                if (var.equals("canaimright")) {
                    ret.setCanAimRight(Boolean.parseBoolean(val));
                }
                if (var.equals("outofammomessage")) {
                    ret.setOutOfAmmoMessage(val);
                }
                if (var.equals("permissionmessage")) {
                    ret.setPermissionMessage(val);
                }
                if (var.equals("bullettype")) {
                    ret.projType = val;
                }
                if (var.equals("needspermission")) {
                    ret.needsPermission = Boolean.parseBoolean(val);
                }
                if (var.equals("hassmoketrail")) {
                    ret.setSmokeTrail(Boolean.parseBoolean(val));
                }
                if (var.equals("gunsound")) {
                    ret.addGunSounds(val);
                }
                if (var.equals("maxclipsize")) {
                    ret.maxClipSize = Integer.parseInt(val);
                }
                if (var.equals("hasclip")) {
                    ret.hasClip = Boolean.parseBoolean(val);
                }
                if (var.equals("reloadgunondrop")) {
                    ret.reloadGunOnDrop = Boolean.parseBoolean(val);
                }
                if (var.equals("localgunsound")) {
                    ret.setLocalGunSound(Boolean.parseBoolean(val));
                }
                if (var.equalsIgnoreCase("canGoPastMaxDistance")) {
                    ret.setCanGoPastMaxDistance(Boolean.parseBoolean(val));
                }
                if (var.equalsIgnoreCase("ignoreitemdata")) {
                    ret.ignoreItemData = Boolean.parseBoolean(val);
                }
                if (var.equals("bulletdelaytime")) {
                    ret.bulletDelayTime = Integer.parseInt(val);
                }
                if (var.equals("explosiondamage")) {
                    ret.setExplosionDamage(Integer.parseInt(val));
                }
                if (var.equals("timeuntilrelease")) {
                    ret.setReleaseTime(Integer.parseInt(val));
                }
                if (var.equals("reloadtype")) {
                    ret.reloadType = val;
                }
            }
        } catch (Exception e) {
            loaded = false;
        }
    }

    private void load() {
        loaded = true;
        List<String> file = new ArrayList<>();
        try {
            FileInputStream fstream = new FileInputStream(this.file.getAbsolutePath());
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String strLine;
            while ((strLine = br.readLine()) != null) {
                file.add(strLine);
            }
            br.close();
            in.close();
            fstream.close();
        } catch (IOException e) {
            plugin.getLogger().severe("Error: " + e.getMessage());
        }
        for (String s : file) {
            computeData(s);
        }
    }
}
