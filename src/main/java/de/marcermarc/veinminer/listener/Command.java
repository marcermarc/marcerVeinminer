package de.marcermarc.veinminer.listener;

import de.marcermarc.veinminer.Util;
import de.marcermarc.veinminer.controller.PluginController;
import de.marcermarc.veinminer.objects.Tool;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Command implements CommandExecutor, TabCompleter, Listener {
    private static final String ENABLE = "enable";
    private static final String DISABLE = "disable";
    private static final String SAVE_BLOCKS = "saveblocks";
    private static final String LOAD_BLOCKS = "loadblocks";
    private static final String LISTS = "lists";

    private static final String ADD = "add";
    private static final String REMOVE = "remove";

    private static final String MESSAGE_PREFIX = ChatColor.DARK_GREEN + "[marcerVeinminer]" + ChatColor.RESET + " ";

    private static final String MESSAGE_ENABLE = MESSAGE_PREFIX + ChatColor.GREEN + "Veinminer enabled, mine a valid block sneaking to use it!";
    private static final String MESSAGE_DISABLE = MESSAGE_PREFIX + ChatColor.GREEN + "Veinminer disabled";
    private static final String MESSAGE_PLAYER_ONLY = MESSAGE_PREFIX + ChatColor.RED + "Its a Player only command";
    private static final String MESSAGE_NO_PERMISSION = MESSAGE_PREFIX + ChatColor.RED + "You have no permission for this command";
    private static final String MESSAGE_LISTS_ARGS = MESSAGE_PREFIX + ChatColor.RED + "Command needs 4 arguments.";
    private static final String MESSAGE_LISTS_ARG2 = MESSAGE_PREFIX + ChatColor.RED + "Please use a valid tool.";
    private static final String MESSAGE_LISTS_ARG3 = MESSAGE_PREFIX + ChatColor.RED + "Please use 'add' or 'remove'.";
    private static final String MESSAGE_LISTS_ARG4 = MESSAGE_PREFIX + ChatColor.RED + "This block does not exists.";
    private static final String MESSAGE_LISTS_ADD_ALREDY_IN = MESSAGE_PREFIX + ChatColor.GREEN + "Block is already in this list.";
    private static final String MESSAGE_LISTS_ADD_SUCCESS = MESSAGE_PREFIX + ChatColor.GREEN + "Block added, don't forget to save.";
    private static final String MESSAGE_LISTS_REMOVE_NOT_IN = MESSAGE_PREFIX + ChatColor.GREEN + "Block is not in this list.";
    private static final String MESSAGE_LISTS_REMOVE_SUCCESS = MESSAGE_PREFIX + ChatColor.GREEN + "Block removed, don't forget to save.";
    private static final String MESSAGE_SAVE = MESSAGE_PREFIX + ChatColor.GREEN + "Saved config.";
    private static final String MESSAGE_SAVE_FAILED = ChatColor.RED + "Failed to save the config!";
    private static final String MESSAGE_LOAD = MESSAGE_PREFIX + ChatColor.GREEN + "Loaded config.";
    private static final String MESSAGE_LOAD_FAILED = ChatColor.RED + "Failed to load the config!";

    private static final List<String> ARG1_NO_OP = Arrays.asList(ENABLE, DISABLE);
    private static final List<String> ARG1_OP = Arrays.asList(ENABLE, DISABLE, SAVE_BLOCKS, LOAD_BLOCKS, LISTS);
    private static final List<String> ARG1_CONSOLE = Arrays.asList(SAVE_BLOCKS, LOAD_BLOCKS, LISTS);
    private static final List<String> ARG2_LISTS = Stream.of(Tool.values()).map(Tool::toString).collect(Collectors.toList());
    private static final List<String> ARG3_LISTS = Arrays.asList(ADD, REMOVE);


    private PluginController controller;

    public Command(PluginController controller) {
        this.controller = controller;
    }

    //region CommandExecutor
    @Override
    public boolean onCommand(CommandSender commandSender, org.bukkit.command.Command command, String s, String[] args) {

        if (args.length >= 1) {
            switch (args[0].toLowerCase()) {
                case ENABLE:
                case "e":

                    if (commandSender instanceof Player) {
                        controller.getConfig().getEnabledPlayers().add((Player) commandSender);
                        commandSender.sendMessage(MESSAGE_ENABLE);
                    } else {
                        commandSender.sendMessage(MESSAGE_PLAYER_ONLY);
                    }
                    return true;

                case DISABLE:
                case "d":

                    if (commandSender instanceof Player) {
                        controller.getConfig().getEnabledPlayers().remove(commandSender);
                        commandSender.sendMessage(MESSAGE_DISABLE);
                    } else {
                        commandSender.sendMessage(MESSAGE_PLAYER_ONLY);
                    }
                    return true;

                case SAVE_BLOCKS:
                    if (!commandSender.isOp()) {
                        commandSender.sendMessage(MESSAGE_NO_PERMISSION);
                        return false;
                    } else if (controller.getConfig().saveConfig()) {
                        commandSender.sendMessage(MESSAGE_SAVE);
                        return true;
                    } else {
                        commandSender.sendMessage(MESSAGE_SAVE_FAILED);
                        return false;
                    }


                case LOAD_BLOCKS:
                    if (!commandSender.isOp()) {
                        commandSender.sendMessage(MESSAGE_NO_PERMISSION);
                        return false;
                    } else if (controller.getConfig().loadConfig()) {
                        commandSender.sendMessage(MESSAGE_LOAD);
                        return true;
                    } else {
                        commandSender.sendMessage(MESSAGE_LOAD_FAILED);
                        return false;
                    }

                case LISTS:
                    if (!commandSender.isOp()) {
                        commandSender.sendMessage(MESSAGE_NO_PERMISSION);
                        return false;
                    } else if (args.length != 4)
                        commandSender.sendMessage(MESSAGE_LISTS_ARGS);

                    return lists(commandSender, args);
            }
        }
        return false;
    }

    private boolean lists(CommandSender commandSender, String[] args) {
        if (!ARG2_LISTS.contains(args[1])) {
            commandSender.sendMessage(MESSAGE_LISTS_ARG2);
        } else if (!ARG3_LISTS.contains(args[2])) {
            commandSender.sendMessage(MESSAGE_LISTS_ARG3);
        } else {

            Material material = Util.stringToMaterial(args[3]);
            if (material == null || !material.isBlock()) {
                commandSender.sendMessage(MESSAGE_LISTS_ARG4);
                return false;
            }

            Tool tool = Tool.getByName(args[1]);

            if (args[2].equalsIgnoreCase(ADD)) {
                if (tool.getVeinminerMaterials().contains(material)) {
                    commandSender.sendMessage(MESSAGE_LISTS_ADD_ALREDY_IN);
                } else {
                    tool.getVeinminerMaterials().add(material);
                    commandSender.sendMessage(MESSAGE_LISTS_ADD_SUCCESS);
                }
            } else if (args[2].equalsIgnoreCase(REMOVE)) {
                if (tool.getVeinminerMaterials().contains(material)) {
                    tool.getVeinminerMaterials().remove(material);
                    commandSender.sendMessage(MESSAGE_LISTS_REMOVE_SUCCESS);
                } else {
                    commandSender.sendMessage(MESSAGE_LISTS_REMOVE_NOT_IN);
                }
            }
            return true;
        }
        return false;
    }
    //endregion

    //region TabComplete

    @Override
    public List<String> onTabComplete(CommandSender sender, org.bukkit.command.Command command, String alias, String[] args) {
        switch (args.length) {
            case 1:
                return onTabCompleteArg1(sender, args);
            case 2:
                return onTabCompleteArg2(sender, args);
            case 3:
                return onTabCompleteArg3(sender, args);
            case 4:
                return onTabCompleteArg4(sender, args);
            default:
                return null;
        }
    }

    private List<String> onTabCompleteArg1(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            return Util.tabCompleteFilter(ARG1_CONSOLE, args[0]);
        } else if (sender.isOp()) {
            return Util.tabCompleteFilter(ARG1_OP, args[0]);
        } else {
            return Util.tabCompleteFilter(ARG1_NO_OP, args[0]);
        }
    }

    private List<String> onTabCompleteArg2(CommandSender sender, String[] args) {
        if (args[0].equals(LISTS) && sender.isOp()) {
            return Util.tabCompleteFilter(ARG2_LISTS, args[1]);
        }
        return null;
    }

    private List<String> onTabCompleteArg3(CommandSender sender, String[] args) {
        if (args[0].equals(LISTS) && ARG2_LISTS.contains(args[1]) && sender.isOp()) {
            return Util.tabCompleteFilter(ARG3_LISTS, args[2]);
        }
        return null;
    }

    private List<String> onTabCompleteArg4(CommandSender sender, String[] args) {
        if (args[0].equals(LISTS) && ARG2_LISTS.contains(args[1]) && ARG3_LISTS.contains(args[2]) && sender.isOp()) {
            if (args[2].equals(ADD)) {
                return Util.tabCompleteFilter(
                        Stream.of(Material.values())
                                .filter(Material::isBlock)
                                .filter(material -> !Tool.getByName(args[1]).getVeinminerMaterials().contains(material))
                                .map(Util::materialToString)
                                .collect(Collectors.toList()),
                        args[3]);

            } else if (args[2].equals(REMOVE)) {
                return Util.tabCompleteFilter(
                        Stream.of(Material.values())
                                .filter(Material::isBlock)
                                .filter(material -> Tool.getByName(args[1]).getVeinminerMaterials().contains(material))
                                .map(Util::materialToString)
                                .collect(Collectors.toList()),
                        args[3]);
            }
        }
        return null;
    }

    //endregion
}