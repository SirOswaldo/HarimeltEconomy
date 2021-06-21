package net.harimelt.economy.commands;

import net.harimelt.economy.HarimeltEconomy;
import net.harimelt.economy.util.command.SimpleCommand;
import net.harimelt.economy.util.yaml.Yaml;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.command.Command;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class BalanceCommand extends SimpleCommand {

    private final HarimeltEconomy harimeltEconomy;

    public BalanceCommand(HarimeltEconomy harimeltEconomy) {
        super(harimeltEconomy, "Balance");
        this.harimeltEconomy = harimeltEconomy;
    }

    @Override
    public boolean onPlayerExecute(Player player, Command command, String[] arguments) {
        Yaml messages = harimeltEconomy.getMessages();
        if (player.hasPermission("harimelt.balance")) {
            if (arguments.length > 0) {
                String targetName = arguments[0];
                Player targetPlayer = harimeltEconomy.getServer().getPlayerExact(targetName);
                if (targetPlayer != null) {
                    if (targetPlayer != player) {
                        if (player.hasPermission("harimelt.other.balance")) {
                            Economy economy = harimeltEconomy.getEconomy();
                            messages.sendMessage(player, "Balance.otherBalance", new String[][] {
                                    {"%player%", targetName}, {"%balance%", economy.getBalance(targetPlayer) + ""}
                            });
                        } else {
                            messages.sendMessage(player, "Balance.noPermissionOther");
                        }
                    } else {
                        Economy economy = harimeltEconomy.getEconomy();
                        messages.sendMessage(player, "Balance.ownBalance", new String[][] {
                                {"%balance%", economy.getBalance(player) + ""}
                        });
                    }
                } else {
                    messages.sendMessage(player, "Balance.playerNameInvalid", new String[][] {
                            {"%playerName%", targetName}
                    });
                }
            } else {
                Economy economy = harimeltEconomy.getEconomy();
                messages.sendMessage(player, "Balance.ownBalance", new String[][] {
                        {"%balance%", economy.getBalance(player) + ""}
                });
            }
        } else {
            messages.sendMessage(player, "Balance.noPermission");
        }
        return true;
    }

    @Override
    public boolean onConsoleExecute(ConsoleCommandSender console, Command command, String[] arguments) {
        Yaml messages = harimeltEconomy.getMessages();
        if (arguments.length > 0) {
            String targetName = arguments[0];
            Player targetPlayer = harimeltEconomy.getServer().getPlayerExact(targetName);
            if (targetPlayer != null) {
                Economy economy = harimeltEconomy.getEconomy();
                messages.sendMessage(console, "Balance.otherBalance", new String[][] {
                        {"%player%", targetName}, {"%balance%", economy.getBalance(targetPlayer) + ""}
                });
            } else {
                messages.sendMessage(console, "Balance.playerNameInvalid", new String[][] {
                        {"%player%"}
                });
            }
        } else {
            messages.sendMessage(console, "Balance.playerNameEmpty");
        }
        return true;
    }

    @Override
    public List<String> onPlayerTabComplete(Player player, Command command, String[] arguments) {
        List<String> names = new ArrayList<>();
        if (arguments.length == 1) {
            for (Player p:harimeltEconomy.getServer().getOnlinePlayers()) {
                names.add(p.getName());
            }
        }
        return names;
    }

    @Override
    public List<String> onConsoleTabComplete(ConsoleCommandSender console, Command command, String[] arguments) {
        List<String> names = new ArrayList<>();
        if (arguments.length == 0) {
            for (Player p:harimeltEconomy.getServer().getOnlinePlayers()) {
                names.add(p.getName());
            }
        }
        return names;
    }
}