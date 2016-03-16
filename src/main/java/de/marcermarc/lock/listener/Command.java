package de.marcermarc.lock.listener;

import de.marcermarc.lock.controller.PluginController;
import de.marcermarc.lock.objects.MaterialType;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class Command implements CommandExecutor {

    private PluginController controller;

    public Command(PluginController controller) {
        this.controller = controller;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, org.bukkit.command.Command command, String s, String[] args) {

        if (args.length >= 1) {
            boolean b;
            switch (args[0].toLowerCase()) {
                case "enable":
                case "e":

                    if (commandSender instanceof Player) {
                        controller.getConfig().getEnabledPlayers().add((Player) commandSender);
                        commandSender.sendMessage("marcerVeinminer enabled");
                    } else {
                        commandSender.sendMessage("Its a Player only command");
                    }
                    return true;

                case "disable":
                case "d":

                    if (commandSender instanceof Player) {
                        controller.getConfig().getEnabledPlayers().remove((Player) commandSender);
                        commandSender.sendMessage("marcerVeinminer disabled");
                    } else {
                        commandSender.sendMessage("Its a Player only command");
                    }
                    return true;

                case "saveblocks":
                    b = controller.getConfig().saveBlocks();
                    if (b) {
                        commandSender.sendMessage(ChatColor.GREEN + "Saved config.");
                    } else {
                        commandSender.sendMessage(ChatColor.RED + "Failed saveBlocks config!");
                    }
                    return b;
                case "loadblocks":
                    b = controller.getConfig().loadBlocks();
                    if (b) {
                        commandSender.sendMessage(ChatColor.GREEN + "Loaded config.");
                    } else {
                        commandSender.sendMessage(ChatColor.RED + "Failed loadBlocks config!");
                    }
                    return b;
                case "lists":
                    return lists(commandSender, args);
            }
        }
        return false;
    }

    private boolean lists(CommandSender commandSender, String[] args) {
        if ((args.length == 4 || args.length == 5) && (args[1].equalsIgnoreCase("add") || args[1].equalsIgnoreCase("remove"))) {
            if (args[2].equalsIgnoreCase("pickaxe")) {
                lists(commandSender, args, controller.getConfig().getPickaxe());
            } else if (args[2].equalsIgnoreCase("axe")) {
                lists(commandSender, args, controller.getConfig().getAxe());
            } else if (args[2].equalsIgnoreCase("spade")) {
                lists(commandSender, args, controller.getConfig().getSpade());
            } else if (args[2].equalsIgnoreCase("hoe")) {
                lists(commandSender, args, controller.getConfig().getHoe());
            } else if (args[2].equalsIgnoreCase("sword")) {
                lists(commandSender, args, controller.getConfig().getSword());
            } else if (args[2].equalsIgnoreCase("shears")) {
                lists(commandSender, args, controller.getConfig().getShears());
            } else {
                commandSender.sendMessage(ChatColor.RED + "Please use one of the following tools: pickaxe, axe, spade, hoe, sword or shears.");
            }
            return true;
        }
        return false;
    }

    private void lists(CommandSender commandSender, String args[], List<MaterialType> lMat) {
        MaterialType mt;
        if (args.length == 4) {
            mt = new MaterialType(Material.getMaterial(args[3].toUpperCase()));
        } else {
            mt = new MaterialType(Material.getMaterial(args[3].toUpperCase()), Integer.parseInt(args[4]));
        }

        if (mt == null) {
            commandSender.sendMessage(ChatColor.RED + "Please input a valid blockname.");
        } else if (!mt.getMaterial().isBlock()) {
            commandSender.sendMessage(ChatColor.RED + "Please choose a block, not an item.");
        } else if (args[1].equalsIgnoreCase("add")) {
            if (lMat.contains(mt)) {
                commandSender.sendMessage(ChatColor.GREEN + "Block is already in the list.");
            } else {
                commandSender.sendMessage(ChatColor.GREEN + "Added the block. Don't forget to saveBlocks!");
                lMat.add(mt);
            }
        } else if (args[1].equalsIgnoreCase("remove")) {
            if (lMat.contains(mt)) {
                commandSender.sendMessage(ChatColor.GREEN + "Removed Block. Don't forget to saveBlocks!");
                lMat.remove(mt);
            } else {
                commandSender.sendMessage(ChatColor.GREEN + "Block is not on the list.");
            }
        }
    }
}