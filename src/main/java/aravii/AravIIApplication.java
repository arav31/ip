package aravii;

import java.nio.file.Path;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/** Displays the Arav II task manager in a JavaFX window. */
public class AravIIApplication extends Application {
    private static final Path DATA_FILE = Path.of("data", "aravii.txt");

    private static final String WELCOME_MESSAGE = "Hello! I'm Arav (II).\n"
            + "What can I do for you?\n"
            + "Type a command below, or type help to see the available commands.";

    private TextArea conversation;

    private TaskList tasks;

    private final CommandHandler commandHandler = new CommandHandler();

    /** Creates and displays the main application window.
     *
     * @param stage the primary JavaFX window
     */
    @Override
    public void start(Stage stage) {
        tasks = TaskList.load(DATA_FILE);
        conversation = new TextArea(WELCOME_MESSAGE);
        conversation.setEditable(false);
        conversation.setWrapText(true);
        conversation.setPrefHeight(360);

        TextField input = new TextField();
        input.setPromptText("Enter a command");
        Button sendButton = new Button("Send");
        sendButton.setDefaultButton(true);
        sendButton.setOnAction(event -> handleInput(input));

        HBox inputRow = new HBox(8, input, sendButton);
        HBox.setHgrow(input, javafx.scene.layout.Priority.ALWAYS);

        Label title = new Label("Arav (II) Task Manager");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        VBox content = new VBox(12, title, conversation, inputRow);
        content.setPadding(new Insets(16));

        BorderPane root = new BorderPane(content);
        Scene scene = new Scene(root, 560, 460);
        stage.setTitle("Arav (II)");
        stage.setScene(scene);
        stage.show();
        input.requestFocus();
    }

    /** Displays a response to a command entered by the user.
     *
     * @param input the text field containing the command
     */
    private void handleInput(TextField input) {
        String command = input.getText().trim();
        if (command.isEmpty()) {
            return;
        }
        conversation.appendText("\n\nYou: " + command);
        if (command.equals("bye")) {
            conversation.appendText("\nArav (II): Bye. Hope to see you again soon!");
            tasks.save(DATA_FILE);
            input.clear();
            input.setDisable(true);
            Platform.exit();
            return;
        }
        try {
            String response = commandHandler.execute(tasks, command);
            conversation.appendText("\nArav (II): " + response);
            tasks.save(DATA_FILE);
        } catch (IllegalArgumentException exception) {
            conversation.appendText("\nArav (II): Error: " + exception.getMessage());
        }
        input.clear();
    }

}
