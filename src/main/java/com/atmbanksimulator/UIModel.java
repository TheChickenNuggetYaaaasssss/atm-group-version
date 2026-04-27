package src.main.java.com.atmbanksimulator;

// ===== 🧠 UIModel (Brain) =====

// The UIModel represents all the actual content and functionality of the app
// For the ATM, it keeps track of the information shown in the display
// (the laMsg and two tfInput boxes), and the interaction with the bank, executes
// commands provided by the controller and tells the view to update when
// something changes
public class UIModel {
    View view; // Reference to the View (part of the MVC setup)
    Bank bank; // The ATM communicates with this Bank
    BankAccount[] accounts;

    // The ATM UIModel can be in one of three states:
    // 1. Waiting for an account number
    // 2. Waiting for a password
    // 3. Logged in (ready to process requests for the logged-in account)
    // We represent each state with a String constant.
    // The 'final' keyword ensures these values cannot be changed.
    private final String STATE_ACCOUNT_NO = "account_no";
    private final String STATE_PASSWORD = "password";
    private final String STATE_LOGGED_IN = "logged_in";
    private final String STATE_CHANGE_PW_OLD      = "change_pw_old";
    private final String STATE_CHANGE_PW_NEW      = "change_pw_new";
    private final String STATE_NEW_ACC_NUMBER     = "new_acc_number";
    private final String STATE_NEW_ACC_PASSWORD   = "new_acc_password";
    private final String STATE_NEW_ACC_TYPE = "new_acc_type";
    private final String STATE_NEW_ACC_BALANCE    = "new_acc_balance";
    private final String STATE_TRANSFER_ACC =  "transfer_acc";
    private final String STATE_TRANSFER_AMT       = "transfer_amt";
    
    // Variables representing the state and data of the ATM UIModel
    private String state = STATE_ACCOUNT_NO;    // Current state of the ATM
    private String accNumber = "";         // Account number being typed
    private String accPasswd = "";         // Password being typed
    private String newAccNumber   = "";   // stores the number typed for a new account
    private String newAccPassword = "";   // stores the password typed for a new account
    private String newAccType = "";

    String trnsfAccNumber;
    int depth = 0;

    // Variables shown on the View display
    private String message;                // Message label text
    private String numberPadInput;         // Current number displayed in the TextField (as a string)
    private String result;                 // Contents of the TextArea (may be multiple lines)

    // UIModel constructor: pass a Bank object that the ATM interacts with
    public UIModel(Bank bank) {
        this.bank = bank;
    }

    int attempt = 0; // used to store number of incorrect passwords entered
    int amount = 3; // used to store number of attempts left for password input

    // Initialize the ATM UIModel: this method is called by Main when starting the app
    // - Set state to STATE_ACCOUNT_NO
    // - Clear the numberPadInput - numbers displayed in the TextField
    // - Display the welcome message and user instructions
    public void initialise() {
        setState(STATE_ACCOUNT_NO);
        numberPadInput = "";
        message = "Welcome to the ATM";
        result = "Enter your Account Number";
        update();
    }

    // Reset the ATM UIModel after an invalid action or logout:
    // - Set state to STATE_ACCOUNT_NO
    // - Clear the numberPadInput
    // - Display the provided message and user instructions
    private void reset(String msg) {
        setState(STATE_ACCOUNT_NO);
        numberPadInput = "";
        message = msg;
        result = "Enter your Account Number";
    }

    // Change the ATM state and print a debug message whenever the state changes
    private void setState(String newState)
    {
        if ( !state.equals(newState) )
        {
            String oldState = state;
            state = newState;
            System.out.println("UIModel::setState: changed state from "+ oldState + " to " + newState);
        }
    }

    // These process**** methods are called by the Controller
    // in response to specific button presses on the GUI.

    // Handle a number button press: append the digit to numberPadInput
    public void processNumber(String numberOnButton) {
        // Optional extension:
        // Improve feedback by showing what the number is being entered for based on the current state.
        // e.g.  if state is STATE_ACCOUNT_NO, display "Receiving Account Number, Beep 5 received"
        numberPadInput += numberOnButton;
        message = "Beep! " + numberOnButton + " received";
        update();
    }

    // Handle the Clear button: reset the current number stored in numberPadInput
    public void processClear() {
        // Optional extension:
        // Improve feedback by showing what was cleared depending on the current state.
        // e.g. if state is STATE_ACCOUNT_NO, display "Account Number cleared: 123"
        if (!numberPadInput.isEmpty()) {
            numberPadInput = "";
            message = "Input Cleared";
            update();
        }
    }


    // Handle the Enter button.
    // This is a more complex method: pressing Enter causes the ATM to change state,
    // progressing from STATE_ACCOUNT_NO → STATE_PASSWORD → STATE_LOGGED_IN,
    // and back to STATE_ACCOUNT_NO when logging out.
    public void processEnter()
    {
        // The action depends on the current ATM state
        switch ( state )
        {
            case STATE_ACCOUNT_NO:

                // Waiting for a complete account number
                // If nothing was entered, reset with "Invalid Account Number"
                if (numberPadInput.isEmpty()) {
                    message = "Invalid Account Number";
                    reset(message);
                }
                else{
                    // Save the entered number as accNumber, clear numberPadInput,
                    // update the state to expect password, and provide instructions
                    accNumber = numberPadInput;
                    numberPadInput = "";
                    setState(STATE_PASSWORD);
                    message = "Account Number Accepted";
                    result = "Now enter your Password";
                }
                break;




            case STATE_PASSWORD:

                if (attempt <= 3) { // checks number of invalid passwords entered
                    // Waiting for a password
                    // Save the typed number as accPasswd, clear numberPadInput,
                    // then contact the bank to attempt login
                    accPasswd = numberPadInput;
                    numberPadInput = "";
                    if (bank.login(accNumber, accPasswd)) {
                        // Successful login: change state to STATE_LOGGED_IN and provide instructions
                        setState(STATE_LOGGED_IN);
                        message = "Logged In";
                        view.changeButtons();
                        result = "Please select an option:\n\nFIN - Finish     | DEP - Deposit\nCLR - Clear      | W/D - Withdraw\nBAL - Balance    | NEWACC - New Account\nBACK - Back      | CHPW - Change Password\nENTR - Enter     | TRNSF - Transfer";
                    }
                    else {
                        attempt++; // tallies invalid attempts
                        message = "Login failed: " + amount + " attempt(s) left";
                        amount--; // counts number of attempts left
                        reset(message);
                    }
                }
                else {
                    message = "Too many attempts, please try again later"; // error if user inputs incorrect password too many times
                    reset(message);
                }
                break;

            case STATE_LOGGED_IN:
                break;


                // Do nothing for other states (user is already logged in)

            case STATE_CHANGE_PW_OLD:
                // User has typed their old password and pressed ENTR
                String oldPw = numberPadInput;
                numberPadInput = "";

                if (oldPw.isEmpty()) {
                    message = "Password cannot be empty";
                    // stay in same state — let them try again
                } else {
                    // Store old password temporarily and ask for the new one
                    accPasswd = oldPw;   // reuse accPasswd to temporarily hold old pw
                    setState(STATE_CHANGE_PW_NEW);
                    message = "Current password accepted";
                    result  = "Now enter your NEW password\nFollowed by \"ENTR\"";
                }
                break;

            case STATE_CHANGE_PW_NEW:
                // User has typed their new password and pressed Ent
                String newPw = numberPadInput;
                numberPadInput = "";

                if (newPw.isEmpty()) {
                    message = "New password cannot be empty";
                    // stay in same state — let them try again
                } else if (bank.changePassword(accPasswd, newPw)) {
                    // accPasswd holds the old pw we saved in the previous step
                    setState(STATE_LOGGED_IN);
                    message = "Password changed successfully";
                    result  = "Please select an option:\n\nFIN - Finish     | DEP - Deposit\nCLR - Clear      | W/D - Withdraw\nBAL - Balance    | NEWACC - New Account\nBACK - Back      | CHPW - Change Password\nENTR - Enter     | TRNSF - Transfer";
                } else {
                    // Old password was wrong — kick back to start
                    message = "Incorrect current password";
                    reset(message);
                }
                break;

            case STATE_NEW_ACC_NUMBER:
                // User has typed a new account number and pressed Ent
                String typedNumber = numberPadInput;
                numberPadInput = "";

                if (typedNumber.isEmpty()) {
                    message = "Account number cannot be empty";
                } else if (typedNumber.length() < 4) {
                    message = "Account number too short (min 4 digits)";
                } else {
                    newAccNumber = typedNumber;   // save it
                    setState(STATE_NEW_ACC_PASSWORD);
                    message = "Account number accepted";
                    result  = "Now enter a password for this account\nFollowed by \"ENTR\"";
                }
                break;

            case STATE_NEW_ACC_PASSWORD:
                // User has typed a password for the new account and pressed Ent
                String typedPassword = numberPadInput;
                numberPadInput = "";

                if (typedPassword.isEmpty()) {
                    message = "Password cannot be empty";
                } else {
                    newAccPassword = typedPassword;   // save it
                    setState(STATE_NEW_ACC_TYPE);
                    message = "Password accepted";
                    result  = "Select account type:\n1 = Standard\n2 = Student\n3 = Prime\n4 = Saving";
                }
                break;
                
            case STATE_NEW_ACC_TYPE:
                String typeChoice = numberPadInput;
                numberPadInput = "";

                switch (typeChoice) {
                    case "1": newAccType = "Standard"; break;
                    case "2": newAccType = "Student";  break;
                    case "3": newAccType = "Prime";    break;
                    case "4": newAccType = "Saving";   break;
                    default:  newAccType = "";         break;
                }

                if (newAccType.isEmpty()) {
                    message = "Invalid choice. Press 1, 2, 3 or 4";
                    result  = "Select account type:\n1 = Standard\n2 = Student\n3 = Prime\n4 = Saving";
                } else {
                    setState(STATE_NEW_ACC_BALANCE);
                    message = newAccType + " account selected";
                    result  = "Now enter the starting balance\nFollowed by \"Ent\"";
                }
                break;
    

            case STATE_NEW_ACC_BALANCE:
                int startingBalance = parseValidAmount(numberPadInput);
                numberPadInput = "";
                if (startingBalance < 0) {
                    message = "Balance cannot be negative";
                } else if (bank.createAccount(newAccNumber, newAccPassword, startingBalance, newAccType)) {
                    message = "Account created: " + newAccNumber;
                    result  = newAccType + " account created!\nEnter your account number to log in.";
                    reset(message);
                } else {
                    message = "Account number already exists";
                    reset(message);
                }
                break;



            case STATE_TRANSFER_ACC:



                if (bank.findAccount(numberPadInput) == null) {
                    message = "Invalid Account Number";
                    setState(STATE_TRANSFER_ACC);
                }
                else if (numberPadInput.equals(accNumber)) {
                    message = "Account number cannot be the same as current account";
                    numberPadInput = "";
                    setState(STATE_TRANSFER_ACC);
                }
                else{

                    // Save the entered number as accNumber, clear numberPadInput,
                    // update the state to expect password, and provide instructions
                    trnsfAccNumber = numberPadInput;
                    numberPadInput = "";
                    setState(STATE_TRANSFER_AMT);
                    message = "Account Number Accepted";
                    result = "Now enter the amount you wish to transfer\nFollowed by \"ENTR\"";
                }
                break;

            case STATE_TRANSFER_AMT:
                if (numberPadInput.isEmpty()) {
                    message = "Invalid Amount";
                    setState(STATE_TRANSFER_AMT);
                } else {
                    int transferAmount = parseValidAmount(numberPadInput);
                    numberPadInput = "";

                    String transferError = bank.getTransferError(trnsfAccNumber, transferAmount);

                    if (transferError != null) {
                        message = "Transfer Failed: " + transferError; // specific reason shown to user
                        setState(STATE_TRANSFER_AMT);
                    } else {
                        bank.transfer(trnsfAccNumber, transferAmount);
                        message = "£" + transferAmount + " transferred to " + trnsfAccNumber;
                        result  = "Please select an option:\n\nFIN - Finish     | DEP - Deposit\nCLR - Clear      | W/D - Withdraw\nBAL - Balance    | NEWACC - New Account\nBACK - Back      | CHPW - Change Password\nENTR - Enter     | TRNSF - Transfer";
                        setState(STATE_LOGGED_IN);
                    }
                }
                break;

            default:
            // Unknown state — should never happen
                break;
        }

        update();
    }
   

    /**
     * Parses a string into a valid transaction amount.
     * - If the string is empty, invalid, or consists only of zeros, returns 0.
     * - Otherwise, returns the integer value.
     *
     * Purpose:
     * Helper method for validating user-entered amounts in transactions (Deposit, Withdraw, etc.).
     *
     * Note: If you later add features like Transfer, this method can be reused.
     */
    private int parseValidAmount(String number) {
        if (number.isEmpty()) {
            return 0;
        }
        try {
            return Integer.parseInt(number);
        } catch (NumberFormatException e) {
            return 0; // Invalid input -> treated as 0
        }
    }

    // Handle the Balance button:
    // - If the user is logged in, retrieve the current balance and update messages/results accordingly
    // - Otherwise, reset the ATM and display an error message
    public void processBalance() {
        if (state.equals(STATE_LOGGED_IN) || state.equals(STATE_TRANSFER_ACC) || state.equals(STATE_NEW_ACC_NUMBER) || state.equals(STATE_CHANGE_PW_NEW) || state.equals(STATE_CHANGE_PW_OLD) ) {
            numberPadInput = "";
            message = "Balance Available";
            result = "Your Balance is: " + bank.getBalance();
            setState(STATE_LOGGED_IN);
            depth++;
        } else {
            reset("You are not logged in");
        }
        update();
    }

    // Handle the Withdraw button:
    // If the user is logged in, attempt to withdraw the amount entered;
    // otherwise, reset the ATM and display an error message.
    // Reads the amount from numberPadInput, validates it, and updates messages/results accordingly.
    public void processWithdraw() {
    if (state.equals(STATE_LOGGED_IN)) {
        int amount = parseValidAmount(numberPadInput);
        numberPadInput = "";

        if (amount <= 0) {
            message = "Invalid Amount";
            result  = "Please select an option:\n\nFIN - Finish     | DEP - Deposit\nCLR - Clear      | W/D - Withdraw\nBAL - Balance    | NEWACC - New Account\nBACK - Back      | CHPW - Change Password\nENTR - Enter     | TRNSF - Transfer";
        } else {
            String error = bank.getWithdrawError(amount); // ask Bank why it would fail

            if (error != null) {
                message = "Withdraw Failed: " + error;   // specific reason shown to user
                result  = "Please select an option:\n\nFIN - Finish     | DEP - Deposit\nCLR - Clear      | W/D - Withdraw\nBAL - Balance    | NEWACC - New Account\nBACK - Back      | CHPW - Change Password\nENTR - Enter     | TRNSF - Transfer";
            } else {
                bank.withdraw(amount);
                message = "Withdraw Successful";
                result  = "Withdrawn: £" + amount;
                depth++;
            }
        }
    } else {
        reset("You are not logged in");
    }
    update();
    }

    // Handle the Deposit button:
    // - If the user is logged in, deposit the amount entered into the bank
    // - Reads the amount from numberPadInput, validates it, and updates messages/results accordingly
    // - Otherwise, reset the ATM and display an error message
    public void processDeposit() {
    if (state.equals(STATE_LOGGED_IN)) {
        int amount = parseValidAmount(numberPadInput);
        numberPadInput = "";

        if (amount <= 0) {
            message = "Invalid Amount";
            result  = "Please select an option:\n\nFIN - Finish     | DEP - Deposit\nCLR - Clear      | W/D - Withdraw\nBAL - Balance    | NEWACC - New Account\nBACK - Back      | CHPW - Change Password\nENTR - Enter     | TRNSF - Transfer";
        } else {
            String error = bank.getDepositError(amount); // ask Bank why it would fail

            if (error != null) {
                message = "Deposit Failed: " + error;   // specific reason shown to user
                result  = "Please select an option:\n\nFIN - Finish     | DEP - Deposit\nCLR - Clear      | W/D - Withdraw\nBAL - Balance    | NEWACC - New Account\nBACK - Back      | CHPW - Change Password\nENTR - Enter     | TRNSF - Transfer";
            } else {
                bank.deposit(amount);
                message = "Deposit Successful";
                result  = "Deposited: £" + amount;
                depth++;
            }
        }
    } else {
        reset("You are not logged in");
    }
    update();
    }   
    
    // Called when user presses "ChPw" button
    public void processChangePassword() {
        if (state.equals(STATE_LOGGED_IN) || state.equals(STATE_TRANSFER_ACC) || state.equals(STATE_NEW_ACC_NUMBER) || state.equals(STATE_CHANGE_PW_NEW) || state.equals(STATE_CHANGE_PW_OLD) ) {
            // User is logged in — start the change password flow
            setState(STATE_CHANGE_PW_OLD);
            numberPadInput = "";
            message = "Change Password";
            result  = "Enter your CURRENT password\nFollowed by \"ENTR\"";
            depth++;
        } else {
            reset("You are not logged in");
        }
        update();
    }

    // Called when user presses "New" button
    public void processCreateAccount() {
        // Anyone can create an account — no need to be logged in
        setState(STATE_NEW_ACC_NUMBER);
        numberPadInput = "";
        newAccNumber   = "";
        newAccPassword = "";
        message = "Create New Account";
        result  = "Enter a new account number\nFollowed by \"ENTR\"";
        update();
    }
    
    // Handle the Finish button:
    // logs the user out and displays the goodbye screen
    public void processFinish() {
        view.goodbye();
        bank.logout();
        depth = 0;
        update();
    }

    // Handle unknown or invalid buttons for the current state:
    // - Reset the ATM and display an "Invalid Command" message
    public void processUnknownKey(String action) {
        reset("Invalid Command");
        update();
    }


    public void processTransferAcc() {
        if  (state.equals(STATE_LOGGED_IN) || state.equals(STATE_CHANGE_PW_NEW) || state.equals(STATE_CHANGE_PW_OLD)) {
            message = "Payment Transfer";
            result = "Pleases enter the recipient's account number\nFollowed by \"ENTR\"";
            setState(STATE_TRANSFER_ACC);
            depth++;
        }
        else {
            message = "You are not logged in";
        }
        update();

    }

    public void processBack(){
        if (depth < 1) {
            setState(STATE_ACCOUNT_NO);
            message = "Welcome to the ATM";
            result = "Enter your account number\nFollowed by \"ENTR\"";
            update();
        }
        else {
            setState(STATE_LOGGED_IN);
            message = "Logged In";
            result = "Please select an option:\n\nFIN - Finish     | DEP - Deposit\nCLR - Clear      | W/D - Withdraw\nBAL - Balance    | NEWACC - New Account\nBACK - Back      | CHPW - Change Password\nENTR - Enter     | TRNSF - Transfer";
            depth = 0;
            update();
        }
    }

    // Notify the View of changes by calling its update method
    private void update() {
        view.update(message,numberPadInput, result);
    }
}

