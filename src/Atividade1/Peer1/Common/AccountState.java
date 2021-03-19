package Common;

public enum AccountState {
    
    WANTED(1), RELEASED(2), HELD(3);
   
    private final int value;
   
    AccountState(final int s) { value = s; }
   
    public int getValue() {
        return value;
    }
}
