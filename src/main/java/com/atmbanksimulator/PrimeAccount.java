// File: src/main/java/com/atmbanksimulator/PrimeAccount.java

package src.main.java.com.atmbanksimulator;

public class PrimeAccount extends BankAccount {

    private static final int OVERDRAFT_LIMIT = 500;

    public PrimeAccount(String accNumber, String password, int balance) {
        super(accNumber, password, balance);
    }

    @Override
    public boolean withdraw(int amount) {
        if (amount < 0) return false;

        // balance is accessible because it's now 'protected' in BankAccount
        if (balance - amount < -OVERDRAFT_LIMIT) return false;

        balance -= amount;   // we do this ourselves — NOT calling super.withdraw()
        return true;         // because super would block going below zero
    }

    public int getAvailableFunds() {
        return balance + OVERDRAFT_LIMIT;
    }
}
