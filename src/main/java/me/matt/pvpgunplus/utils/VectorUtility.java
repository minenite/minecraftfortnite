// 
// Decompiled by Procyon v0.5.34
// 

package me.matt.pvpgunplus.utils;

import org.bukkit.util.Vector;

public class VectorUtility {

    private static float invSqrt(float x) {
        float xhalf = 0.5f * x;
        int i = Float.floatToIntBits(x);
        i = 1597463007 - (i >> 1);
        x = Float.intBitsToFloat(i);
        x *= 1.5f - xhalf * x * x;
        return x;
    }

    private static float getMagnitudeSquared(Vector vector) {
        return (float) (vector.getX() * vector.getX() + vector.getY() * vector.getY() + vector.getZ() * vector.getZ());
    }

    public static Vector normalizeIn(Vector vector) {
        float invSqrt = invSqrt(getMagnitudeSquared(vector));
        vector.setX(vector.getX() * invSqrt);
        vector.setY(vector.getY() * invSqrt);
        vector.setZ(vector.getZ() * invSqrt);
        return vector;
    }
}
