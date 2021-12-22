package banking;


public class BankAccount {
    protected static int currentId = 0;

    // for database
    private final int id;

    // length is 16
    private final String cardNumber;

    // length is 4
    private final String pin;

    private int balance = 0;

    public BankAccount(String cardNumber, String pin) {
        this.cardNumber = cardNumber;
        this.pin = pin;
        id = ++currentId;
    }

    public BankAccount(int id, String cardNumber, String pin, int balance) {
        this.cardNumber = cardNumber;
        this.pin = pin;
        this.balance = balance;
        this.id = id;
    }


    int getId() {
        return id;
    }

    String getCardNumber() {
        return cardNumber;
    }

    String getPin() {
        return pin;
    }

    int getBalance() {
        return balance;
    }

    void setBalance(int balance) {
        this.balance = balance;
    }
}
