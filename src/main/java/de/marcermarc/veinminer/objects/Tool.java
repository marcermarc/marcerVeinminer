package de.marcermarc.veinminer.objects;

import org.bukkit.Material;

import java.util.HashSet;
import java.util.stream.Stream;

public enum Tool {
    PICKAXE("pickaxe", Material.WOODEN_PICKAXE, Material.STONE_PICKAXE, Material.IRON_PICKAXE, Material.GOLDEN_PICKAXE, Material.DIAMOND_PICKAXE),
    SHOVEL("shovel", Material.WOODEN_SHOVEL, Material.STONE_SHOVEL, Material.IRON_SHOVEL, Material.GOLDEN_SHOVEL, Material.DIAMOND_SHOVEL),
    AXE("axe", Material.WOODEN_AXE, Material.STONE_AXE, Material.IRON_AXE, Material.GOLDEN_AXE, Material.DIAMOND_AXE),
    HOE("hoe", Material.WOODEN_HOE, Material.STONE_HOE, Material.IRON_HOE, Material.GOLDEN_HOE, Material.DIAMOND_HOE),
    SWORD("sword", Material.WOODEN_SWORD, Material.STONE_SWORD, Material.IRON_SWORD, Material.GOLDEN_SWORD, Material.DIAMOND_SWORD),
    SHEARS("shears", Material.SHEARS);

    private final String name;
    private final HashSet<Material> veinminerMaterials;
    private final Material[] tools;

    Tool(String name, Material... tools) {
        this.name = name;
        this.tools = tools;
        this.veinminerMaterials = new HashSet<>();
    }

    public static Tool getByName(String name) {
        return Stream.of(values()).filter(t -> t.toString().equals(name)).findFirst().orElse(null);
    }

    public static Tool getByTool(Material tool) {
        return Stream.of(values())
                .filter(t -> Stream.of(t.tools).anyMatch(m -> m == tool))
                .findFirst().orElse(null);
    }

    @Override
    public String toString() {
        return name;
    }

    public HashSet<Material> getVeinminerMaterials() {
        return veinminerMaterials;
    }

    public Material[] getTools() {
        return tools;
    }
}
