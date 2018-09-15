package de.marcermarc.veinminer;

import de.marcermarc.veinminer.controller.ConfigController;
import de.marcermarc.veinminer.controller.PluginController;
import de.marcermarc.veinminer.listener.Command;
import de.marcermarc.veinminer.listener.Mining;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    private PluginController controller = new PluginController();

    @Override
    public void onEnable() {
        controller.setMain(this);

        PluginManager pM = getServer().getPluginManager();

        registerEvents(pM);

        controller.setConfig(new ConfigController(controller));
    }

    private void registerEvents(PluginManager in_PM) {
        in_PM.registerEvents(new Mining(controller), this);

        Command c = new Command(controller);
        in_PM.registerEvents(c, this);

        this.getCommand("marcerVeinminer").setExecutor(c);
        this.getCommand("mV").setExecutor(c);
        this.getCommand("marcerVeinminer").setTabCompleter(c);
        this.getCommand("mV").setTabCompleter(c);
    }

    @Override
    public void onDisable() {
        controller.getConfig().saveConfig();
    }
}