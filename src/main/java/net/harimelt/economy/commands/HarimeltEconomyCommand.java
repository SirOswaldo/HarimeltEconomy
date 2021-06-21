package net.harimelt.economy.commands;

import net.harimelt.economy.HarimeltEconomy;
import net.harimelt.economy.util.command.SimpleCommand;
import net.harimelt.economy.util.yaml.Yaml;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class HarimeltEconomyCommand extends SimpleCommand {

    private final HarimeltEconomy harimeltEconomy;

    public HarimeltEconomyCommand(HarimeltEconomy harimeltEconomy) {
        super(harimeltEconomy, "HarimeltEconomy");
        this.harimeltEconomy = harimeltEconomy;
    }

    @Override
    public boolean onPlayerExecute(Player player, Command command, String[] arguments) {
        Yaml messages = harimeltEconomy.getMessages();
        if (player.hasPermission("harimelt.admin.economy")) {
            if (arguments.length > 0) {
                switch (arguments[0].toLowerCase()) {
                    case "help":
                        messages.sendMessage(player, "Admin.help", new String[][]{{"%command%", command.getName()}});
                        break;
                    case "reload":
                        harimeltEconomy.getConfiguration().reloadFileConfiguration();
                        harimeltEconomy.getMessages().reloadFileConfiguration();
                        messages.sendMessage(player, "Admin.reloadComplete");
                        break;
                    case "version":
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aHarimelt&2Tags &8» &fInformación del Complemento"));
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&fVersión: " + harimeltEconomy.getDescription().getVersion()));
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&fAutores: " + harimeltEconomy.getDescription().getAuthors()));
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&fWeb: " + harimeltEconomy.getDescription().getWebsite()));
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&a&m                                                "));
                        break;
                    default:
                        messages.sendMessage(player, "Admin.optionInvalid");
                }
            } else {
                messages.sendMessage(player, "Admin.optionEmpty");
            }
        } else {
            messages.sendMessage(player, "Admin.noPermission");
        }
        return true;
    }

    @Override
    public boolean onConsoleExecute(ConsoleCommandSender console, Command command, String[] arguments) {
        Yaml messages = harimeltEconomy.getMessages();
        if (arguments.length > 0) {
            switch (arguments[0].toLowerCase()) {
                case "help":
                    messages.sendMessage(console, "Admin.help", new String[][] {{"%command%", command.getName()}});
                    break;
                case "reload":
                    harimeltEconomy.getConfiguration().reloadFileConfiguration();
                    harimeltEconomy.getMessages().reloadFileConfiguration();
                    messages.sendMessage(console, "Admin.reloadComplete");
                    break;
                case "version":
                    console.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aHarimelt&2Tags &8» &fInformación del Complemento"));
                    console.sendMessage(ChatColor.translateAlternateColorCodes('&', "&fVersión: " + harimeltEconomy.getDescription().getVersion()));
                    console.sendMessage(ChatColor.translateAlternateColorCodes('&', "&fAutores: " + harimeltEconomy.getDescription().getAuthors()));
                    console.sendMessage(ChatColor.translateAlternateColorCodes('&', "&fWeb: " + harimeltEconomy.getDescription().getWebsite()));
                    console.sendMessage(ChatColor.translateAlternateColorCodes('&', "&a&m                                                "));
                    break;
                default:
                    messages.sendMessage(console, "Admin.optionInvalid");
            }
        } else {
            messages.sendMessage(console, "Admin.optionEmpty");
        }
        return true;
    }

    @Override
    public List<String> onPlayerTabComplete(Player player, Command command, String[] arguments) {
        List<String> completer = new ArrayList<>();
        if (player.hasPermission("harimelt.admin.economy")) {
            if (arguments.length == 1) {
                completer.add("help");
                completer.add("reload");
                completer.add("version");
            }
        }
        return completer;
    }

    @Override
    public List<String> onConsoleTabComplete(ConsoleCommandSender console, Command command, String[] arguments) {
        List<String> completer = new ArrayList<>();
        if (arguments.length == 1) {
            completer.add("help");
            completer.add("reload");
            completer.add("version");
        }
        return completer;
    }
}