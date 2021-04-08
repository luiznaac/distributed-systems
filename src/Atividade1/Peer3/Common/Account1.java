package Common;

import java.util.ArrayList;

public class Account1 {
    private static Account1 instance;
    private int balance;
    public AccountState state;
    public ArrayList<String> line;
    
    private Account1() {
        balance = 0;
        state = AccountState.RELEASED;
        line = new ArrayList<>();
    }
    
    public static Account1 getInstance() {
        if (instance == null) {
            instance = new Account1();
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
