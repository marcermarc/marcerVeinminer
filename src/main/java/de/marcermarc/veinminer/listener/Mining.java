package de.marcermarc.veinminer.listener;


import de.marcermarc.veinminer.controller.PluginController;
import de.marcermarc.veinminer.objects.Tool;
import de.marcermarc.veinminer.objects.VeinminerMining;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collection;
import java.util.HashSet;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import static org.bukkit.Material.AIR;

public class Mining implements Listener {

    private final int[][] AREA = {
            {-1, -1, -1}, {0, -1, -1}, {1, -1, -1},
            {-1, -1, 0}, {0, -1, 0}, {1, -1, 0},
            {-1, -1, 1}, {0, -1, 1}, {1, -1, 1},

            {-1, 0, -1}, {0, 0, -1}, {1, 0, -1},
            {-1, 0, 0}, {1, 0, 0}, // {0,0,0} not try to inspect own position a second time
            {-1, 0, 1}, {0, 0, 1}, {1, 0, 1},

            {-1, 1, -1}, {0, 1, -1}, {1, 1, -1},
            {-1, 1, 0}, {0, 1, 0}, {1, 1, 0},
            {-1, 1, 1}, {0, 1, 1}, {1, 1, 1},
    };

    private PluginController controller;
    private Random random;

    public Mining(PluginController controller) {
        this.controller = controller;
        random = new Random();
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockBreak(BlockBreakEvent event) {
        if (controller.getConfig().getEnabledPlayers().contains(event.getPlayer()) && event.getPlayer().isSneaking()) {

            Block bl = event.getBlock();
            Material material = bl.getType();
            ItemStack item = event.getPlayer().getInventory().getItemInMainHand();
            Entity entity = event.getPlayer();
            HashSet<Material> veinMinerMaterials = getVeinMinerList(item);

            if (veinMinerMaterials != null && veinMinerMaterials.contains(material) && canBreak(item, bl, entity)) {

                VeinminerMining vm = new VeinminerMining(material, item, entity);

                veinminer(bl, vm);

                for (ItemStack d : vm.getDropBlocks()) {
                    event.getPlayer().getWorld().dropItem(bl.getLocation().add(0.5, 0.2, 0.5), d);
                }

                int exp = getDropExperiance(vm.getType(), vm.getDestroyedBlocks());

                if (exp != 0) {
                    (event.getPlayer().getWorld().spawn(bl.getLocation().add(0.5, 0.2, 0.5), ExperienceOrb.class)).setExperience(exp);
                }

                if (vm.getMeta().getDamage() >= item.getType().getMaxDurability()) {
                    event.getPlayer().getInventory().setItemInMainHand(new ItemStack(AIR));
                    event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.ENTITY_ITEM_BREAK, 1.0f, 1.0f); // optional
                } else {
                    item.setItemMeta((ItemMeta) vm.getMeta());
                    event.getPlayer().getInventory().setItemInMainHand(item);
                }
            }
        }
    }

    private boolean canBreak(ItemStack item, Block bl, Entity entity) {
        Collection<ItemStack> result = bl.getDrops(item, entity);
        return !result.isEmpty();
    }

    private HashSet<Material> getVeinMinerList(ItemStack item) {
        Tool tool = Tool.getByTool(item.getType());

        if (tool != null) {
            return tool.getVeinminerMaterials();
        }

        return null;
    }

    private void veinminer(Block bl, VeinminerMining vm) {
        vm.addDropsForOneBlock(bl.getDrops(vm.getHoldItem(), vm.getEntity()));

        bl.setType(AIR);

        if (vm.getUnbreaking() != -1 && random.nextDouble() <= (1.0 / (vm.getUnbreaking() + 1.0))) {
            vm.getMeta().setDamage((vm.getMeta().getDamage() + 1));
            if (vm.getMeta().getDamage() > vm.getHoldItem().getType().getMaxDurability())
                vm.getHoldItem().setType(AIR);
        }

        for (int[] a : AREA) {
            if (vm.getHoldItem().getType().equals(AIR)) break;

            Block b = bl.getWorld().getBlockAt(bl.getX() + a[0], bl.getY() + a[1], bl.getZ() + a[2]);

            if (vm.getType() == b.getType()) {
                veinminer(b, vm);
            }
        }
    }

    private int getDropExperiance(Material material, int breakAmount) {
        switch (material) {
            case COAL_ORE:
                final int minByBreakCoal = 0;
                final int maxByBreakCoal = 2;
                return ThreadLocalRandom.current().nextInt(minByBreakCoal * breakAmount, maxByBreakCoal * breakAmount);

            case DIAMOND_ORE:
            case EMERALD_ORE:
                final int minByBreakDia = 3;
                final int maxByBreakDia = 7;
                return ThreadLocalRandom.current().nextInt(minByBreakDia * breakAmount, maxByBreakDia * breakAmount);

            case NETHER_QUARTZ_ORE:
            case LAPIS_ORE:
                final int minByBreakNet = 2;
                final int maxByBreakNet = 5;
                return ThreadLocalRandom.current().nextInt(minByBreakNet * breakAmount, maxByBreakNet * breakAmount);

            case REDSTONE_ORE:
                final int minByBreakRed = 1;
                final int maxByBreakRed = 5;
                return ThreadLocalRandom.current().nextInt(minByBreakRed * breakAmount, maxByBreakRed * breakAmount);
        }
        return 0;
    }
}