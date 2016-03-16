package de.marcermarc.lock.controller;

import de.marcermarc.lock.objects.MaterialType;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ConfigController {

    private PluginController controller;

    private ArrayList<MaterialType> pickaxe, axe, spade, hoe, sword, shears;

    private ArrayList<Player> enabledPlayers;

    public ConfigController(PluginController controller) {
        this.controller = controller;

        enabledPlayers = new ArrayList();

        setDefaultConfig();

        loadBlockLists();
    }

    private void setDefaultConfig() {

        pickaxe = new ArrayList();
        axe = new ArrayList();
        spade = new ArrayList();
        hoe = new ArrayList();
        sword = new ArrayList();
        shears = new ArrayList();

        controller.getMain().getConfig().addDefault("pickaxe", new ArrayList<String>());
        controller.getMain().getConfig().addDefault("axe", new ArrayList<String>());
        controller.getMain().getConfig().addDefault("spade", new ArrayList<String>());
        controller.getMain().getConfig().addDefault("hoe", new ArrayList<String>());
        controller.getMain().getConfig().addDefault("sword", new ArrayList<String>());

        controller.getMain().getConfig().options().copyDefaults(true);
        controller.getMain().saveDefaultConfig();

    }

    private void loadBlockLists() {
        loadBlockLists("pickaxe", pickaxe);
        loadBlockLists("axe", axe);
        loadBlockLists("spade", spade);
        loadBlockLists("hoe", hoe);
        loadBlockLists("sword", sword);
        loadBlockLists("shears", shears);
    }

    private void loadBlockLists(String name, ArrayList<MaterialType> m) {
        m.clear();

        List<String> sL = controller.getMain().getConfig().getStringList(name);
        for (String s : sL) {
            m.add(new MaterialType(s));
        }
    }

    public boolean saveBlocks() {
        saveBlockLists("pickaxe", pickaxe);
        saveBlockLists("axe", axe);
        saveBlockLists("spade", spade);
        saveBlockLists("hoe", hoe);
        saveBlockLists("sword", sword);
        saveBlockLists("shears", shears);

        controller.getMain().saveConfig();

        return true;
    }

    public void saveBlockLists(String name, List<MaterialType> mat) {
        List<String> s = new ArrayList();
        for (MaterialType m : mat) {
            s.add(m.toString());
        }
        controller.getMain().getConfig().set(name, s);
    }

    public boolean loadBlocks() {
        controller.getMain().reloadConfig();

        loadBlockLists();

        return true;
    }

    //region getters and setters
    public ArrayList<MaterialType> getPickaxe() {
        return pickaxe;
    }

    public void setPickaxe(ArrayList<MaterialType> pickaxe) {
        this.pickaxe = pickaxe;
    }

    public ArrayList<MaterialType> getAxe() {
        return axe;
    }

    public void setAxe(ArrayList<MaterialType> axe) {
        this.axe = axe;
    }

    public ArrayList<MaterialType> getSpade() {
        return spade;
    }

    public void setSpade(ArrayList<MaterialType> spade) {
        this.spade = spade;
    }

    public ArrayList<MaterialType> getHoe() {
        return hoe;
    }

    public void setHoe(ArrayList<MaterialType> hoe) {
        this.hoe = hoe;
    }

    public ArrayList<MaterialType> getShears() {
        return shears;
    }

    public void setShears(ArrayList<MaterialType> shears) {
        this.shears = shears;
    }

    public ArrayList<MaterialType> getSword() {
        return sword;
    }

    public void setSword(ArrayList<MaterialType> sword) {
        this.sword = sword;
    }

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
