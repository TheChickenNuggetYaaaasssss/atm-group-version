

package com.atmbanksimulator;

public class StudentAccount extends BankAccount {

    private static final int DAILY_LIMIT = 200;
    private int withdrawnToday = 0;

    public StudentAccount(String accNumber, String password, int balance) {
        super(accNumber, password, balance);
    }

    @Override
    public boolean withdraw(int amount) {   // matches BankAccount — both double now
        if (withdrawnToday + amount > DAILY_LIMIT) return false;
        if (super.withdraw(amount)) {
            withdrawnToday += amount;
            return true;
        }
        return false;
    }

    public int getRemainingDailyLimit() {
        return DAILY_LIMIT - withdrawnToday;
    }
}
