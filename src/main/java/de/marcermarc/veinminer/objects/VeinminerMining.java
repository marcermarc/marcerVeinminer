package de.marcermarc.veinminer.objects;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collection;

import static org.bukkit.Material.*;

public class VeinminerMining {
    //drops
    private Collection<ItemStack> dropBlocks;
    private int dropExperiance;

    //enchantments
    private int luckLevel;
    private boolean silkTouch;
    private int unbreaking;

    //other
    private MaterialType type;
    private ItemStack holdItem;
    //private Location location;

    public VeinminerMining(MaterialType type, ItemStack holdItem) {
        this.type = type;
        this.holdItem = holdItem;
        //this.location = location;

        this.dropBlocks = new ArrayList();
        this.dropExperiance = 0;

        //silktouck
        if (holdItem.getType().equals(SHEARS) && (
                type.getMaterial().equals(GRASS) ||
                        type.getMaterial().equals(LONG_GRASS) ||
                        type.getMaterial().equals(LEAVES) ||
                        type.getMaterial().equals(LEAVES_2)
        )) {
            this.silkTouch = true; //for shears true, because of the other drops
        } else {
            this.silkTouch = holdItem.containsEnchantment(Enchantment.SILK_TOUCH);
        }

        //luck
        if ((holdItem.containsEnchantment(Enchantment.LOOT_BONUS_BLOCKS) ||
                holdItem.containsEnchantment(Enchantment.LOOT_BONUS_MOBS)
        ) && (
                type.getMaterial().equals(COAL_ORE) ||
                        type.getMaterial().equals(DIAMOND_ORE) ||
                        type.getMaterial().equals(REDSTONE_ORE) ||
                        type.getMaterial().equals(LAPIS_ORE) ||
                        type.getMaterial().equals(QUARTZ_ORE) ||
                        type.getMaterial().equals(POTATO) ||
                        type.getMaterial().equals(CARROT) ||
                        type.getMaterial().equals(WHEAT) ||
                        type.getMaterial().equals(GRASS) ||
                        type.getMaterial().equals(EMERALD_ORE) ||
                        type.getMaterial().equals(GLOWSTONE) ||
                        type.getMaterial().equals(MELON_BLOCK) ||
                        type.getMaterial().equals(NETHER_WARTS) ||
                        type.getMaterial().equals(GRAVEL) ||
                        type.getMaterial().equals(LEAVES) ||
                        type.getMaterial().equals(LEAVES_2)
        )) {
            if (holdItem.containsEnchantment(Enchantment.LOOT_BONUS_BLOCKS)) {
                this.luckLevel = holdItem.getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS);
            } else if (holdItem.containsEnchantment(Enchantment.LOOT_BONUS_MOBS)) {
                this.luckLevel = holdItem.getEnchantmentLevel(Enchantment.LOOT_BONUS_MOBS);
            }
        } else {
            this.luckLevel = 0;
        }

        //unbreaking
        if (holdItem.getType().equals(WOOD_HOE) || holdItem.getType().equals(STONE_HOE) || holdItem.getType().equals(IRON_HOE) || holdItem.getType().equals(GOLD_HOE) || holdItem.getType().equals(DIAMOND_HOE)) {
            this.unbreaking = -1;
        } else if (holdItem.containsEnchantment(Enchantment.DURABILITY)) {
            this.unbreaking = holdItem.getEnchantmentLevel(Enchantment.DURABILITY);
        } else {
            this.unbreaking = 0;
        }
    }

    public void addDropBlock(ItemStack drop) {
        boolean success = false;
        for (ItemStack itemS : this.dropBlocks) {
            if (drop.getType().equals(itemS) && drop.getData() == itemS.getData()) {
                itemS.setAmount(itemS.getAmount() + drop.getAmount());
                success = true;
                break;
            }
        }
        if (!success) {
            this.dropBlocks.add(drop);
        }
    }

    public void addRangeDropBlocks(Collection<ItemStack> drops) {
        for (ItemStack drop : drops) {
            addDropBlock(drop);
        }
    }

    public void addDropExperiance(int drop) {
        this.dropExperiance += drop;
    }

    //region getter and setter

    public Collection<ItemStack> getDropBlocks() {
        return dropBlocks;
    }

    public int getDropExperiance() {
        return dropExperiance;
    }

    public int getLuckLevel() {
        return luckLevel;
    }

    public boolean isSilktouch() {
        return silkTouch;
    }

    public int getUnbreaking() {
        return unbreaking;
    }

    public MaterialType getType() {
        return type;
    }

    public ItemStack getHoldItem() {
        return holdItem;
    }


    //endregion

}
