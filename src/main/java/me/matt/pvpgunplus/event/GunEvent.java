// 
// Decompiled by Procyon v0.5.34
// 

package me.matt.pvpgunplus.event;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class GunEvent extends Event implements Cancellable {

    private static HandlerList handlers;

    static {
        GunEvent.handlers = new HandlerList();
    }

    private boolean cancelled;

    public GunEvent() {
        cancelled = false;
    }

    public static HandlerList getHandlerList() {
        return GunEvent.handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return GunEvent.handlers;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean arg0) {
        cancelled = arg0;
    }
}
