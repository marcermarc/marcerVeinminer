package de.marcermarc.veinminer.objects;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

import static org.bukkit.Material.*;

public class VeinminerMining {
    //drops
    private Collection<ItemStack> dropBlocks;
    private int destroyedBlocks;

    //enchantments
    private int unbreaking;

    //other
    private Material type;
    private ItemStack holdItem;
    private Damageable meta;
    private Entity entity;
    //private Location location;

    public VeinminerMining(Material type, ItemStack holdItem, Entity entity) {
        this.type = type;
        this.holdItem = holdItem;
        this.meta = (Damageable) holdItem.getItemMeta();
        this.entity = entity;
        //this.location = location;

        this.dropBlocks = new ArrayList<>();

        //unbreaking
        if (holdItem.getType().equals(WOODEN_HOE) || holdItem.getType().equals(STONE_HOE) || holdItem.getType().equals(IRON_HOE) || holdItem.getType().equals(GOLDEN_HOE) || holdItem.getType().equals(DIAMOND_HOE)) {
            this.unbreaking = -1;
        } else if (holdItem.containsEnchantment(Enchantment.DURABILITY)) {
            this.unbreaking = holdItem.getEnchantmentLevel(Enchantment.DURABILITY);
        } else {
            this.unbreaking = 0;
        }
    }

    public void addDropsForOneBlock(Collection<ItemStack> drops) {
        for (ItemStack drop : drops) {
            addDropBlock(drop);
        }
        destroyedBlocks++;
    }

    private void addDropBlock(ItemStack drop) {
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

    //region getter and setter

    public Collection<ItemStack> getDropBlocks() {
        return dropBlocks;
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

    public Entity getEntity() {
        return entity;
    }

    public int getDestroyedBlocks() {
        return destroyedBlocks;
    }
    //endregion

}
