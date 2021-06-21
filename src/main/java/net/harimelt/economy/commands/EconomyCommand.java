package net.harimelt.economy.commands;

import net.harimelt.economy.HarimeltEconomy;
import net.harimelt.economy.util.command.SimpleCommand;
import net.harimelt.economy.util.yaml.Yaml;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.command.Command;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class EconomyCommand extends SimpleCommand {

    private final HarimeltEconomy harimeltEconomy;

    public EconomyCommand(HarimeltEconomy harimeltEconomy) {
        super(harimeltEconomy, "Economy");
        this.harimeltEconomy = harimeltEconomy;
    }

    @Override
    public boolean onPlayerExecute(Player player, Command command, String[] arguments) {
        Yaml messages = harimeltEconomy.getMessages();
        if (player.hasPermission("harimelt.economy")) {
            if (arguments.length > 0) {
                switch (arguments[0].toLowerCase()) {
                    case "help":
                        messages.sendMessage(player, "Economy.help", new String[][] {{"%command%", command.getName()}});
                        break;
                    case "give":
                        if (arguments.length > 1) {
                            if (arguments.length > 2) {
                                String targetName = arguments[1];
                                Player targetPlayer = harimeltEconomy.getServer().getPlayerExact(targetName);
                                if (targetPlayer != null) {
                                    String amountString = arguments[2];
                                    if (amountString.contains(".")) {
                                        amountString = amountString.split("\\.")[0];
                                    }
                                    if (amountString.contains(",")) {
                                        amountString = amountString.split(",")[0];
                                    }
                                    try {
                                        double amount = Double.parseDouble(amountString);
                                        // Action
                                        Economy economy = harimeltEconomy.getEconomy();
                                        EconomyResponse economyResponse = economy.depositPlayer(targetPlayer, amount);
                                        if (economyResponse.transactionSuccess()) {
                                            messages.sendMessage(player, "Economy.GivePlayerComplete", new String[][] {{"%player%", targetName}, {"%amount%", amountString}});
                                            messages.sendMessage(targetPlayer, "Economy.GivePlayerCompleteNotify", new String[][] {{"%amount%", amountString}});
                                        } else {
                                            messages.sendMessage(player, "Economy.TakePlayerError", new String[][] {{"%error%", economyResponse.errorMessage}});
                                        }
                                    } catch (NumberFormatException e) {
                                        messages.sendMessage(player, "Economy.amountInvalid", new String[][] {{"%amount%", amountString}});
                                    }
                                } else {
                                    messages.sendMessage(player, "Economy.playerNameInvalid", new String[][] {{"%playerName%", targetName}});
                                }
                            } else {
                                messages.sendMessage(player, "Economy.amountEmpty");
                            }
                        } else {
                            messages.sendMessage(player, "Economy.playerNameEmpty");
                        }
                        break;
                    case "take":
                        if (arguments.length > 1) {
                            if (arguments.length > 2) {
                                String targetName = arguments[1];
                                Player targetPlayer = harimeltEconomy.getServer().getPlayerExact(targetName);
                                if (targetPlayer != null) {
                                    String amountString = arguments[2];
                                    if (amountString.contains(".")) {
                                        amountString = amountString.split("\\.")[0];
                                    }
                                    if (amountString.contains(",")) {
                                        amountString = amountString.split(",")[0];
                                    }
                                    try {
                                        double amount = Double.parseDouble(amountString);
                                        // Action
                                        Economy economy = harimeltEconomy.getEconomy();
                                        EconomyResponse economyResponse = economy.withdrawPlayer(targetPlayer, amount);
                                        if (economyResponse.transactionSuccess()) {
                                            messages.sendMessage(player, "Economy.TakePlayerComplete", new String[][] {{"%player%", targetName}, {"%amount%", amountString}});
                                            messages.sendMessage(targetPlayer, "Economy.TakePlayerCompleteNotify", new String[][] {{"%amount%", amountString}});
                                        } else {
                                            messages.sendMessage(player, "Economy.TakePlayerError", new String[][] {{"%error%", economyResponse.errorMessage}});
                                        }
                                    } catch (NumberFormatException e) {
                                        messages.sendMessage(player, "Economy.amountInvalid", new String[][] {{"%amount%", amountString}});
                                    }
                                } else {
                                    messages.sendMessage(player, "Economy.playerNameInvalid", new String[][] {{"%playerName%", targetName}});
                                }
                            } else {
                                messages.sendMessage(player, "Economy.amountEmpty");
                            }
                        } else {
                            messages.sendMessage(player, "Economy.playerNameEmpty");
                        }
                        break;
                    case "set":
                        if (arguments.length > 1) {
                            if (arguments.length > 2) {
                                String targetName = arguments[1];
                                Player targetPlayer = harimeltEconomy.getServer().getPlayerExact(targetName);
                                if (targetPlayer != null) {
                                    String amountString = arguments[2];
                                    if (amountString.contains(".")) {
                                        amountString = amountString.split("\\.")[0];
                                    }
                                    if (amountString.contains(",")) {
                                        amountString = amountString.split(",")[0];
                                    }
                                    try {
                                        double amount = Double.parseDouble(amountString);
                                        // Action
                                        Economy economy = harimeltEconomy.getEconomy();
                                        economy.withdrawPlayer(targetPlayer, economy.getBalance(targetPlayer));
                                        economy.depositPlayer(targetPlayer, amount);
                                        messages.sendMessage(player, "Economy.SetPlayerComplete", new String[][] {{"%player%", targetName}, {"%amount%", amountString}});
                                        messages.sendMessage(targetPlayer, "Economy.SetPlayerCompleteNotify", new String[][] {{"%amount%", amountString}});
                                    } catch (NumberFormatException e) {
                                        messages.sendMessage(player, "Economy.amountInvalid", new String[][] {{"%amount%", amountString}});
                                    }
                                } else {
                                    messages.sendMessage(player, "Economy.playerNameInvalid", new String[][] {{"%playerName%", targetName}});
                                }
                            } else {
                                messages.sendMessage(player, "Economy.amountEmpty");
                            }
                        } else {
                            messages.sendMessage(player, "Economy.playerNameEmpty");
                        }
                        break;
                    default:
                        messages.sendMessage(player, "Economy.optionInvalid", new String[][] {{"%option%", arguments[0]}});
                }
            } else {
                messages.sendMessage(player, "Economy.optionEmpty");
            }
        } else {
            messages.sendMessage(player, "Economy.noPermission");
        }
        return true;
    }

    @Override
    public boolean onConsoleExecute(ConsoleCommandSender console, Command command, String[] arguments) {
        Yaml messages = harimeltEconomy.getMessages();
        if (arguments.length > 0) {
            switch (arguments[0].toLowerCase()) {
                case "help":
                    messages.sendMessage(console, "Economy.help", new String[][] {{"%command%", command.getName()}});
                    break;
                case "give":
                    if (arguments.length > 1) {
                        if (arguments.length > 2) {
                            String targetName = arguments[1];
                            Player targetPlayer = harimeltEconomy.getServer().getPlayerExact(targetName);
                            if (targetPlayer != null) {
                                String amountString = arguments[2];
                                if (amountString.contains(".")) {
                                    amountString = amountString.split("\\.")[0];
                                }
                                if (amountString.contains(",")) {
                                    amountString = amountString.split(",")[0];
                                }
                                try {
                                    double amount = Double.parseDouble(amountString);
                                    // Action
                                    Economy economy = harimeltEconomy.getEconomy();
                                    EconomyResponse economyResponse = economy.depositPlayer(targetPlayer, amount);
                                    if (economyResponse.transactionSuccess()) {
                                        messages.sendMessage(console, "Economy.GivePlayerComplete", new String[][] {{"%player%", targetName}, {"%amount%", amountString}});
                                        messages.sendMessage(targetPlayer, "Economy.GivePlayerCompleteNotify", new String[][] {{"%amount%", amountString}});
                                    } else {
                                        messages.sendMessage(console, "Economy.TakePlayerError", new String[][] {{"%error%", economyResponse.errorMessage}});
                                    }
                                } catch (NumberFormatException e) {
                                    messages.sendMessage(console, "Economy.amountInvalid", new String[][] {{"%amount%", amountString}});
                                }
                            } else {
                                messages.sendMessage(console, "Economy.playerNameInvalid", new String[][] {{"%playerName%", targetName}});
                            }
                        } else {
                            messages.sendMessage(console, "Economy.amountEmpty");
                        }
                    } else {
                        messages.sendMessage(console, "Economy.playerNameEmpty");
                    }
                    break;
                case "take":
                    if (arguments.length > 1) {
                        if (arguments.length > 2) {
                            String targetName = arguments[1];
                            Player targetPlayer = harimeltEconomy.getServer().getPlayerExact(targetName);
                            if (targetPlayer != null) {
                                String amountString = arguments[2];
                                if (amountString.contains(".")) {
                                    amountString = amountString.split("\\.")[0];
                                }
                                if (amountString.contains(",")) {
                                    amountString = amountString.split(",")[0];
                                }
                                try {
                                    double amount = Double.parseDouble(amountString);
                                    // Action
                                    Economy economy = harimeltEconomy.getEconomy();
                                    EconomyResponse economyResponse = economy.withdrawPlayer(targetPlayer, amount);
                                    if (economyResponse.transactionSuccess()) {
                                        messages.sendMessage(console, "Economy.TakePlayerComplete", new String[][] {{"%player%", targetName}, {"%amount%", amountString}});
                                        messages.sendMessage(targetPlayer, "Economy.TakePlayerCompleteNotify", new String[][] {{"%amount%", amountString}});
                                    } else {
                                        messages.sendMessage(console, "Economy.TakePlayerError", new String[][] {{"%error%", economyResponse.errorMessage}});
                                    }
                                } catch (NumberFormatException e) {
                                    messages.sendMessage(console, "Economy.amountInvalid", new String[][] {{"%amount%", amountString}});
                                }
                            } else {
                                messages.sendMessage(console, "Economy.playerNameInvalid", new String[][] {{"%playerName%", targetName}});
                            }
                        } else {
                            messages.sendMessage(console, "Economy.amountEmpty");
                        }
                    } else {
                        messages.sendMessage(console, "Economy.playerNameEmpty");
                    }
                    break;
                case "set":
                    if (arguments.length > 1) {
                        if (arguments.length > 2) {
                            String targetName = arguments[1];
                            Player targetPlayer = harimeltEconomy.getServer().getPlayerExact(targetName);
                            if (targetPlayer != null) {
                                String amountString = arguments[2];
                                if (amountString.contains(".")) {
                                    amountString = amountString.split("\\.")[0];
                                }
                                if (amountString.contains(",")) {
                                    amountString = amountString.split(",")[0];
                                }
                                try {
                                    double amount = Double.parseDouble(amountString);
                                    // Action
                                    Economy economy = harimeltEconomy.getEconomy();
                                    economy.withdrawPlayer(targetPlayer, economy.getBalance(targetPlayer));
                                    economy.depositPlayer(targetPlayer, amount);
                                    messages.sendMessage(console, "Economy.SetPlayerComplete", new String[][] {{"%player%", targetName}, {"%amount%", amountString}});
                                    messages.sendMessage(targetPlayer, "Economy.SetPlayerCompleteNotify", new String[][] {{"%amount%", amountString}});
                                } catch (NumberFormatException e) {
                                    messages.sendMessage(console, "Economy.amountInvalid", new String[][] {{"%amount%", amountString}});
                                }
                            } else {
                                messages.sendMessage(console, "Economy.playerNameInvalid", new String[][] {{"%playerName%", targetName}});
                            }
                        } else {
                            messages.sendMessage(console, "Economy.amountEmpty");
                        }
                    } else {
                        messages.sendMessage(console, "Economy.playerNameEmpty");
                    }
                    break;
                default:
                    messages.sendMessage(console, "Economy.optionInvalid", new String[][] {{"%option%", arguments[0]}});
            }
        } else {
            messages.sendMessage(console, "Economy.optionEmpty");
        }
        return true;
    }

    @Override
    public List<String> onPlayerTabComplete(Player player, Command command, String[] arguments) {
        List<String> complete = new ArrayList<>();
        if (player.hasPermission("harimelt.economy")) {
            if (arguments.length == 1) {
                complete.add("give");
                complete.add("take");
                complete.add("set");
                complete.add("help");
            }
            if (arguments.length == 2) {
                for (Player p:harimeltEconomy.getServer().getOnlinePlayers()) {
                    complete.add(p.getName());
                }
            }
            if (arguments.length == 3) {
                complete.add("Coloque un numero entero");
            }
        }
        return complete;
    }

    @Override
    public List<String> onConsoleTabComplete(ConsoleCommandSender console, Command command, String[] arguments) {
        List<String> complete = new ArrayList<>();
        if (arguments.length == 1) {
            complete.add("give");
            complete.add("take");
            complete.add("set");
            complete.add("help");
        }
        if (arguments.length == 2) {
            for (Player p:harimeltEconomy.getServer().getOnlinePlayers()) {
                complete.add(p.getName());
            }
        }
        if (arguments.length == 3) {
            complete.add("Coloque un numero entero");
        }
        return complete;
    }
}