import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class AravII {
    /** Represents a task and its optional deadline or event details. */
    private static class Task {
        private final String type;
        private final String description;
        private final String details;
        private boolean completed;

        Task(String type, String description, String details) {
            this.type = type;
            this.description = description;
            this.details = details;
        }

        /** Returns the task in the format shown by the list command. */
        @Override
        public String toString() {
            String status = completed ? "[X]" : "[ ]";
            String taskDetails = details.isEmpty() ? "" : " " + details;
            return "[" + type + "] " + status + " " + description + taskDetails;
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

        List<Task> tasks = new ArrayList<>();
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            String input = scanner.nextLine();
            try {
                if (input.equals("bye")) {
                    break;
                } else if (input.equals("list")) {
                    for (int i = 0; i < tasks.size(); i++) {
                        System.out.println((i + 1) + ". " + tasks.get(i));
                    }
                } else if (input.startsWith("todo ")) {
                    tasks.add(new Task("T", requireDescription(input.substring(5)), ""));
                } else if (input.startsWith("deadline ")) {
                    int byIndex = input.indexOf(" /by ");
                    if (byIndex < 0) {
                        throw new IllegalArgumentException("A deadline must include /by followed by a date.");
                    }
                    String description = requireDescription(input.substring(9, byIndex));
                    String date = requireDescription(input.substring(byIndex + 5));
                    tasks.add(new Task("D", description, "(by: " + date + ")"));
                } else if (input.startsWith("event ")) {
                    int fromIndex = input.indexOf(" /from ");
                    int toIndex = input.indexOf(" /to ");
                    if (fromIndex < 0 || toIndex < 0 || toIndex < fromIndex) {
                        throw new IllegalArgumentException(
                                "An event must include /from and /to followed by times.");
                    }
                    String description = requireDescription(input.substring(6, fromIndex));
                    String from = requireDescription(input.substring(fromIndex + 7, toIndex));
                    String to = requireDescription(input.substring(toIndex + 5));
                    tasks.add(new Task("E", description,
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
        }

        System.out.println("Bye. Hope to see you again soon!");
        System.out.println("____________________________________________________________");
    }
}
