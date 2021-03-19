package Common;

public class Account2 {
    private static Account2 instance;
    private static int balance;
    
    private Account2() {}
    
    public static synchronized Account2 getInstance() {
        if (instance == null) {
            instance = new Account2();
            balance = 0;
        }
        return instance;
    }
    
    public static synchronized void insertMoney(int value) {
        balance += value;
    }
    
    public static synchronized void withdrawMoney(int value) {
        balance -= value;
    }
}
