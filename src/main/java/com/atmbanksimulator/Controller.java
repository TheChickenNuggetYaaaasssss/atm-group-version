package src.main.java.com.atmbanksimulator;

// ===== ⚡ Controller (Nerves) =====

// The Controller receives user actions from the View and delegates the appropriate tasks to the UIModel.
// Its main job is to decide what to do based on the user input.
public class Controller {

    UIModel UIModel; // Reference to the UIModel (part of the MVC setup)

    // The process method is called by the View in response to user interface events.
    // It uses a switch statement to determine which UIModel method should be called,
    // and delegates the task accordingly.
    void process( String action ) {
        switch (action) {
            case "1" : case "2" : case "3" : case "4" : case "5" :
            case "6" : case "7" : case "8" : case "9" : case "0" :
                UIModel.processNumber(action);
                break;
            case "CLR":
                UIModel.processClear();
                break;
            case "ENTR":
                UIModel.processEnter();
                break;
            case "W/D":
                UIModel.processWithdraw();
                break;
            case "DEP":
                UIModel.processDeposit();
                break;
            case "BAL":
                UIModel.processBalance();
                break;
            case "FIN":
                UIModel.processFinish();
                break;
            
            // Starting week 4 lab
            case "CHPW":
                UIModel.processChangePassword();
                break;
            case "NEWACC":
                UIModel.processCreateAccount();
                break;
            default:
                UIModel.processUnknownKey(action);
                break;

            // week 6 lab
            case "TRNSF":
                UIModel.processTransferAcc();
                break;

            case "BACK":
                UIModel.processBack();
                break;
        }
    }

}


