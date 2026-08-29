import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

/** Owns the collection of tasks and its persistent storage. */
public class TaskList {
    private final List<Task> tasks = new ArrayList<>();

    /** Loads tasks from disk, or creates an empty list when no save exists. */
    public static TaskList load(Path dataFile) {
        TaskList taskList = new TaskList();
        if (!Files.exists(dataFile)) {
            return taskList;
        }

        try {
            for (String line : Files.readAllLines(dataFile)) {
                taskList.tasks.add(Task.deserialize(line));
            }
        } catch (IOException | IllegalArgumentException exception) {
            System.out.println("Error: Could not load saved tasks.");
        }
        return taskList;
    }

    /** Saves all tasks to disk. */
    public void save(Path dataFile) {
        List<String> lines = new ArrayList<>();
        for (Task task : tasks) {
            lines.add(task.serialize());
        }

        try {
            Files.createDirectories(dataFile.getParent());
            Files.write(dataFile, lines, StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException exception) {
            System.out.println("Error: Could not save tasks.");
        }
    }

    /** Adds a task to the list. */
    public void add(Task task) {
        tasks.add(task);
    }

    /** Returns the task selected by a one-based task number. */
    public Task get(String taskNumber) {
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

    /** Removes and returns the task selected by a one-based task number. */
    public Task remove(String taskNumber) {
        Task task = get(taskNumber);
        tasks.remove(task);
        return task;
    }

    /** Prints all tasks with one-based numbering. */
    public void printAll() {
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println((i + 1) + ". " + tasks.get(i));
        }
    }
}
