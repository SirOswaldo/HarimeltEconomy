/*
 *   Copyright (C) 2021 SirOswaldo
 *
 *      This program is free software: you can redistribute it and/or modify
 *      it under the terms of the GNU General Public License as published by
 *      the Free Software Foundation, either version 3 of the License, or
 *      (at your option) any later version.
 *
 *      This program is distributed in the hope that it will be useful,
 *      but WITHOUT ANY WARRANTY; without even the implied warranty of
 *      MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *      GNU General Public License for more details.
 *
 *      You should have received a copy of the GNU General Public License
 *      along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package net.harimelt.economy.commands;

import net.harimelt.economy.HarimeltEconomy;
import net.harimelt.economy.util.command.SimpleCommand;
import net.harimelt.economy.util.yaml.Yaml;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.command.Command;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.*;

public class PayCommand extends SimpleCommand {

    private final HarimeltEconomy harimeltEconomy;

    public PayCommand(HarimeltEconomy harimeltEconomy) {
        super(harimeltEconomy, "Pay");
        this.harimeltEconomy = harimeltEconomy;
    }

    @Override
    public boolean onPlayerExecute(Player player, Command command, String[] arguments) {
        Yaml messages = harimeltEconomy.getMessages();
        if (player.hasPermission("harimelt.pay")) {
            if (arguments.length > 0) {
                if (arguments.length > 1) {
                    String targetName = arguments[0];
                    String amountString = arguments[1];
                    Player targetPlayer = harimeltEconomy.getServer().getPlayerExact(targetName);
                    if (targetPlayer != null) {
                        if (amountString.contains(".")) {
                            amountString = amountString.split("\\.")[0];
                        }
                        if (amountString.contains(",")) {
                            amountString = amountString.split(",")[0];
                        }
                        try {
                            double amount = Double.parseDouble(amountString);
                            Economy economy = harimeltEconomy.getEconomy();
                            if (economy.has(player, amount)) {
                                economy.withdrawPlayer(player, amount);
                                economy.depositPlayer(targetPlayer, amount);
                                messages.sendMessage(player, "Pay.payComplete", new String[][] {{"%playerName%", targetName}, {"%amount%", amountString}});
                                messages.sendMessage(targetPlayer, "Pay.payCompleteNotify", new String[][] {{"%playerName%", player.getName()}, {"%amount%", amountString}});
                            } else {
                                messages.sendMessage(player, "Pay.noHaveMoney");
                            }
                        } catch (NumberFormatException e) {
                            messages.sendMessage(player, "Pay.amountInvalid", new String[][] {{"%amount%", amountString}});
                        }
                    } else {
                        messages.sendMessage(player, "Pay.playerNameInvalid", new String[][] {{"%playerName%", targetName}});
                    }
                } else {
                    messages.sendMessage(player, "Pay.amountEmpty");
                }
            } else {
                messages.sendMessage(player, "Pay.playerNameEmpty");
            }
        } else {
            messages.sendMessage(player, "Pay.noPermission");
        }
        return true;
    }

    @Override
    public boolean onConsoleExecute(ConsoleCommandSender console, Command command, String[] arguments) {
        Yaml messages = harimeltEconomy.getMessages();
        messages.sendMessage(console, "Pay.isConsole");
        return true;
    }

    @Override
    public List<String> onPlayerTabComplete(Player player, Command command, String[] arguments) {
        List<String> complete = new ArrayList<>();
        if (player.hasPermission("harimelt.pay")) {
            if (arguments.length == 1) {
                for (Player p:harimeltEconomy.getServer().getOnlinePlayers()) {
                    complete.add(p.getName());
                }
            }
        }
        return complete;
    }
}