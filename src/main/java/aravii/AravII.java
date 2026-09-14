package aravii;

import java.nio.file.Path;
import java.util.Scanner;

/** Runs the Arav II command-line task manager. */
public class AravII {
    /** Location of the file used to persist tasks between runs. */
    private static final Path DATA_FILE = Path.of("data", "aravii.txt");

    /** Starts the chatbot and processes commands until the user enters {@code bye}.
     *
     * @param args command-line arguments, which are not used
     */
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
        CommandHandler commandHandler = new CommandHandler();
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            String input = scanner.nextLine();
            try {
                if (input.equals("bye")) {
                    tasks.save(DATA_FILE);
                    break;
                } else {
                    System.out.println(commandHandler.execute(tasks, input));
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
