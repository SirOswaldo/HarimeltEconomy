/*
 *  Copyright (C) 2021 SirOswaldo
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *      You should have received a copy of the GNU General Public License
 *      along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package net.harimelt.economy;

import net.harimelt.economy.commands.BalanceCommand;
import net.harimelt.economy.commands.EconomyCommand;
import net.harimelt.economy.commands.HarimeltEconomyCommand;
import net.harimelt.economy.commands.PayCommand;
import net.harimelt.economy.economy.EcoApi;
import net.harimelt.economy.listeners.PlayerJoinListener;
import net.harimelt.economy.listeners.PlayerQuitListener;
import net.harimelt.economy.tasks.SaveBalanceTimerTask;
import net.harimelt.economy.util.yaml.Yaml;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;

public class HarimeltEconomy extends JavaPlugin {

    // Files
    private final Yaml configuration = new Yaml(this, "configuration");
    public Yaml getConfiguration() {
        return configuration;
    }
    private final Yaml messages = new Yaml(this, "messages");
    public Yaml getMessages() {
        return messages;
    }

    // Economy
    private EcoApi economy;
    public EcoApi getEconomy() {
        return economy;
    }

    private final HashMap<String, Double> balances = new HashMap<>();
    public HashMap<String, Double> getBalances() {
        return balances;
    }

    @Override
    public void onEnable() {
        // Register Yaml Files
        configuration.registerFileConfiguration();
        messages.registerFileConfiguration();
        // Setup Vault
        economy = new EcoApi(this);
        if (!setupEconomy()) {
            getLogger().info("This plugin need Vault to found!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        getLogger().info("Vault found, Economy has been registered.");
        // Register Commands
        new HarimeltEconomyCommand(this);
        new EconomyCommand(this);
        new PayCommand(this);
        new BalanceCommand(this);
        // Register Listeners
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerQuitListener(this), this);
        // Load All Online
        economy.loadAllOnlineAccounts();
        SaveBalanceTimerTask saveBalanceTimerTask = new SaveBalanceTimerTask(this);
        saveBalanceTimerTask.startScheduler();
    }

    @Override
    public void onDisable() {
        economy.saveAllOnlineAccounts(true);
    }

    private boolean setupEconomy() {
        if (this.getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        this.getServer().getServicesManager().register(Economy.class, economy, this,
                ServicePriority.Highest);
        return true;
    }

}