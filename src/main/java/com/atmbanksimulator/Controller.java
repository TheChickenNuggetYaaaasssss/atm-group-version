package src.main.java.com.atmbanksimulator;
import javax.sound.midi.*;
import java.io.IOException;

// ===== ⚡ Controller (Nerves) =====

// The Controller receives user actions from the View and delegates the appropriate tasks to the UIModel.
// Its main job is to decide what to do based on the user input.
public class Controller {

    UIModel UIModel; // Reference to the UIModel (part of the MVC setup)

    // The process method is called by the View in response to user interface events.
    // It uses a switch statement to determine which UIModel method should be called,
    // and delegates the task accordingly.
    void process( String action ) throws IOException {
        try{
        //allows the program to play sounds
        Synthesizer press = MidiSystem.getSynthesizer(); 
        press.open();
    
        //grabs sounds from java databases
        Instrument[] instr = press.getDefaultSoundbank().getInstruments();
        MidiChannel[] mChannels = press.getChannels();
        press.loadInstrument(instr[37]);//loads instrument
    
        switch (action) {
            case "1" : case "2" : case "3" : case "4" : case "5" :
            case "6" : case "7" : case "8" : case "9" : case "0" :
                UIModel.processNumber(action);
                mChannels[9].noteOn(62, 40);//plays sound 62 on channel 9, volume 40
                break;
            case "CLR":
                UIModel.processClear();
                mChannels[9].noteOn(62, 40);
                break;
            case "ENTR":
                UIModel.processEnter();
                mChannels[9].noteOn(62, 40);
                break;
            case "W/D":
                UIModel.processWithdraw();
                mChannels[9].noteOn(62, 40);
                break;
            case "DEP":
                UIModel.processDeposit();
                mChannels[9].noteOn(62, 40);
                break;
            case "BAL":
                UIModel.processBalance();
                mChannels[9].noteOn(62, 40);
                break;
            case "FIN":
                UIModel.processFinish();
                mChannels[9].noteOn(62, 40);
                break;
            
            // Starting week 4 lab
            case "CHPW":
                UIModel.processChangePassword();
                mChannels[9].noteOn(62, 40);
                break;
            case "NEWACC":
                UIModel.processCreateAccount();
                mChannels[9].noteOn(62, 40);
                break;
            default:
                UIModel.processUnknownKey(action);
                mChannels[9].noteOn(62, 40);
                break;

            // week 6 lab
            case "TRNSF":
                UIModel.processTransferAcc();
                mChannels[9].noteOn(62, 40);
                break;

            case "BACK":
                UIModel.processBack();
                mChannels[9].noteOn(62, 40);
                break;
                
            // week 8 lab
            case "RCNT":
                UIModel.processRecent();
                mChannels[9].noteOn(62,40);
                break;
        }
      } catch (MidiUnavailableException e) {
         e.printStackTrace();
      }
    }

}


