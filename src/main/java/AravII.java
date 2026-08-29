import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public class AravII {
    private static final Path DATA_FILE = Path.of("data", "aravii.txt");
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final DateTimeFormatter DATE_TIME_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    /** Identifies the supported task categories and their display symbols. */
    private enum TaskType {
        TODO("T"),
        DEADLINE("D"),
        EVENT("E");

        private final String symbol;

        TaskType(String symbol) {
            this.symbol = symbol;
        }
    }

    /** Represents a task and its optional deadline or event details. */
    private static class Task {
        private final TaskType type;
        private final String description;
        private final String details;
        private boolean completed;

        Task(TaskType type, String description, String details) {
            this.type = type;
            this.description = description;
            this.details = details;
        }

        /** Returns the task in the format shown by the list command. */
        @Override
        public String toString() {
            String status = completed ? "[X]" : "[ ]";
            String taskDetails = details.isEmpty() ? "" : " " + details;
            return "[" + type.symbol + "] " + status + " " + description + taskDetails;
        }
    }

    /** Returns the task selected by a one-based task number. */
    private static Task getTask(List<Task> tasks, String taskNumber) {
        try {
            int index = Integer.parseInt(taskNumber) - 1;
            if (index < 0 || index >= tasks.size()) {
                throw new IllegalArgumentException("That task number does not exist.");
            }
            return tasks.get(index);
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Please provide a valid task number.");
        }
    }

    /** Rejects an empty task description. */
    private static String requireDescription(String description) {
        if (description.isBlank()) {
            throw new IllegalArgumentException("The task description cannot be empty.");
        }
        return description;
    }

    /** Validates and normalises a deadline date in YYYY-MM-DD format. */
    private static String parseDate(String date) {
        try {
            return LocalDate.parse(date.trim(), DATE_FORMAT).format(DATE_FORMAT);
        } catch (DateTimeParseException exception) {
            throw new IllegalArgumentException("Use dates in YYYY-MM-DD format.");
        }
    }

    /** Validates and normalises an event date and time in YYYY-MM-DD HH:MM format. */
    private static String parseDateTime(String dateTime) {
        try {
            return LocalDateTime.parse(dateTime.trim(), DATE_TIME_FORMAT).format(DATE_TIME_FORMAT);
        } catch (DateTimeParseException exception) {
            throw new IllegalArgumentException("Use date and time in YYYY-MM-DD HH:MM format.");
        }
    }

    /** Loads previously saved tasks, returning an empty list if no save exists yet. */
    private static List<Task> loadTasks() {
        List<Task> tasks = new ArrayList<>();
        if (!Files.exists(DATA_FILE)) {
            return tasks;
        }

        try {
            for (String line : Files.readAllLines(DATA_FILE)) {
                String[] fields = line.split("\\t", -1);
                if (fields.length != 4) {
                    continue;
                }
                Task task = new Task(TaskType.valueOf(fields[0]), fields[2], fields[3]);
                task.completed = Boolean.parseBoolean(fields[1]);
                tasks.add(task);
            }
        } catch (IOException | IllegalArgumentException exception) {
            System.out.println("Error: Could not load saved tasks.");
        }
        return tasks;
    }

    /** Saves all current tasks so they can be restored in the next session. */
    private static void saveTasks(List<Task> tasks) {
        List<String> lines = new ArrayList<>();
        for (Task task : tasks) {
            lines.add(task.type.name() + "\t"
                    + task.completed + "\t"
                    + task.description + "\t"
                    + task.details);
        }

        try {
            Files.createDirectories(DATA_FILE.getParent());
            Files.write(DATA_FILE, lines, StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException exception) {
            System.out.println("Error: Could not save tasks.");
        }
    }

    public static void main(String[] args) {
        String banner = "____________________________________________________________\n"
                + "     _    ____      _       __      __\n"
                + "    / \\  |  _ \\    / \\      \\ \\    / /\n"
                + "   / _ \\ | |_) |  / _ \\      \\ \\  / / \n"
                + "  / ___ \\|  _ <  / ___ \\      \\ \\/ /  \n"
                + " /_/   \\_\\_| \\_\\/_/   \\_\\      \\__/   \n"
                + "             ( II )\n"
                + "Hello! I'm Arav (II).\n"
                + "What can I do for you?\n"
                + "____________________________________________________________";
        System.out.println(banner);

        List<Task> tasks = loadTasks();
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            String input = scanner.nextLine();
            try {
                if (input.equals("bye")) {
                    saveTasks(tasks);
                    break;
                } else if (input.equals("list")) {
                    for (int i = 0; i < tasks.size(); i++) {
                        System.out.println((i + 1) + ". " + tasks.get(i));
                    }
                } else if (input.startsWith("todo ")) {
                    tasks.add(new Task(TaskType.TODO, requireDescription(input.substring(5)), ""));
                } else if (input.startsWith("deadline ")) {
                    int byIndex = input.indexOf(" /by ");
                    if (byIndex < 0) {
                        throw new IllegalArgumentException("A deadline must include /by followed by a date.");
                    }
                    String description = requireDescription(input.substring(9, byIndex));
                    String date = parseDate(requireDescription(input.substring(byIndex + 5)));
                    tasks.add(new Task(TaskType.DEADLINE, description, "(by: " + date + ")"));
                } else if (input.startsWith("event ")) {
                    int fromIndex = input.indexOf(" /from ");
                    int toIndex = input.indexOf(" /to ");
                    if (fromIndex < 0 || toIndex < 0 || toIndex < fromIndex) {
                        throw new IllegalArgumentException(
                                "An event must include /from and /to followed by times.");
                    }
                    String description = requireDescription(input.substring(6, fromIndex));
                    String from = parseDateTime(requireDescription(input.substring(fromIndex + 7, toIndex)));
                    String to = parseDateTime(requireDescription(input.substring(toIndex + 5)));
                    tasks.add(new Task(TaskType.EVENT, description,
                            "(from: " + from + " to: " + to + ")"));
                } else if (input.startsWith("mark ")) {
                    getTask(tasks, input.substring(5)).completed = true;
                } else if (input.startsWith("unmark ")) {
                    getTask(tasks, input.substring(7)).completed = false;
                } else if (input.startsWith("delete ")) {
                    Task deletedTask = getTask(tasks, input.substring(7));
                    tasks.remove(deletedTask);
                    System.out.println("Deleted: " + deletedTask);
                } else {
                    throw new IllegalArgumentException("I don't recognise that command.");
                }
            } catch (IllegalArgumentException exception) {
                System.out.println("Error: " + exception.getMessage());
            }
            saveTasks(tasks);
        }

        System.out.println("Bye. Hope to see you again soon!");
        System.out.println("____________________________________________________________");
    }
}
