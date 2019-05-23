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
package net.minenite.minecraftfortnite.game.timer;

import java.util.concurrent.TimeUnit;

public class TimeParser {

    public static String convert(long seconds) {
        long day = TimeUnit.SECONDS.toDays(seconds);
        long hours = TimeUnit.SECONDS.toHours(seconds) - (day * 24);
        long minute = TimeUnit.SECONDS.toMinutes(seconds) - (TimeUnit.SECONDS.toHours(seconds) * 60);
        long second = TimeUnit.SECONDS.toSeconds(seconds) - (TimeUnit.SECONDS.toMinutes(seconds) * 60);
        String newSeconds = second == 1 ? "1 second" : second + " seconds";
        String newMinutes = minute == 1 ? "1 minute" : minute + " minutes";
        String newHours = hours == 1 ? "1 hour" : hours + " hours";
        String newDays = day == 1 ? "1 day" : day + " days";
        if (day == 0) {
            if (hours == 0) {
                if (minute == 0) {
                    return newSeconds;
                } else {
                    return newMinutes + " " + (second == 0 ? "" : newSeconds);
                }
            } else {
                return newHours + " " + (minute == 0 ? "" : newMinutes) + " " + (second == 0 ? "" : newSeconds);
            }
        } else {
            return newDays + " " + (hours == 0 ? "" : newHours) + " " + (minute == 0 ? "" : newMinutes) + " " + (second == 0 ? "" : newSeconds);
        }
    }

    public static int parseActualTime(String input) {
        int actualTime = 0;
        if (input.endsWith("s")) {
            try {
                actualTime = Integer.parseInt(input.replaceAll("s", ""));
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        } else if (input.endsWith("m")) {
            try {
                actualTime = Integer.parseInt(input.replaceAll("m", "")) * 60;
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        } else if (input.endsWith("h")) {
            try {
                actualTime = Integer.parseInt(input.replaceAll("h", "")) * 60 * 60;
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        } else if (input.endsWith("d")) {
            try {
                actualTime = Integer.parseInt(input.replaceAll("d", "")) * 60 * 60 * 24;
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        } else if (input.endsWith("M")) {
            try {
                actualTime = Integer.parseInt(input.replaceAll("M", "")) * 60 * 60 * 24 * 30;
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        } else if (input.endsWith("y")) {
            try {
                actualTime = Integer.parseInt(input.replaceAll("y", "")) * 60 * 60 * 24 * 30 * 12;
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        return actualTime;
    }
}
