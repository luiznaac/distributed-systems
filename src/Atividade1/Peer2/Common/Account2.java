package Common;

import java.util.ArrayList;

public class Account2 {
    private static Account2 instance;
    private int balance;
    public AccountState state;
    public ArrayList<String> line;

    private Account2() {
        balance = 0;
        state = AccountState.RELEASED;
        line = new ArrayList<>();
    }

    public static Account2 getInstance() {
        if (instance == null) {
            instance = new Account2();
        }
        return instance;
    }

    public void insertMoney(int value) {
        balance += value;
    }

    public void withdrawMoney(int value) {
        balance -= value;
    }

    public int getBalance() {
        return balance;
    }

    public void setState(AccountState state) { this.state = state; }
}
