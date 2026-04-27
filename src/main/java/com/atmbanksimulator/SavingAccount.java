// File: src/main/java/com/atmbanksimulator/SavingAccount.java

package src.main.java.com.atmbanksimulator;

public class SavingAccount extends BankAccount {

    private static final double INTEREST_RATE = 0.03;
    private double interestEarned = 0;

    public SavingAccount(String accNumber, String password, int balance) {
        super(accNumber, password, balance);
    }

    // No @Override on withdraw — savings accounts use BankAccount's standard withdraw()

    public void applyInterest() {
        double interest = balance * (INTEREST_RATE / 12);  // monthly
        balance       += interest;
        interestEarned += interest;
    }

    public double getInterestEarned() {
        return interestEarned;
    }
}
