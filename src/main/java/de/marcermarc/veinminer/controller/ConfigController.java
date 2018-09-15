package de.marcermarc.veinminer.controller;

import de.marcermarc.veinminer.Util;
import de.marcermarc.veinminer.objects.Tool;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ConfigController {

    private PluginController controller;

    private ArrayList<Player> enabledPlayers;

    public ConfigController(PluginController controller) {
        this.controller = controller;

        enabledPlayers = new ArrayList<>();

        setDefaultConfig();

        loadBlockLists();
    }

    private void setDefaultConfig() {
        for (Tool tool : Tool.values()) {
            controller.getMain().getConfig().addDefault(tool.toString(), new ArrayList<String>());
        }

        controller.getMain().getConfig().options().copyDefaults(true);
        controller.getMain().saveDefaultConfig();
    }

    private void loadBlockLists() {
        for (Tool tool : Tool.values()) {
            loadBlockLists(tool);
        }
    }

    private void loadBlockLists(Tool tool) {
        tool.getVeinminerMaterials().clear();

        List<String> materialStrings = controller.getMain().getConfig().getStringList(tool.toString());

        for (String materialString : materialStrings) {
            tool.getVeinminerMaterials().add(Material.matchMaterial(materialString));
        }
    }

    private void saveBlockLists() {
        for (Tool tool : Tool.values()) {
            saveBlockLists(tool);
        }
    }

    private void saveBlockLists(Tool tool) {
        List<String> materialStrings = new ArrayList<>();

        for (Material material : tool.getVeinminerMaterials()) {
            materialStrings.add(Util.materialToString(material));
        }

        controller.getMain().getConfig().set(tool.toString(), materialStrings);
    }

    public boolean loadConfig() {
        controller.getMain().reloadConfig();

        loadBlockLists();

        return true;
    }

    public boolean saveConfig() {
        saveBlockLists();

        controller.getMain().saveConfig();

        return true;
    }

    //region getters and setters
    public PluginController getController() {
        return controller;
    }

    public void setController(PluginController controller) {
        this.controller = controller;
    }

    public ArrayList<Player> getEnabledPlayers() {
        return enabledPlayers;
    }

    public void setEnabledPlayers(ArrayList<Player> enabledPlayers) {
        this.enabledPlayers = enabledPlayers;
    }

    //endregion


}
