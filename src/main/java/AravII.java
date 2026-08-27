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
            if (input.equals("bye")) {
                break;
            } else if (input.equals("list")) {
                for (int i = 0; i < tasks.size(); i++) {
                    System.out.println((i + 1) + ". " + tasks.get(i));
                }
            } else if (input.startsWith("todo ")) {
                tasks.add(new Task("T", input.substring(5), ""));
            } else if (input.startsWith("deadline ")) {
                int byIndex = input.indexOf(" /by ");
                String description = input.substring(9, byIndex);
                String date = input.substring(byIndex + 5);
                tasks.add(new Task("D", description, "(by: " + date + ")"));
            } else if (input.startsWith("event ")) {
                int fromIndex = input.indexOf(" /from ");
                int toIndex = input.indexOf(" /to ");
                String description = input.substring(6, fromIndex);
                String from = input.substring(fromIndex + 7, toIndex);
                String to = input.substring(toIndex + 5);
                tasks.add(new Task("E", description, "(from: " + from + " to: " + to + ")"));
            } else if (input.startsWith("mark ")) {
                int taskIndex = Integer.parseInt(input.substring(5)) - 1;
                tasks.get(taskIndex).completed = true;
            } else if (input.startsWith("unmark ")) {
                int taskIndex = Integer.parseInt(input.substring(7)) - 1;
                tasks.get(taskIndex).completed = false;
            } else {
                System.out.println(input);
            }
        }

        System.out.println("Bye. Hope to see you again soon!");
        System.out.println("____________________________________________________________");
    }
}
