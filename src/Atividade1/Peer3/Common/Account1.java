package Common;

public class Account1 {
    private static Account1 instance;
    private int balance;
    
    private Account1() {
        balance = 0;
    }
    
    public static synchronized Account1 getInstance() {
        if (instance == null) {
            instance = new Account1();
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
