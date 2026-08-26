import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Scanner;

public class AravII {
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

        List<String> tasks = new ArrayList<>();
        Set<Integer> completedTasks = new HashSet<>();
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            String input = scanner.nextLine();
            if (input.equals("bye")) {
                break;
            } else if (input.equals("list")) {
                for (int i = 0; i < tasks.size(); i++) {
                    String status = completedTasks.contains(i) ? "[X]" : "[ ]";
                    System.out.println((i + 1) + ". " + status + " " + tasks.get(i));
                }
            } else if (input.startsWith("todo ")) {
                tasks.add(input.substring(5));
            } else if (input.startsWith("mark ")) {
                int taskIndex = Integer.parseInt(input.substring(5)) - 1;
                completedTasks.add(taskIndex);
            } else if (input.startsWith("unmark ")) {
                int taskIndex = Integer.parseInt(input.substring(7)) - 1;
                completedTasks.remove(taskIndex);
            } else {
                System.out.println(input);
            }
        }

        System.out.println("Bye. Hope to see you again soon!");
        System.out.println("____________________________________________________________");
    }
}
