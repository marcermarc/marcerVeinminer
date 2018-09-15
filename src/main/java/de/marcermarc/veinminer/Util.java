package de.marcermarc.veinminer;


import org.bukkit.Material;
import org.bukkit.NamespacedKey;

import java.util.List;
import java.util.stream.Collectors;

public class Util {
    /**
     * Damit keine Instanz erstellt wird
     */
    private Util() {
    }

    public static String materialToString(Material material) {
        return NamespacedKey.MINECRAFT + ":" + material.toString().toLowerCase();
    }

    public static Material stringToMaterial(String material) {
        return Material.matchMaterial(material);
    }

    public static List<String> tabCompleteFilter(List<String> full, String startetText) {
        return full.stream().filter(s -> startetText.isEmpty() || s.contains(startetText)).collect(Collectors.toList());
    }
}
