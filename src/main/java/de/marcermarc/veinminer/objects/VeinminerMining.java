package de.marcermarc.veinminer.objects;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

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
    private Material type;
    private ItemStack holdItem;
    private Damageable meta;
    //private Location location;

    public VeinminerMining(Material type, ItemStack holdItem) {
        this.type = type;
        this.holdItem = holdItem;
        this.meta = (Damageable) holdItem.getItemMeta();
        //this.location = location;

        this.dropBlocks = new ArrayList<>();
        this.dropExperiance = 0;

        //silktouck
        if (holdItem.getType().equals(SHEARS)
                && (type == GRASS
                || type == TALL_GRASS
                || type == OAK_LEAVES
                || type == JUNGLE_LEAVES
                || type == DARK_OAK_LEAVES
                || type == BIRCH_LEAVES
                || type == SPRUCE_LEAVES
                || type == ACACIA_LEAVES
        )) {
            this.silkTouch = true; //for shears true, because of the other drops
        } else {
            this.silkTouch = holdItem.containsEnchantment(Enchantment.SILK_TOUCH);
        }

        //luck
        if (holdItem.containsEnchantment(Enchantment.LOOT_BONUS_BLOCKS)) {
            this.luckLevel = holdItem.getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS);
        } else if (holdItem.containsEnchantment(Enchantment.LOOT_BONUS_MOBS)) {
            this.luckLevel = holdItem.getEnchantmentLevel(Enchantment.LOOT_BONUS_MOBS);
        } else {
            this.luckLevel = 0;
        }

        //unbreaking
        if (holdItem.getType().equals(WOODEN_HOE) || holdItem.getType().equals(STONE_HOE) || holdItem.getType().equals(IRON_HOE) || holdItem.getType().equals(GOLDEN_HOE) || holdItem.getType().equals(DIAMOND_HOE)) {
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
            if (drop.getType() == itemS.getType() && Objects.equals(drop.getData(), itemS.getData())) {
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

    public Material getType() {
        return type;
    }

    public ItemStack getHoldItem() {
        return holdItem;
    }

    public Damageable getMeta() {
        return meta;
    }

//endregion

}
