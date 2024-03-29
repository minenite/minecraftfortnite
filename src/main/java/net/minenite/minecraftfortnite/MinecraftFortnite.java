package net.minenite.minecraftfortnite;

import co.aikar.commands.BukkitCommandManager;
import net.minenite.minecraftfortnite.commands.CommandModify;
import net.minenite.minecraftfortnite.game.Game;
import net.minenite.minecraftfortnite.listeners.InventoryClickListener;
import net.minenite.minecraftfortnite.listeners.PlayerJoinListener;
import net.minenite.minecraftfortnite.listeners.PlayerPreLoginListener;
import net.minenite.minecraftfortnite.listeners.PlayerQuitListener;
import net.minenite.minecraftfortnite.listeners.map.MapInitializeListener;
import net.minenite.minecraftfortnite.storage.EnumDataDirection;
import net.minenite.minecraftfortnite.storage.Storage;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.map.MapView;
import org.bukkit.plugin.java.JavaPlugin;

public final class MinecraftFortnite extends JavaPlugin {

    private Storage storage;
    private Game game;
    private MapInitializeListener mapInitializeListener;

    @Override
    public void onEnable() {
        setCommands();
        storage = new Storage(getDataFolder());
        setSpawnLocationOfWorld();
        setGameInstance();
        registerListeners();
        getLogger().info("Enabled");
    }

    @Override
    public void onDisable() {
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
    }

    private void setGameInstance() {
        game = new Game(this, 50);
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
}
