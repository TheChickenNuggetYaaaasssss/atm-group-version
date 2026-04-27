package src.main.java.com.atmbanksimulator;


import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.*;
import javafx.stage.Stage;

// ===== 🙂 View (Eyes / Ears / Nose / Mouth / Face) =====

// The View class creates the GUI for the application.
// It does not know anything about business logic;
// it only updates the display when notified by the UIModel.
class View {
    int H = 550;         // Height of window pixels
    int W = 650;         // Width  of window pixels



    Controller controller; // Reference to the Controller (part of the MVC setup)

    // Components (controls and layout) of the user interface
    private Label laMsg;        // Message label, e.g. shows "Welcome to ATM" at startup (not the window title)
    private TextField tfInput;  // Input field where numbers typed on the keypad appear
    private TextArea taResult;  // Output area where instructions and results are displayed
    private ScrollPane scrollPane; // Provides scrollbars around the TextArea
    private GridPane grid;      // Main layout container (grid-based)
    private TilePane buttonPane;// Container for ATM keypad buttons (tiled layout)
    private Stage primaryStage;


    public void welcome(Stage window) {

        this.primaryStage = window;

        VBox root = new VBox(20); // sets up the screen layout
        root.setId("welcome_screen"); // ID to be used in CSS design
        root.setAlignment(Pos.CENTER); // aligns items in screen layout to the centre
        root.setPadding(new Insets(40)); // adds padding around elements

        Label bankName = new Label("Welcome to the ATM Bank Simulator"); // text for the welcome screen
        bankName.setId("welcome_title");

        Label hint = new Label("Press START to begin"); // text for the welcome screen
        hint.setId("welcome_hint");
        hint.setTextAlignment(TextAlignment.CENTER);

        Button startBtn = new Button("START"); // button to take user to main application
        startBtn.setId("welcome_btn");
        startBtn.setOnAction(e -> view_start_atm(window));

        root.getChildren().addAll(bankName, hint, startBtn); // adds all elements to screen layout

        Scene welcomeScene = new Scene(root, W, H);
        welcomeScene.getStylesheets().add("src/main/resources/atm.css");

        window.setMinWidth(665); //setting the min width for the application
        window.setMinHeight(580); //setting the min height for the application
        window.setTitle("ATM Simulator"); //setting the window title
        window.setScene(welcomeScene);
        window.show();

    }

    // transitions between welcome and main application
    private void view_start_atm(Stage window) {
        start(window);  // calls the start() method in View
        controller.UIModel.initialise();  // re-initialise model state
    }

    public void goodbye() {
        goodbye(primaryStage);
    }

    public void goodbye(Stage window) {

        VBox root = new VBox(24); // sets up the screen layout
        root.setId("goodbye_screen"); // ID to be used in CSS design
        root.setAlignment(Pos.CENTER); // aligns items in screen layout to the centre
        root.setPadding(new Insets(40)); // adds padding around elements

        Label thankYou = new Label("Thank you for banking\nwith the ATM Bank Simulator!"); // text to display on goodbye screen
        thankYou.setId("goodbye_title");
        thankYou.setTextAlignment(TextAlignment.CENTER);

        Label reminder = new Label("Press HOME to return to start"); // text to display on goodbye screen
        reminder.setId("goodbye_hint");
        reminder.setTextAlignment(TextAlignment.CENTER);

        Button homeBtn = new Button("HOME"); // button to send user back to welcome screen
        homeBtn.setId("home_btn");
        homeBtn.setOnAction(e -> welcome(window));

        root.getChildren().addAll(thankYou, reminder, homeBtn); // adding all elements to screen

        Scene goodbyeScene = new Scene(root, W, H);
        goodbyeScene.getStylesheets().add("src/main/resources/atm.css");
        window.setScene(goodbyeScene);
        window.setTitle("ATM Simulator");
    }



    // start() is called from Main to set up the UI.
    // Important: create controls here (not in the constructor or as field initializers),
    // so that everything is initialized in the correct order.
    public void start(Stage window) {
        // Create the user interface component objects.
        // The ATM UI is organized as a vertical grid with four main parts:
        // 1. A message label
        // 2. A text field showing numbers
        // 3. A text area showing transaction results, summaries, and user instructions
        // 4. A tiled panel of buttons
        grid = new GridPane(); // top layout
        grid.setId("Layout");  // assign an id to be used in css file
        buttonPane = new TilePane(); //
        buttonPane.setId("Buttons"); // assign an id to be used in css file
        grid.setAlignment(Pos.CENTER);

        // controls
        laMsg = new Label("Welcome to Bank-ATM");  // Message bar at the top
        grid.add(laMsg, 0, 0);         // Add to GUI at the top

        tfInput = new TextField();     // text field for numbers
        tfInput.setEditable(false);     // Read only (user can't type in)
        grid.add(tfInput, 0, 1);    // Add to GUI on second row

        taResult = new TextArea();         // text area for instructions, transaction results
        taResult.setEditable(false);       // Read only
        scrollPane  = new ScrollPane();    // create a scrolling window
        scrollPane.setContent(taResult);   // put the text area 'inside' the scrolling window
        grid.add( scrollPane, 0, 2);    // add the scrolling window to GUI on third row

        // Define the button layout as a 2D array of text labels.
        // Empty strings ("") represent blank spaces in the grid.
            
        String buttonTexts[][] = {
                {"7", "8", "9", "", "DEP", "W/D"},
                {"4", "5", "6", "", "NEWACC", "CHPW"},
                {"1", "2", "3", "", "BAL", "TRNSF"},
                {"", "0", "", "",   "", ""},
                {"CLR", "", "ENTR", "", "BACK", "FIN"}
            };

        // Build the button panel, loop through the array,
        // - For non-empty strings, create a Button
        // - For empty strings, add an empty Text element as a spacer
        // Add all elements to the buttonPane (a tiled pane),
        // then place the buttonPane into the main grid as the fourth row.
        for ( String[] row: buttonTexts ) {
            for (String text: row) {
                if ( text.length() >= 1 ) {
                    // non-empty string - make a button
                    Button btn = new Button( text );
                    btn.setOnAction( this::buttonClicked );
                              // Register event handler: call buttonClicked() whenever this button is pressed
                    buttonPane.getChildren().add( btn );    // add this button to tiled pane
                } else {
                    // empty string - make an empty Text element as a spacer
                    buttonPane.getChildren().add( new Text() );
                }
            }
        }
        grid.add(buttonPane,0,3); // add the tiled pane of buttons to the main grid

        // add the complete GUI to the window and display it

        Scene scene = new Scene(grid, W, H);
        window.setMinWidth(665);
        window.setMinHeight(580);
        scene.getStylesheets().add("src/main/resources/atm.css"); // tell to use our css file
        window.setScene(scene);
        window.setTitle("ATM Simulator"); //set window title
        window.show();
    }

    // This is how the View talks to the Controller
    // This method is called when a button is pressed
    // It fetches the label on the button and passes it to the controller's process method
    private void buttonClicked(ActionEvent event) {
        // this line asks the event to provide the actual Button object that was clicked
        Button b = ((Button) event.getSource());
        String text = b.getText();   // get the button label
        System.out.println( "View::buttonClicked: label = "+ text );
        controller.process( text );  // Pass it to the controller's process method
    }

    // This method is called by the UIModel whenever the UIModel changes.
    // It receives updated information from the UIModel and displays them in the GUI.
    // - msg → shown in the top message label
    // - tfInputMsg → shown in the text field (user input area)
    // - taResultMsg → shown in the text area (instructions / results)
    public void update(String msg,String tfInputMsg,String taResultMsg)
    {
        laMsg.setText(msg);
        tfInput.setText(tfInputMsg);
        taResult.setText(taResultMsg);
    }
}
