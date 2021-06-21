package net.harimelt.economy.economy;

import net.harimelt.economy.HarimeltEconomy;
import net.harimelt.economy.util.mysql.MySQL;
import net.harimelt.economy.util.yaml.Yaml;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;

public class EcoApi implements Economy {

    private final HarimeltEconomy harimeltEconomy;

    private MySQL mySQL;
    private boolean mySqlBridge = false;
    public boolean isMySqlBridge() {
        return mySqlBridge;
    }

    public EcoApi(HarimeltEconomy harimeltEconomy) {
        this.harimeltEconomy = harimeltEconomy;
        Yaml configuration = harimeltEconomy.getConfiguration();
        if (configuration.getBoolean("mySqlSync")) {
            String ip = configuration.getString("mysql.ip");
            String port = configuration.getString("mysql.port");
            String username = configuration.getString("mysql.username");
            String password = configuration.getString("mysql.password");
            String database = configuration.getString("mysql.database");
            String table = configuration.getString("mysql.table");
            String[][] values = new String[][]{
                    {"PLAYER_NAME", "VARCHAR(64)"},
                    {"BALANCE", "DOUBLE"}
            };
            mySQL = new MySQL(ip, port, username, password, database, table, values);
            if (mySQL.startConnection()) {
                mySQL.createTable();
                mySQL.closeConnection();
                mySqlBridge = true;
            }
        }
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

    @Override
    public String getName() {
        return "HarimeltEconomy";
    }

    @Override
    public boolean hasBankSupport() {
        return false;
    }

    @Override
    public int fractionalDigits() {
        return 0;
    }

    @Override
    public String format(double v) {
        return null;
    }

    @Override
    public String currencyNamePlural() {
        return null;
    }

    @Override
    public String currencyNameSingular() {
        return null;
    }

    private final HashMap<String, Double> balances = new HashMap<>();

    public void loadAccount(String name) {
        Yaml yaml = new Yaml(harimeltEconomy, "players", name);
        if (yaml.existFileConfiguration()) {
            yaml.registerFileConfiguration();
            if (yaml.contains("balance")) {
                balances.put(name, yaml.getDouble("balance"));
            } else {
                balances.put(name, harimeltEconomy.getConfiguration().getDouble("startBalance"));
                yaml.set("balance", balances.get(name));
                yaml.saveFileConfiguration();
            }
        }
        if (mySqlBridge) {
            mySQL.startConnection();
            if (mySQL.existsPrimaryKey(name)) {
                if (!mySQL.getDouble(name, "BALANCE").equals(balances.get(name))) {
                    balances.put(name, mySQL.getDouble(name, "BALANCE"));
                }
            } else {
                mySQL.createRow(new Object[]{name, balances.get(name)});
            }
            mySQL.closeConnection();
        }
    }

    public void saveAccount(String name) {
        Yaml yaml = new Yaml(harimeltEconomy, "players", name);
        yaml.registerFileConfiguration();
        yaml.set("balance", balances.get(name));
        yaml.saveFileConfiguration();
        if (mySqlBridge) {
            mySQL.startConnection();
            if (mySQL.existsPrimaryKey(name)) {
                mySQL.setDouble(name, "BALANCE", balances.get(name));
            } else {
                mySQL.createRow(new Object[]{name, balances.get(name)});
            }
            mySQL.closeConnection();
        }
        balances.remove(name);
    }

    public void loadAllOnlineAccounts() {
        if (mySqlBridge) {
            mySQL.startConnection();
        }
        for (Player player:harimeltEconomy.getServer().getOnlinePlayers()) {
            Yaml yaml = new Yaml(harimeltEconomy, "players", player.getName());
            yaml.registerFileConfiguration();
            balances.put(player.getName(), yaml.getDouble("balance"));
            if (mySqlBridge) {
                balances.put(player.getName(), mySQL.getDouble(player.getName(), "BALANCE"));
            }
        }
        if (mySqlBridge) {
            mySQL.closeConnection();
        }
    }

    public void saveAllOnlineAccounts(boolean remove) {
        if (mySqlBridge) {
            mySQL.startConnection();
        }
        for (Player player:harimeltEconomy.getServer().getOnlinePlayers()) {
            Yaml yaml = new Yaml(harimeltEconomy, "players", player.getName());
            yaml.registerFileConfiguration();
            yaml.set("balance", balances.get(player.getName()));
            yaml.saveFileConfiguration();
            if (mySqlBridge) {
                mySQL.setDouble(player.getName(), "BALANCE", balances.get(player.getName()));
            }
            if (remove) {
                balances.remove(player.getName());
            }
        }
        if (mySqlBridge) {
            mySQL.closeConnection();
        }
    }

    @Override
    public boolean hasAccount(String s) {
        return false;
    }
    @Override
    public boolean hasAccount(OfflinePlayer offlinePlayer) {
        return false;
    }
    @Override
    public boolean hasAccount(String s, String s1) {
        return false;
    }
    @Override
    public boolean hasAccount(OfflinePlayer offlinePlayer, String s) {
        return false;
    }

    @Override
    public boolean createPlayerAccount(String name) {
        Yaml yaml = new Yaml(harimeltEconomy, "player", name);
        yaml.registerFileConfiguration();
        yaml.set("balance", harimeltEconomy.getConfiguration().getDouble("startBalance"));
        yaml.saveFileConfiguration();
        if (mySqlBridge) {
            mySQL.startConnection();
            mySQL.createRow(new Object[] {name, harimeltEconomy.getConfiguration().getDouble("startBalance")});
            mySQL.closeConnection();
        }
        return true;
    }
    @Override
    public boolean createPlayerAccount(OfflinePlayer offlinePlayer) {
        return createPlayerAccount(offlinePlayer.getName());
    }
    @Override
    public boolean createPlayerAccount(String s, String s1) {
        return createPlayerAccount(s);
    }
    @Override
    public boolean createPlayerAccount(OfflinePlayer offlinePlayer, String s) {
        return createPlayerAccount(offlinePlayer);
    }

    @Override
    public double getBalance(String s) {
        if (balances.containsKey(s)) {
            return balances.get(s);
        }
        return 0;
    }
    @Override
    public double getBalance(OfflinePlayer offlinePlayer) {
        return getBalance(offlinePlayer.getName());
    }
    @Override
    public double getBalance(String s, String s1) {
        return getBalance(s);
    }
    @Override
    public double getBalance(OfflinePlayer offlinePlayer, String s) {
        return getBalance(offlinePlayer);
    }

    @Override
    public boolean has(String s, double v) {
        return getBalance(s) >= v;
    }
    @Override
    public boolean has(OfflinePlayer offlinePlayer, double v) {
        return has(offlinePlayer.getName(), v);
    }
    @Override
    public boolean has(String s, String s1, double v) {
        return has(s, v);
    }
    @Override
    public boolean has(OfflinePlayer offlinePlayer, String s, double v) {
        return has(offlinePlayer, v);
    }

    @Override
    public EconomyResponse withdrawPlayer(String s, double v) {
        double balance = getBalance(s);
        if (balance >= v) {
            balances.put(s, balance - v);
            Yaml yaml = new Yaml(harimeltEconomy, "players", s);
            yaml.registerFileConfiguration();
            yaml.set("balance", balance - v);
            if (mySqlBridge) {
                mySQL.startConnection();
                mySQL.setDouble(s, "BALANCE", balance - v);
                mySQL.closeConnection();
            }
            return new EconomyResponse(v, balance, EconomyResponse.ResponseType.SUCCESS, "Transaction Can Susses");
        } else {
            return new EconomyResponse(v, balance, EconomyResponse.ResponseType.FAILURE, "No Sufficient Balance");
        }
    }
    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer offlinePlayer, double v) {
        return withdrawPlayer(offlinePlayer.getName(), v);
    }
    @Override
    public EconomyResponse withdrawPlayer(String s, String s1, double v) {
        return withdrawPlayer(s, v);
    }
    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer offlinePlayer, String s, double v) {
        return withdrawPlayer(offlinePlayer, v);
    }

    @Override
    public EconomyResponse depositPlayer(String s, double v) {
        double balance = getBalance(s);
        balances.put(s, balance + v);
        Yaml yaml = new Yaml(harimeltEconomy, "players", s);
        yaml.registerFileConfiguration();
        yaml.set("balance", balance + v);
        if (mySqlBridge) {
            mySQL.startConnection();
            mySQL.setDouble(s, "BALANCE", balance + v);
            mySQL.closeConnection();
        }
        return new EconomyResponse(v, balance, EconomyResponse.ResponseType.SUCCESS, "none");
    }
    @Override
    public EconomyResponse depositPlayer(OfflinePlayer offlinePlayer, double v) {
        return depositPlayer(offlinePlayer.getName(), v);
    }
    @Override
    public EconomyResponse depositPlayer(String s, String s1, double v) {
        return depositPlayer(s, v);
    }
    @Override
    public EconomyResponse depositPlayer(OfflinePlayer offlinePlayer, String s, double v) {
        return depositPlayer(offlinePlayer, v);
    }








    /**
     * @param s
     * @param s1
     * @deprecated
     */
    @Override
    public EconomyResponse createBank(String s, String s1) {
        return null;
    }

    @Override
    public EconomyResponse createBank(String s, OfflinePlayer offlinePlayer) {
        return null;
    }

    @Override
    public EconomyResponse deleteBank(String s) {
        return null;
    }

    @Override
    public EconomyResponse bankBalance(String s) {
        return null;
    }

    @Override
    public EconomyResponse bankHas(String s, double v) {
        return null;
    }

    @Override
    public EconomyResponse bankWithdraw(String s, double v) {
        return null;
    }

    @Override
    public EconomyResponse bankDeposit(String s, double v) {
        return null;
    }

    /**
     * @param s
     * @param s1
     * @deprecated
     */
    @Override
    public EconomyResponse isBankOwner(String s, String s1) {
        return null;
    }

    @Override
    public EconomyResponse isBankOwner(String s, OfflinePlayer offlinePlayer) {
        return null;
    }

    /**
     * @param s
     * @param s1
     * @deprecated
     */
    @Override
    public EconomyResponse isBankMember(String s, String s1) {
        return null;
    }

    @Override
    public EconomyResponse isBankMember(String s, OfflinePlayer offlinePlayer) {
        return null;
    }

    @Override
    public List<String> getBanks() {
        return null;
    }

}
