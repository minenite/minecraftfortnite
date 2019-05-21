package net.minenite.minecraftfortnite;

import co.aikar.commands.PaperCommandManager;
import net.minenite.minecraftfortnite.commands.CommandInfo;
import org.bukkit.plugin.java.JavaPlugin;

public final class MinecraftFortnite extends JavaPlugin {

    @Override
    public void onEnable() {
        setCommands();
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
}
