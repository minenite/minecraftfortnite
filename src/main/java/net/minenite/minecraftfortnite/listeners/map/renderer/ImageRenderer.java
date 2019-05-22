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
package net.minenite.minecraftfortnite.listeners.map.renderer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import net.minenite.minecraftfortnite.MinecraftFortnite;
import net.minenite.minecraftfortnite.listeners.map.MapUtils;
import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapCursorCollection;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.jetbrains.annotations.NotNull;

public class ImageRenderer extends MapRenderer {

    private BufferedImage image;

    public ImageRenderer(MinecraftFortnite main) {
        File[] files = main.getDataFolder().listFiles((file, name) -> endsWithImageExtension(name));
        if (files == null) {
            image = null;
            return;
        }
        if (files.length > 1) {
            throw new UnsupportedOperationException("There are 2 or more image files inside data " +
                    "folder. Cannot render 2 or more images!");
        }
        try {
            image = ImageIO.read(files[0]);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void render(@NotNull MapView map, @NotNull MapCanvas canvas, @NotNull Player player) {
        MapUtils.removeRenderers(map);
        if (image == null) {
            return;
        }
        MapCursorCollection cursors = canvas.getCursors();
        for (int cursor = 0; cursor < cursors.size(); cursor++) {
            cursors.removeCursor(cursors.getCursor(cursor));
        }
        canvas.drawImage(0, 0, image);
    }

    private boolean endsWithImageExtension(String name) {
        return name.endsWith(".jpg") || name.endsWith(".png") || name.endsWith(".jpeg");
    }
}
