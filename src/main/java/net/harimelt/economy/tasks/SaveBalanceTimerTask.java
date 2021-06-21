package net.harimelt.economy.tasks;

import net.harimelt.economy.HarimeltEconomy;
import net.harimelt.economy.util.task.Task;

public class SaveBalanceTimerTask extends Task {

    private final HarimeltEconomy harimeltEconomy;

    public SaveBalanceTimerTask(HarimeltEconomy harimeltEconomy) {
        super(harimeltEconomy, ((20L) * 60) * 5);
        this.harimeltEconomy = harimeltEconomy;
    }

    @Override
    public void actions() {
        harimeltEconomy.getEconomy().saveAllOnlineAccounts(false);
    }

}