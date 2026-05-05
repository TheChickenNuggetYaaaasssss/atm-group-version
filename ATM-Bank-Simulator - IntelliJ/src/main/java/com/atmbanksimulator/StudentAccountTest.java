package com.atmbanksimulator;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


public class StudentAccountTest{

    StudentAccount account;
    
    @BeforeEach
    public void setUp(){
        account = new StudentAccount("55555", "12345", 500);
    }
    
    
    @Test
    public void withdraw_shouldFail_whenExceedsDailyLimit(){
    
        boolean success = true;
        while (success){
            success = account.withdraw(50);
        }
        
        assertFalse(success);
    }
    
    @Test
    public void withdraw_shouldPass_whenWithinDailyLimit(){
    
        boolean success = account.withdraw(50);
        
        assertTrue(success);
    }
}
