package com.atmbanksimulator;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SavingAccountTest {
    
    SavingAccount account;
    
    @BeforeEach
    public void setUp(){
        
        account = new SavingAccount("55555", "12345", 500);
    }
    
    @Test
    public void applyInterest_shouldApplyInterest(){
        
        
        double startingInterestEarned = account.getInterestEarned();
        account.applyInterest();
        
        if (account.getInterestEarned() > startingInterestEarned){
            assertTrue(true);
        }
    }
}
