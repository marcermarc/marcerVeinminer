package de.marcermarc.veinminer.listener;


import de.marcermarc.veinminer.controller.PluginController;
import de.marcermarc.veinminer.objects.Tool;
import de.marcermarc.veinminer.objects.VeinminerMining;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
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

import static org.bukkit.Material.*;

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
            ItemStack item = event.getPlayer().getInventory().getItemInMainHand();
            HashSet<Material> veinMinerMaterials = getVeinMinerList(item);

            if (veinMinerMaterials != null && veinMinerMaterials.contains(bl.getType()) && canBreak(item, bl)) {

                VeinminerMining vm = new VeinminerMining(bl.getType(), item);

                veinminer(bl, vm);

                for (ItemStack d : vm.getDropBlocks()) {
                    event.getPlayer().getWorld().dropItem(bl.getLocation().add(0, 1, 0), d);
                }

                if (vm.getDropExperiance() != 0) {
                    (event.getPlayer().getWorld().spawn(bl.getLocation(), ExperienceOrb.class)).setExperience(vm.getDropExperiance());
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

    private boolean canBreak(ItemStack item, Block bl) {
        switch (item.getType()) {
            case WOODEN_PICKAXE:
                if (bl.getType().equals(Material.IRON_ORE) ||
                        bl.getType().equals(Material.LAPIS_ORE))
                    return false;
            case STONE_PICKAXE:
                if (bl.getType().equals(Material.DIAMOND_ORE) ||
                        bl.getType().equals(Material.GOLD_ORE) ||
                        bl.getType().equals(Material.REDSTONE_ORE))
                    return false;

            case IRON_PICKAXE:
            case GOLDEN_PICKAXE:
                if (bl.getType().equals(Material.OBSIDIAN))
                    return false;
                break;
        }
        return true;

    }

    private HashSet<Material> getVeinMinerList(ItemStack item) {
        Tool tool = Tool.getByTool(item.getType());

        if (tool != null) {
            return tool.getVeinminerMaterials();
        }

        return null;
    }

    private void veinminer(Block bl, VeinminerMining vm) {
        if (vm.isSilktouch()) {
            vm.addDropBlock(new ItemStack(bl.getType(), 1));
        } else {
            getDrops(bl, vm);
        }

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

    private void getDrops(Block bl, VeinminerMining vm) {
        switch (bl.getType()) {
            case COAL_ORE:
            case DIAMOND_ORE:
            case EMERALD_ORE:
            case NETHER_QUARTZ_ORE:
            case LAPIS_ORE:

                Collection<ItemStack> itemStacksOre = bl.getDrops();

                double ranOres = random.nextDouble();

                for (ItemStack is : itemStacksOre) {
                    int newAmount = is.getAmount();

                    for (int i = 1; i <= vm.getLuckLevel(); i++) {
                        if (ranOres <= ((double) i / (double) (vm.getLuckLevel() + 2))) {
                            newAmount *= i + 1;
                            break;
                        }
                    }

                    is.setAmount(newAmount);
                }

                vm.addRangeDropBlocks(itemStacksOre);

                switch (bl.getType()) {
                    case COAL_ORE:
                        vm.addDropExperiance(random.nextInt(2));
                        break;
                    case DIAMOND_ORE:
                    case EMERALD_ORE:
                        vm.addDropExperiance(random.nextInt(5) + 3);
                        break;
                    case NETHER_QUARTZ_ORE:
                    case LAPIS_ORE:
                        vm.addDropExperiance(random.nextInt(4) + 2);
                        break;
                }
                break;

            case REDSTONE_ORE:
                vm.addDropBlock(new ItemStack(REDSTONE, random.nextInt(1 + vm.getLuckLevel()) + 4));
                vm.addDropExperiance(random.nextInt(5) + 1);
                break;

            case CARROTS:
                Ageable dataCarrot = (Ageable) bl.getBlockData();

                if (dataCarrot.getAge() == dataCarrot.getMaximumAge()) {
                    vm.addDropBlock(new ItemStack(CARROT, random.nextInt(3 + vm.getLuckLevel()) + 1));
                }
                break;

            case GLOWSTONE:
                int amountGlowstone = random.nextInt(2 + vm.getLuckLevel()) + 2;
                vm.addDropBlock(new ItemStack(GLOWSTONE_DUST, amountGlowstone > 4 ? 4 : amountGlowstone));
                break;

            case SEA_LANTERN:
                int amountSealatern = random.nextInt(1 + vm.getLuckLevel()) + 2;
                vm.addDropBlock(new ItemStack(PRISMARINE_CRYSTALS, amountSealatern > 5 ? 5 : amountSealatern));
                break;

            case MELON:
                int amountMelon = random.nextInt(4 + vm.getLuckLevel()) + 3;
                vm.addDropBlock(new ItemStack(MELON_SLICE, amountMelon > 9 ? 9 : amountMelon));
                break;

            case NETHER_WART:
                vm.addDropBlock(new ItemStack(NETHER_WART, random.nextInt(2 + vm.getLuckLevel()) + 2));
                break;

            case POTATOES:
                Ageable dataPotato = (Ageable) bl.getBlockData();

                if (dataPotato.getAge() == dataPotato.getMaximumAge()) {
                    vm.addDropBlock(new ItemStack(POTATO, random.nextInt(3 + vm.getLuckLevel()) + 1));
                }
                break;

            case WHEAT:
                Ageable dataWheat = (Ageable) bl.getBlockData();

                if (dataWheat.getAge() == dataWheat.getMaximumAge()) {
                    vm.addDropBlock(new ItemStack(WHEAT_SEEDS, random.nextInt(3 + vm.getLuckLevel())));
                    vm.addDropBlock(new ItemStack(WHEAT, 1));
                }
                break;

            case BEETROOTS:
                Ageable dataBeetroot = (Ageable) bl.getBlockData();

                if (dataBeetroot.getAge() == dataBeetroot.getMaximumAge()) {
                    vm.addDropBlock(new ItemStack(BEETROOT_SEEDS, random.nextInt(3 + vm.getLuckLevel())));
                    vm.addDropBlock(new ItemStack(BEETROOT, 1));
                }
                break;

            case TALL_GRASS:
                Collection<ItemStack> itemStacksGrass = bl.getDrops();

                double ranGrass = random.nextDouble();

                for (ItemStack is : itemStacksGrass) {
                    int newAmount = is.getAmount();

                    for (int i = 1; i <= vm.getLuckLevel(); i++) {
                        if (ranGrass <= ((double) i / (double) vm.getLuckLevel() + 2)) {
                            newAmount *= i + 1;
                            break;
                        }
                    }

                    is.setAmount(newAmount);
                }
                vm.addRangeDropBlocks(itemStacksGrass);
                break;

            case GRAVEL:

                double ranGravel = random.nextDouble();

                ItemStack isGravel = new ItemStack(GRAVEL, 1);
                //Formel: 0.095 x^3 - 0.25 x^2 + 0.195 x + 0.1 not needed because at 3 or more ist every time flint

                switch (vm.getLuckLevel()) {
                    case 0:
                        if (ranGravel <= 0.1) isGravel = new ItemStack(FLINT, 1);
                        break;
                    case 1:
                        if (ranGravel <= 0.14) isGravel = new ItemStack(FLINT, 1);
                        break;
                    case 2:
                        if (ranGravel <= 0.25) isGravel = new ItemStack(FLINT, 1);
                        break;
                    default:
                        isGravel = new ItemStack(FLINT, 1);
                }
                vm.addDropBlock(isGravel);
                break;

            case OAK_LEAVES:
            case DARK_OAK_LEAVES:
                double neededApple = Math.pow(0.00021 * vm.getLuckLevel(), 3) + Math.pow(0.000565 * vm.getLuckLevel(), 2) + 0.000915 * vm.getLuckLevel() + 0.005;
                if (neededApple >= 1 || random.nextDouble() <= neededApple) vm.addDropBlock(new ItemStack(APPLE, 1));
            case ACACIA_LEAVES:
            case BIRCH_LEAVES:
            case SPRUCE_LEAVES:
                double neededSapling = Math.pow((0.0062 / 3) * vm.getLuckLevel(), 3) + Math.pow(0.01035 * vm.getLuckLevel(), 2) + (0.01265 / 3) * vm.getLuckLevel() + 0.05;
                if (neededSapling >= 1 || random.nextDouble() <= neededSapling) {
                    switch (bl.getType()) {
                        case OAK_LEAVES:
                            vm.addDropBlock(new ItemStack(OAK_SAPLING));
                            break;
                        case DARK_OAK_LEAVES:
                            vm.addDropBlock(new ItemStack(DARK_OAK_SAPLING));
                            break;
                        case ACACIA_LEAVES:
                            vm.addDropBlock(new ItemStack(ACACIA_SAPLING));
                            break;
                        case BIRCH_LEAVES:
                            vm.addDropBlock(new ItemStack(BIRCH_SAPLING));
                            break;
                        case SPRUCE_LEAVES:
                            vm.addDropBlock(new ItemStack(SPRUCE_SAPLING));
                            break;
                    }
                }
                break;

            case JUNGLE_LEAVES:
                double neededJungleSapling = Math.pow((0.190825 / 3) * vm.getLuckLevel(), 3) + Math.pow(-0.1905 * vm.getLuckLevel(), 2) + (0.389075 / 3) * vm.getLuckLevel() + 0.025;
                if (neededJungleSapling >= 1 || random.nextDouble() <= neededJungleSapling) vm.addDropBlock(new ItemStack(JUNGLE_SAPLING, 1));
                break;

            default:
                vm.addRangeDropBlocks(bl.getDrops());
        }

        // https://minecraft.gamepedia.com/Fortune
        // https://minecraft-de.gamepedia.com/Verzauberung#Gl.C3.BCck
    }

}