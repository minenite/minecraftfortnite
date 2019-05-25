package net.minenite.minecraftfortnite;

import java.util.ArrayList;
import java.util.List;

import co.aikar.commands.BukkitCommandManager;
import me.matt.pvpgunplus.listeners.MinecraftFortniteListenerRegistry;
import net.minenite.minecraftfortnite.commands.CommandModify;
import net.minenite.minecraftfortnite.game.Game;
import net.minenite.minecraftfortnite.listeners.InventoryClickListener;
import net.minenite.minecraftfortnite.listeners.PlayerJoinListener;
import net.minenite.minecraftfortnite.listeners.PlayerPreLoginListener;
import net.minenite.minecraftfortnite.listeners.PlayerQuitListener;
import net.minenite.minecraftfortnite.listeners.map.MapInitializeListener;
import net.minenite.minecraftfortnite.storage.EnumDataDirection;
import net.minenite.minecraftfortnite.storage.Storage;
import net.minenite.minecraftfortnite.util.GunManager;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.bukkit.map.MapView;
import org.bukkit.plugin.java.JavaPlugin;

public final class MinecraftFortnite extends JavaPlugin {

    private Storage storage;
    private Game game;
    private MapInitializeListener mapInitializeListener;
    private GunManager gunManager;

    @Override
    public void onEnable() {
        setCommands();
        storage = new Storage(getDataFolder());
        gunManager = new GunManager(this);
        gunManager.callThisOnEnable();
        setSpawnLocationOfWorld();
        setGameInstance();
        registerListeners();
        getLogger().info("Enabled");
    }

    @Override
    public void onDisable() {
        gunManager.callThisOnDisable();
        getLogger().info("Disabled");
    }

    private void setCommands() {
        BukkitCommandManager manager = new BukkitCommandManager(this);
        manager.registerCommand(new CommandModify(this));
    }

    /**
     * Storage, used to store locations of objectsK
     */
    public Storage getStorage() {
        return storage;
    }

    private void setSpawnLocationOfWorld() { // just to be sure
        World world = getServer().getWorld("world");
        Location location = storage.deserialize(EnumDataDirection.TO_SPAWN_LOCATION, 0);
        if (location == null) {
            return;
        }
        world.setSpawnLocation(location);
    }

    private void registerListeners() {
        mapInitializeListener = new MapInitializeListener(this);
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(this), this);
        getServer().getPluginManager().registerEvents(mapInitializeListener, this);
        getServer().getPluginManager().registerEvents(new PlayerQuitListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerPreLoginListener(this), this);
        getServer().getPluginManager().registerEvents(new InventoryClickListener(this), this);
        new MinecraftFortniteListenerRegistry(this);
    }

    private void setGameInstance() {
        List<ItemStack> possibleItems = new ArrayList<>();
        // todo: possible items
        game = new Game(this, possibleItems, 50);
    }

    public MapView getMapView() {
        return mapInitializeListener.getMapView();
    }

    public Game getGame() {
        return game;
    }

    public String colorize(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    public GunManager getGunManager() {
        return gunManager;
    }
}
