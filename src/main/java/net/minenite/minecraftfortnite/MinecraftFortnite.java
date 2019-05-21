package net.minenite.minecraftfortnite;

import co.aikar.commands.PaperCommandManager;
import net.minenite.minecraftfortnite.commands.CommandInfo;
import net.minenite.minecraftfortnite.storage.Storage;
import org.bukkit.plugin.java.JavaPlugin;

public final class MinecraftFortnite extends JavaPlugin {

    private Storage storage;

    @Override
    public void onEnable() {
        setCommands();
        storage = new Storage(getDataFolder());
        getLogger().info("Enabled");
    }

    @Override
    public void onDisable() {
        getLogger().info("Disabled");
    }

    private void setCommands() {
        PaperCommandManager manager = new PaperCommandManager(this);
        manager.registerCommand(new CommandInfo());
    }

    /**
     * Storage, used to store locations of objectsK
     */
    public Storage getStorage() {
        return storage;
    }
}
