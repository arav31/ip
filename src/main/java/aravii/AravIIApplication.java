package aravii;

import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

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

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE;

    private static final DateTimeFormatter DATE_TIME_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private static final String WELCOME_MESSAGE = "Hello! I'm Arav (II).\n"
            + "What can I do for you?\n"
            + "Type a command below, or type help to see the available commands.";

    private TextArea conversation;

    private TaskList tasks;

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
        if (command.equals("help")) {
            conversation.appendText("\nArav (II): " + helpMessage());
            input.clear();
            return;
        }
        try {
            String response = executeCommand(command);
            conversation.appendText("\nArav (II): " + response);
            tasks.save(DATA_FILE);
        } catch (IllegalArgumentException exception) {
            conversation.appendText("\nArav (II): Error: " + exception.getMessage());
        }
        input.clear();
    }

    /** Returns the command reference shown by the help command.
     *
     * @return the available command formats
     */
    private String helpMessage() {
        return "Available commands:\n"
                + "todo <description>\n"
                + "deadline <description> /by <YYYY-MM-DD>\n"
                + "event <description> /from <YYYY-MM-DD HH:MM> /to <YYYY-MM-DD HH:MM>\n"
                + "list\nmark <number>\nunmark <number>\ndelete <number>\n"
                + "find <keyword>\nhelp\nbye";
    }

    /** Executes a supported command and returns its response.
     *
     * @param input the command to execute
     * @return the command response
     */
    private String executeCommand(String input) {
        if (input.equals("list")) {
            String result = tasks.formatAll();
            return result.isEmpty() ? "There are no tasks." : result.trim();
        } else if (input.startsWith("find ")) {
            String keyword = requireValue(input.substring(5));
            String result = tasks.formatMatching(keyword);
            return result.isEmpty() ? "No matching tasks found." : result.trim();
        } else if (input.startsWith("todo ")) {
            Task task = new Task(TaskType.TODO, requireValue(input.substring(5)), "");
            tasks.add(task);
            return "Added: " + task;
        } else if (input.startsWith("deadline ")) {
            int byIndex = input.indexOf(" /by ");
            if (byIndex < 0) {
                throw new IllegalArgumentException("A deadline must include /by followed by a date.");
            }
            String description = requireValue(input.substring(9, byIndex));
            String date = parseDate(requireValue(input.substring(byIndex + 5)));
            Task task = new Task(TaskType.DEADLINE, description, "(by: " + date + ")");
            tasks.add(task);
            return "Added: " + task;
        } else if (input.startsWith("event ")) {
            return addEvent(input);
        } else if (input.startsWith("mark ")) {
            Task task = tasks.get(input.substring(5));
            task.mark();
            return "Marked: " + task;
        } else if (input.startsWith("unmark ")) {
            Task task = tasks.get(input.substring(7));
            task.unmark();
            return "Unmarked: " + task;
        } else if (input.startsWith("delete ")) {
            return "Deleted: " + tasks.remove(input.substring(7));
        }
        throw new IllegalArgumentException("I don't recognise that command.");
    }

    /** Adds an event task from its command input.
     *
     * @param input the event command
     * @return the command response
     */
    private String addEvent(String input) {
        int fromIndex = input.indexOf(" /from ");
        int toIndex = input.indexOf(" /to ");
        if (fromIndex < 0 || toIndex < 0 || toIndex < fromIndex) {
            throw new IllegalArgumentException("An event must include /from and /to followed by times.");
        }
        String description = requireValue(input.substring(6, fromIndex));
        String from = parseDateTime(requireValue(input.substring(fromIndex + 7, toIndex)));
        String to = parseDateTime(requireValue(input.substring(toIndex + 5)));
        Task task = new Task(TaskType.EVENT, description, "(from: " + from + " to: " + to + ")");
        tasks.add(task);
        return "Added: " + task;
    }

    /** Rejects an empty command argument.
     *
     * @param value the value to validate
     * @return the validated value
     */
    private String requireValue(String value) {
        if (value.isBlank()) {
            throw new IllegalArgumentException("The value cannot be empty.");
        }
        return value;
    }

    /** Validates a date in YYYY-MM-DD format.
     *
     * @param date the date to validate
     * @return the normalized date
     */
    private String parseDate(String date) {
        try {
            return LocalDate.parse(date.trim(), DATE_FORMAT).format(DATE_FORMAT);
        } catch (DateTimeParseException exception) {
            throw new IllegalArgumentException("Use dates in YYYY-MM-DD format.");
        }
    }

    /** Validates a date and time in YYYY-MM-DD HH:MM format.
     *
     * @param dateTime the date and time to validate
     * @return the normalized date and time
     */
    private String parseDateTime(String dateTime) {
        try {
            return LocalDateTime.parse(dateTime.trim(), DATE_TIME_FORMAT).format(DATE_TIME_FORMAT);
        } catch (DateTimeParseException exception) {
            throw new IllegalArgumentException("Use date and time in YYYY-MM-DD HH:MM format.");
        }
    }
}
