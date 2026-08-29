import java.nio.file.Path;
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

        TaskList tasks = TaskList.load(DATA_FILE);
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            String input = scanner.nextLine();
            try {
                if (input.equals("bye")) {
                    tasks.save(DATA_FILE);
                    break;
                } else if (input.equals("list")) {
                    tasks.printAll();
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
                    tasks.add(new Task(TaskType.EVENT,
                            description, "(from: " + from + " to: " + to + ")"));
                } else if (input.startsWith("mark ")) {
                    tasks.get(input.substring(5)).mark();
                } else if (input.startsWith("unmark ")) {
                    tasks.get(input.substring(7)).unmark();
                } else if (input.startsWith("delete ")) {
                    Task deletedTask = tasks.remove(input.substring(7));
                    System.out.println("Deleted: " + deletedTask);
                } else {
                    throw new IllegalArgumentException("I don't recognise that command.");
                }
            } catch (IllegalArgumentException exception) {
                System.out.println("Error: " + exception.getMessage());
            }
            tasks.save(DATA_FILE);
        }

        System.out.println("Bye. Hope to see you again soon!");
        System.out.println("____________________________________________________________");
    }
}
