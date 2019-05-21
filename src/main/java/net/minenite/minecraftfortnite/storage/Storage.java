/*
 * Copyright 2019 Ivan Pekov (MrIvanPlays)

 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in the
 * Software without restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

 * The above copyright notice and this permission notice shall be included in all copies
 * or substantial portions of the Software.

 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 **/
package net.minenite.minecraftfortnite.storage;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Location;

public class Storage {

    private final DataYAML data;
    private final int[] integers = new int[2];

    public Storage(File dataFolder) {
        data = new DataYAML(dataFolder);
        integers[0] = 0;
        integers[1] = 0;
    }

    public void serialize(EnumDataDirection dataDirection, Location location) {
        Map<String, Object> serialized = location.serialize();
        for (Map.Entry<String, Object> entry : serialized.entrySet()) {
            switch (dataDirection) {
                case TO_CHEST_LOCATION:
                    int posChestLoc;
                    if (data.getConfiguration().get(dataDirection.getPathToStart() + integers[1]) != null) {
                        posChestLoc = integers[1]++;
                        integers[1] = posChestLoc;
                    } else {
                        posChestLoc = integers[1];
                    }
                    data.getConfiguration().set(dataDirection.getPathToStart() + posChestLoc +
                            "." + entry.getKey(), entry.getValue());
                    data.save();
                    break;
                case TO_SPAWN_LOCATION:
                    data.getConfiguration().set(dataDirection.getPathToStart() + entry.getKey(),
                            entry.getValue());
                    data.save();
                    break;
                default:
                    int posNormalLoc;
                    if (data.getConfiguration().get(dataDirection.getPathToStart() + integers[0]) != null) {
                        posNormalLoc = integers[0]++;
                        integers[0] = posNormalLoc;
                    } else {
                        posNormalLoc = integers[0];
                    }
                    data.getConfiguration().set(dataDirection.getPathToStart() + posNormalLoc + "." + entry.getKey(), entry.getValue());
                    data.save();
            }
        }
    }

    /**
     * Pos will be ignored if data direction is to_spawn_location
     **/
    public Location deserialize(EnumDataDirection dataDirection, int pos) {
        Map<String, Object> map = new ConcurrentHashMap<>();
        String basePath = dataDirection == EnumDataDirection.TO_SPAWN_LOCATION ?
                dataDirection.getPathToStart() : dataDirection.getPathToStart() + pos;
        if (data.getConfiguration().get(basePath) != null) {
            map.put("world", data.getConfiguration().get(basePath + "world"));
            map.put("x", data.getConfiguration().get(basePath + "x"));
            map.put("y", data.getConfiguration().get(basePath + "y"));
            map.put("z", data.getConfiguration().get(basePath + "z"));
            map.put("yaw", data.getConfiguration().get(basePath + "yaw"));
            map.put("pitch", data.getConfiguration().get(basePath + "pitch"));
        }
        return Location.deserialize(map);
    }
}
