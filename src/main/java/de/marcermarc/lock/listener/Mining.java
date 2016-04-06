package de.marcermarc.lock.listener;


import de.marcermarc.lock.controller.PluginController;
import de.marcermarc.lock.objects.MaterialType;
import de.marcermarc.lock.objects.VeinminerMining;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Dye;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.bukkit.Material.*;

public class Mining implements Listener {


    private final short AREA[][] = {
            {-1, -1, -1}, {0, -1, -1}, {1, -1, -1},
            {-1, -1, 0}, {0, -1, 0}, {1, -1, 0},
            {-1, -1, 1}, {0, -1, 1}, {1, -1, 1},

            {-1, 0, -1}, {0, 0, -1}, {1, 0, -1},
            {-1, 0, 0}, {1, 0, 0}, // {0,0,0} nicht, eigene Position nicht prüfen
            {-1, 0, 1}, {0, 0, 1}, {1, 0, 1},

            {-1, 1, -1}, {0, 1, -1}, {1, 1, -1},
            {-1, 1, 0}, {0, 1, 0}, {1, 1, 0},
            {-1, 1, 1}, {0, 1, 1}, {1, 1, 1},
    };
    private final static Material LAPIS = new Dye(DyeColor.BLUE).getItemType();

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
            ItemStack item = event.getPlayer().getItemInHand();
            List<MaterialType> veinMinerMaterials = getVeinMinerList(item);

            if (veinMinerMaterials != null && veinMinerMaterials.contains(new MaterialType(bl.getType(), bl.getData())) && canBreak(item, bl)) {

                VeinminerMining vm = new VeinminerMining(new MaterialType(bl.getType(), bl.getData()), item);

                veinminer(bl, vm);

                for (ItemStack d : vm.getDropBlocks()) {
                    event.getPlayer().getWorld().dropItem(bl.getLocation(), d);
                }

                if (vm.getDropExperiance() != 0) {
                    (event.getPlayer().getWorld().spawn(bl.getLocation(), ExperienceOrb.class)).setExperience(vm.getDropExperiance());
                }

                if (item.getDurability() >= item.getType().getMaxDurability()) {
                    event.getPlayer().setItemInHand(new ItemStack(AIR));
                    event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.ITEM_BREAK, 1.0f, 1.0f); // optional
                } else {
                    event.getPlayer().setItemInHand(item);
                }
            }
        }
    }

    private boolean canBreak(ItemStack item, Block bl) {
        switch (item.getTypeId()) {
            case (270): //Material.WOOD_PICKAXE
                if (bl.getType().equals(Material.IRON_ORE) ||
                        bl.getType().equals(Material.LAPIS_ORE))
                    return false;
            case (274): //Material.STONE_PICKAXE
                if (bl.getType().equals(Material.DIAMOND_ORE) ||
                        bl.getType().equals(Material.GOLD_ORE) ||
                        bl.getType().equals(Material.REDSTONE_ORE) ||
                        bl.getType().equals(Material.GLOWING_REDSTONE_ORE))
                    return false;

            case (257): //Material.IRON_PICKAXE
            case (285): //Material.GOLD_PICKAXE
                if (bl.getType().equals(Material.OBSIDIAN))
                    return false;
                break;
        }
        return true;

    }

    private List<MaterialType> getVeinMinerList(ItemStack item) {
        if (item.getType().name().contains("_PICKAXE")) {
            return controller.getConfig().getPickaxe();
        } else if (item.getType().name().contains("_AXE")) {
            return controller.getConfig().getAxe();
        } else if (item.getType().name().contains("_SPADE")) {
            return controller.getConfig().getSpade();
        } else if (item.getType().name().contains("_HOE")) {
            return controller.getConfig().getHoe();
        } else if (item.getType().name().contains("_SWORD")) {
            return controller.getConfig().getSword();
        } else if (item.getType().name().equals("_SHEARS")) {
            return controller.getConfig().getShears();
        } else {
            return null;
        }
    }

    private void veinminer(Block bl, VeinminerMining vm) {
        if (vm.isSilktouch()) {
            vm.addDropBlock(new ItemStack(bl.getType(), 1, bl.getData()));
        } else {
            getDrops(bl, vm);
        }

        bl.setType(AIR);

        if (vm.getUnbreaking() != -1 && random.nextDouble() <= (1.0 / (vm.getUnbreaking() + 1.0))) {
            vm.getHoldItem().setDurability((short) (vm.getHoldItem().getDurability() + 1));
            if (vm.getHoldItem().getDurability() > vm.getHoldItem().getType().getMaxDurability())
                vm.getHoldItem().setType(AIR);
        }

        for (short[] a : AREA) {

            if (vm.getHoldItem().getType().equals(AIR)) break;

            Block b = bl.getWorld().getBlockAt(bl.getX() + a[0], bl.getY() + a[1], bl.getZ() + a[2]);

            if (vm.getType().equals(new MaterialType(b.getType(), b.getData()))) {
                veinminer(b, vm);
            }
        }
    }

    private void getDrops(Block bl, VeinminerMining vm) {
        if (bl.getType().equals(COAL_ORE) ||
                bl.getType().equals(DIAMOND_ORE) ||
                bl.getType().equals(EMERALD_ORE) ||
                bl.getType().equals(QUARTZ_ORE) ||
                bl.getType().equals(LAPIS_ORE)) {
            ArrayList<ItemStack> itemStacks = (ArrayList) bl.getDrops();

            double ran = random.nextDouble();

            int newAmount = itemStacks.get(0).getAmount();

            for (int i = 1; i <= vm.getLuckLevel(); i++) {
                if (ran <= ((double) i / (double) vm.getLuckLevel() + 2)) {
                    newAmount *= i + 1;
                    break;
                }
            }

            /*switch (luckLevel) {
                case 1:
                    if (ran <= 0.33) newAmount *= 2;
                    break;
                case 2:
                    if (ran <= 0.25) newAmount *= 2;
                    else if (ran <= 0.50) newAmount *= 3;
                    break;
                case 3:
                    if (ran <= 0.20) newAmount *= 2;
                    else if (ran <= 0.40) newAmount *= 3;
                    else if (ran <= 0.60) newAmount *= 4;
                    break;
            }*/
            itemStacks.get(0).setAmount(newAmount);

            vm.addRangeDropBlocks(itemStacks);

            if (bl.getType().equals(COAL_ORE)) {
                vm.addDropExperiance(random.nextInt(2));
            } else if (bl.getType().equals(DIAMOND_ORE) ||
                    bl.getType().equals(EMERALD_ORE)) {
                vm.addDropExperiance(random.nextInt(5) + 3);
            } else if (bl.getType().equals(QUARTZ_ORE) ||
                    bl.getType().equals(LAPIS)) {
                vm.addDropExperiance(random.nextInt(4) + 2);
            }

        } else if (bl.getType().equals(REDSTONE_ORE)) {

            vm.addDropBlock(new ItemStack(REDSTONE, random.nextInt(1 + vm.getLuckLevel()) + 4));
            vm.addDropExperiance(random.nextInt(5) + 1);

        } else if (bl.getType().equals(CARROT) && bl.getData() == 7) {

            vm.addDropBlock(new ItemStack(CARROT_ITEM, random.nextInt(3 + vm.getLuckLevel()) + 1));

        } else if (bl.getType().equals(GLOWSTONE)) {

            int amount = random.nextInt(2 + vm.getLuckLevel()) + 2;
            vm.addDropBlock(new ItemStack(GLOWSTONE_DUST, amount > 4 ? 4 : amount));

        } else if (bl.getType().equals(SEA_LANTERN)) {

            int amount = random.nextInt(1 + vm.getLuckLevel()) + 2;
            vm.addDropBlock(new ItemStack(PRISMARINE_CRYSTALS, amount > 5 ? 5 : amount));

        } else if (bl.getType().equals(MELON_BLOCK)) {

            int amount = random.nextInt(4 + vm.getLuckLevel()) + 3;
            vm.addDropBlock(new ItemStack(MELON, amount > 9 ? 9 : amount));

        } else if (bl.getType().equals(NETHER_WARTS)) {

            vm.addDropBlock(new ItemStack(NETHER_WARTS, random.nextInt(2 + vm.getLuckLevel()) + 2));

        } else if (bl.getType().equals(POTATO) && bl.getData() == 7) {

            vm.addDropBlock(new ItemStack(POTATO_ITEM, random.nextInt(3 + vm.getLuckLevel()) + 1));

        } else if (bl.getType().equals(CROPS) && bl.getData() == 7) {

            vm.addDropBlock(new ItemStack(SEEDS, random.nextInt(3 + vm.getLuckLevel())));
            vm.addDropBlock(new ItemStack(WHEAT, 1));

        } else if (bl.getType().equals(GRASS)) {

            ArrayList<ItemStack> itemStacks = (ArrayList) bl.getDrops();
            if (itemStacks.size() >= 1) {
                itemStacks.get(0).setAmount(random.nextInt(vm.getLuckLevel() * 2) + 1);
            }
            vm.addRangeDropBlocks(itemStacks);

        } else if (bl.getType().equals(GRAVEL)) {
            double ran = random.nextDouble();

            ItemStack iS = new ItemStack(GRAVEL, 1);

            //Formel: 0.095 x^3 - 0.25 x^2 + 0.195 x + 0.1

            switch (vm.getLuckLevel()) {
                case 0:
                    if (ran <= 0.1) iS = new ItemStack(FLINT, 1);
                    break;
                case 1:
                    if (ran <= 0.14) iS = new ItemStack(FLINT, 1);
                    break;
                case 2:
                    if (ran <= 0.25) iS = new ItemStack(FLINT, 1);
                    break;
                default:
                    iS = new ItemStack(FLINT, 1);
            }
            vm.addDropBlock(iS);

        } else if (bl.getType().equals(LEAVES) || bl.getType().equals(LEAVES_2)) {
            double ran = random.nextDouble();

            if ((bl.getType().equals(LEAVES) && (bl.getData() == 0 || bl.getData() == 4 || bl.getData() == 8 || bl.getData() == 12)) ||
                    (bl.getType().equals(LEAVES_2) && (bl.getData() == 1 || bl.getData() == 5 || bl.getData() == 9 || bl.getData() == 13))) {
                double appleNeeded = Math.pow(0.00021 * vm.getLuckLevel(), 3) + Math.pow(0.000565 * vm.getLuckLevel(), 2) + 0.000915 * vm.getLuckLevel() + 0.005;

                if (appleNeeded >= 1 || ran <= appleNeeded) vm.addDropBlock(new ItemStack(APPLE, 1));

                /*switch (vm.getLuckLevel()) {
                    case 0:
                        if (ran <= 0.005) vm.addDropBlock(new ItemStack(APPLE, 1));
                        break;
                    case 1:
                        if (ran <= 0.00556) vm.addDropBlock(new ItemStack(APPLE, 1));
                        break;
                    case 2:
                        if (ran <= 0.00625) vm.addDropBlock(new ItemStack(APPLE, 1));
                        break;
                    case 3:
                        if (ran <= 0.00833) vm.addDropBlock(new ItemStack(APPLE, 1));
                }*/
                ran = random.nextDouble();
            }

            double needed = Math.pow((0.0062 / 3) * vm.getLuckLevel(), 3) + Math.pow(0.01035 * vm.getLuckLevel(), 2) + (0.01265 / 3) * vm.getLuckLevel() + 0.05;
            /*switch (vm.getLuckLevel()) {
                case 1:
                    needed = 0.0625;
                    break;
                case 2:
                    needed = 0.0833;
                    break;
                case 3:
                    needed = 0.1;
            }*/

            if (bl.getType().equals(LEAVES)) {
                switch (bl.getData()) {
                    case 0:
                    case 4:
                    case 8:
                    case 12:
                        if (needed >= 1 || ran <= needed) vm.addDropBlock(new ItemStack(SAPLING, 1, (short) 0));
                        break;
                    case 1:
                    case 5:
                    case 9:
                    case 13:
                        if (needed >= 1 || ran <= needed) vm.addDropBlock(new ItemStack(SAPLING, 1, (short) 1));
                        break;
                    case 2:
                    case 6:
                    case 10:
                    case 14:
                        if (needed >= 1 || ran <= needed) vm.addDropBlock(new ItemStack(SAPLING, 1, (short) 2));
                        break;
                    case 3:
                    case 7:
                    case 11:
                    case 15:
                        //Jungle Sapling
                        needed = Math.pow((0.190825 / 3) * vm.getLuckLevel(), 3) + Math.pow(-0.1905 * vm.getLuckLevel(), 2) + (0.389075 / 3) * vm.getLuckLevel() + 0.025;

                        if (needed >= 1 || ran <= needed) vm.addDropBlock(new ItemStack(SAPLING, 1, (short) 3));

                        /*switch (vm.getLuckLevel()) {
                            case 0:
                                if (ran <= 0.025) vm.addDropBlock(new ItemStack(SAPLING, 1, (short) 3));
                                break;
                            case 1:
                                if (ran <= 0.0278) vm.addDropBlock(new ItemStack(SAPLING, 1, (short) 3));
                                break;
                            case 2:
                                if (ran <= 0.03125) vm.addDropBlock(new ItemStack(SAPLING, 1, (short) 3));
                                break;
                            case 3:
                                if (ran <= 0.0417) vm.addDropBlock(new ItemStack(SAPLING, 1, (short) 3));
                        }*/
                        break;
                }
            } else if (bl.getType().equals(LEAVES_2)) {
                switch (bl.getData()) {
                    case 0:
                    case 4:
                    case 8:
                    case 12:
                        if (needed >= 1 || ran <= needed) vm.addDropBlock(new ItemStack(SAPLING, 1, (short) 4));
                        break;
                    case 1:
                    case 5:
                    case 9:
                    case 13:
                        if (needed >= 1 || ran <= needed) vm.addDropBlock(new ItemStack(SAPLING, 1, (short) 5));
                        break;
                }
            }
        } else {
            vm.addRangeDropBlocks(bl.getDrops());
        }

        // http://minecraft.gamepedia.com/Enchanting#Fortune
        // http://minecraft-de.gamepedia.com/Verzauberung
    }

}
