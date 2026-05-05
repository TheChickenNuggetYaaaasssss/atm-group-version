package src.main.java.com.atmbanksimulator;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class BankTest{
    
    private Bank bank;
    
    @BeforeEach
    public void setUp(){
        bank = new Bank();
    }
    
    @Test
    public void createAccount_shouldReturnTrue_whenBelowAccountLimit(){
        
        boolean result = bank.createAccount("45454", "12345", 500, "Student");
        assertTrue(result);
        
    }
    
    @Test
    public void createAccount_shouldReturnFalse_whenOverAccountLimit(){
        
        boolean accountCreatedSuccessfully = true;
        int accNo = 45454;
        
        while (accountCreatedSuccessfully) {
            
            // increment account no to prevent duplicates being used
            accNo++;
            String accountNo = String.format("%05d", accNo);
            
            accountCreatedSuccessfully = bank.createAccount(accountNo, "12345", 500, "Student");
            System.out.println(accountCreatedSuccessfully);
            
        }
        assertFalse(accountCreatedSuccessfully);
    
    }
    
    @Test
    public void createAccount_shouldReturnFalse_whenAccountNumberTaken(){
    
        String accountNo = "65656";
        
        // create account with the accountNo
        boolean account1 = bank.createAccount(accountNo, "12345", 500, "Student");
        // create another account with the same accountNo
        boolean account2 = bank.createAccount(accountNo, "12345", 500, "Student");
        
        assertFalse(account2);
    }
    
    @Test
    public void login_shouldReturnTrue_whenCorrectDetails(){
        
        String accNo = "10001";
        String accPsswd = "11111";
        
        bank.createAccount(accNo, accPsswd, 500, "Student");
        
        boolean loginAttempt = bank.login(accNo, accPsswd);
        
        assertTrue(loginAttempt);
    }
    
    @Test
    public void login_shouldReturnFalse_whenInorrectDetails(){
        
        String accNo = "10001";
        String accPsswd = "11111";
        String wrongPsswd = "22222";
        
        bank.createAccount(accNo, accPsswd, 500, "Student");
        
        boolean loginAttempt = bank.login(accNo, wrongPsswd);
        
        assertFalse(loginAttempt);
    }
    
    @Test
    public void withdraw_shouldPass_whenSufficientFunds(){
        
        bank.createAccount("45454", "12345", 500, "Student");
        bank.login("45454", "12345");
        
        boolean withdrawAttempt = bank.withdraw(100);
        
        assertTrue(withdrawAttempt);
    }

    @Test
    public void withdraw_shouldFail_whenInsufficientFunds(){
        
        bank.createAccount("45454", "12345", 500, "Student");
        bank.login("45454", "12345");
        
        boolean withdrawAttempt = bank.withdraw(99999);
        
        assertFalse(withdrawAttempt);
    }
}
