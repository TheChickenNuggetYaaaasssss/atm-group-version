package com.atmbanksimulator;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PrimeAccountTest {
    
    PrimeAccount account;
    
    @BeforeEach
    public void setUp(){
        account = new PrimeAccount("55555", "12345", 1000);
    }
    
    @Test
    public void withdraw_shouldReturnFalse_whenExceedingOverdraft(){
    
        boolean test = account.withdraw(2500);
        
        assertFalse(test);
    }
    
    @Test
    public void withdraw_shouldReturnTrue_whenWithinOverdraft(){
    
        boolean test = account.withdraw(1100);
        
        assertTrue(test);
    }
    
    @Test
    public void getAvailableFunds_shouldIncludeOverdraft(){
    
        int available = account.getAvailableFunds();
    
        assertEquals(1500, available);
    }
}
