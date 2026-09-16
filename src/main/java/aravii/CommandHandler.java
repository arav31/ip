package aravii;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/** Parses and executes commands shared by the text and graphical interfaces. */
public class CommandHandler {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE;

    private static final DateTimeFormatter DATE_TIME_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    /** Returns the command reference shown by the help command.
     *
     * @return the available command formats
     */
    public String helpMessage() {
        return "Available commands:\n"
                + "todo <description>\n"
                + "deadline <description> /by <YYYY-MM-DD>\n"
                + "event <description> /from <YYYY-MM-DD HH:MM> /to <YYYY-MM-DD HH:MM>\n"
                + "list\nmark <number>\nunmark <number>\ndelete <number>\n"
                + "find <keyword>\nsort\nhelp\nbye";
    }

    /** Executes a supported command and returns its response.
     *
     * @param tasks the current task list
     * @param input the command to execute
     * @return the command response
     */
    public String execute(TaskList tasks, String input) {
        if (input.equals("help")) {
            return helpMessage();
        } else if (input.equals("list")) {
            String result = tasks.formatAll();
            return result.isEmpty() ? "There are no tasks." : result.trim();
        } else if (input.equals("sort")) {
            tasks.sortByDescription();
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
            return addDeadline(tasks, input);
        } else if (input.startsWith("event ")) {
            return addEvent(tasks, input);
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

    /** Adds a deadline task from its command input.
     *
     * @param tasks the current task list
     * @param input the deadline command
     * @return the command response
     */
    private String addDeadline(TaskList tasks, String input) {
        int byIndex = input.indexOf(" /by ");
        if (byIndex < 0) {
            throw new IllegalArgumentException("A deadline must include /by followed by a date.");
        }
        String description = requireValue(input.substring(9, byIndex));
        String date = parseDate(requireValue(input.substring(byIndex + 5)));
        Task task = new Task(TaskType.DEADLINE, description, "(by: " + date + ")");
        tasks.add(task);
        return "Added: " + task;
    }

    /** Adds an event task from its command input.
     *
     * @param tasks the current task list
     * @param input the event command
     * @return the command response
     */
    private String addEvent(TaskList tasks, String input) {
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
