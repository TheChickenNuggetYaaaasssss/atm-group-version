package src.main.java.com.atmbanksimulator;

// ===== 📚🌐Bank (Domain / Service / Business Logic) =====

// Bank class: a simple implementation of a bank, containing a list of bank accounts
// and has a currently logged-in account (loggedInAccount).

public class Bank {

    // ToDO: Optional extension:
    // Improve account management in the Bank class:
    // Replace Array with ArrayList for managing BankAccount objects.
    // Refactor addBankAccount and login methods to leverage ArrayList.



    // Instance variables storing bank information
    private int maxAccounts = 10;                       // Maximum number of accounts the bank can hold
    private int numAccounts = 0;                        // Current number of accounts in the bank
    private BankAccount[] accounts = new BankAccount[maxAccounts];  // Array to hold BankAccount objects
    private BankAccount loggedInAccount = null;         // Currently logged-in account ('null' if no one is logged in)

    // a method to create new BankAccount - this is known as a 'factory method' and is a more
    // flexible way to do it than just using the 'new' keyword directly.
    public BankAccount makeBankAccount(String accNumber, String accPasswd, int balance) {
        return new BankAccount(accNumber, accPasswd, balance);
    }

    // a method to add a new bank account to the bank - it returns true if it succeeds
    // or false if it fails (because the bank is 'full')
    public boolean addBankAccount(BankAccount a) {
        if (numAccounts < maxAccounts) {
            accounts[numAccounts] = a;
            numAccounts++ ;
            return true;
        } else {
            return false;
        }
    }

    // Variant of addBankAccount: creates a BankAccount and adds it in one step.
    // This is an example of method overloading: two methods can share the same name
    // if they have different parameter lists.
    public boolean addBankAccount(String accNumber, String accPasswd, int balance) {
        return addBankAccount(makeBankAccount(accNumber, accPasswd, balance));
    }
    
    // Change the password of the currently logged-in account
    // Returns true if old password matched and change succeeded
    public boolean changePassword(String oldPassword, String newPassword) {
        if (loggedInAccount == null) return false;

        // Check the old password matches
        if (!loggedInAccount.getaccPasswd().equals(oldPassword)) {
            return false;   // wrong old password — reject
        }

        // New password must not be empty
        if (newPassword.isEmpty()) return false;

        loggedInAccount.setPassword(newPassword);   // save the new password
        return true;
    }

    // Create a brand new account
    // Returns true if successful, false if account number already exists
    public boolean createAccount(String accNumber, String password, int balance, String accountType) {
        if (findAccount(accNumber) != null) return false;
        if (accNumber.isEmpty() || password.isEmpty() || balance < 0) return false;

        // Create the right subclass based on the type chosen
        BankAccount newAccount;
        switch (accountType) {
            case "Student":  newAccount = new StudentAccount(accNumber, password, balance); break;
            case "Prime":    newAccount = new PrimeAccount(accNumber, password, balance);   break;
            case "Saving":   newAccount = new SavingAccount(accNumber, password, balance);  break;
            default:         newAccount = new BankAccount(accNumber, password, balance);    break;
        }

        return addBankAccount(newAccount);
    }

    // Check whether the given accountNumber and password match an existing BankAccount.
    // If successful, set 'loggedInAccount' to that account and return true.
    // Otherwise, set 'loggedInAccount' to null and return false.
    public boolean login(String accountNumber, String password) {
        logout(); // logout of any previous loggedInAccount

        // Search the accounts array to find a BankAccount with a matching accountNumber and password.
        // - If found, set 'loggedInAccount' to that account and return true.
        // - If not found, reset 'loggedInAccount' to null and return false.
        for (BankAccount b: accounts) {
            if (b == null) continue;  // This line will skip null slots
            if (b.getAccNumber().equals(accountNumber) && b.getaccPasswd().equals(password)) {
                // found the right account
                loggedInAccount = b;
                return true;
            }
        }
        // not found - return false
        loggedInAccount = null;
        return false;
    }

    // Log out of the currently logged-in account, if any
    public void logout() {
        if (loggedIn()) {
            loggedInAccount = null;
        }
    }

    // Check whether the bank currently has a logged-in account
    public boolean loggedIn() {
        if (loggedInAccount == null) {
            return false;
        } else {
            return true;
        }
    }

    // Attempt to deposit money into the currently logged-in account
    // by calling the deposit method of the BankAccount object
    public boolean deposit(int amount)
    {
        if (loggedIn()) {
            return loggedInAccount.deposit(amount);
        } else {
            return false;
        }
    }


    // Attempt to withdraw money from the currently logged-in account
    // by calling the withdraw method of the BankAccount object
    public boolean withdraw(int amount)
    {
        if (loggedIn()) {
            return loggedInAccount.withdraw(amount);
        } else {
            return false;
        }
    }

    // get the currently logged-in account balance
    // by calling the getBalance method of the BankAccount object
    public int getBalance()
    {
        if (loggedIn()) {
            return loggedInAccount.getBalance();
        } else {
            return -1; // use -1 as an indicator of an error
        }
    }
    
    //Adding findAccount()
    public BankAccount findAccount(String accNumber) {
        for (BankAccount acc : accounts) {          // 'accounts' is my list of accounts
            if (acc == null) continue;  // This line will skip null slots
            if (acc.getAccNumber().equals(accNumber)) {
                return acc;
            }
        }
        return null;   // When account is not found
    }

    public boolean transfer(String targetAccNumber, int amount) {
        BankAccount target = findAccount(targetAccNumber);
        if (target == null) return false;

        // Withdraw from logged-in account first using its own rules
        // (StudentAccount will check daily cap, PrimeAccount will check overdraft, etc.)
        boolean withdrawn = loggedInAccount.withdraw(amount);

        if (withdrawn) {
            // Only deposit into target if the withdrawal actually succeeded
            target.deposit(amount);
            return true;
        }
        return false;  // withdraw failed — nothing happened to either account
    }
    
    // Returns null if the withdrawal is allowed, or a specific error message if not
    // UIModel calls this to show the user a helpful message instead of just "failed"
    public String getWithdrawError(int amount) {
        if (!loggedIn())  return "You are not logged in";
        if (amount <= 0)  return "Invalid amount";

        if (loggedInAccount instanceof StudentAccount) {
            StudentAccount sa = (StudentAccount) loggedInAccount;
            if (amount > sa.getRemainingDailyLimit()) {
                return "Daily limit reached — £" + (int) sa.getRemainingDailyLimit() + " remaining today";
            }
            if (amount > loggedInAccount.getBalance()) {
                return "Insufficient funds";
            }

        } else if (loggedInAccount instanceof PrimeAccount) {
            PrimeAccount pa = (PrimeAccount) loggedInAccount;
            if (amount > pa.getAvailableFunds()) {
                return "Exceeds overdraft limit — £" + (int) pa.getAvailableFunds() + " available";
            }

        } else {
            // Standard or Saving — normal balance check
            if (amount > loggedInAccount.getBalance()) {
                return "Insufficient funds";
            }
        }
        return null;  // null means no error — withdrawal is allowed
    }
    
    public String getDepositError(int amount) {
        if (!loggedIn()) return "You are not logged in";
        if (amount <= 0)  return "Invalid amount";

        if (loggedInAccount instanceof SavingAccount) {
            if (amount < 10) {
                return "Saving accounts require a minimum deposit of £10";
            }
        }
        return null;  // null means no error — deposit is allowed
    }
    
    public String getTransferError(String targetAccNumber, int amount) {
        if (!loggedIn()) return "You are not logged in";
        if (amount <= 0)  return "Invalid amount";

        if (findAccount(targetAccNumber) == null) {
            return "Target account not found";
        }

        // Reuse withdraw error — transfer deducts from sender using same rules
        return getWithdrawError(amount);
    }

}

