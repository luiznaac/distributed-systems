package Common;

public class Account2 {
    private static Account2 instance;
    private int balance;
    
    private Account2() {
        balance = 0;
    }
    
    public static synchronized Account2 getInstance() {
        if (instance == null) {
            instance = new Account2();
        }
        return instance;
    }
    
    public synchronized void insertMoney(int value) {
        balance += value;
    }
    
    public synchronized void withdrawMoney(int value) {
        balance -= value;
    }

    public synchronized int getBalance() {
        return balance;
    }
    

}
