package aravii;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

/** Owns the collection of tasks and its persistent storage. */
public class TaskList {
    private final List<Task> tasks = new ArrayList<>();

    /** Loads tasks from disk, or creates an empty list when no save exists.
     *
     * @param dataFile the file containing the saved tasks
     * @return the loaded task list
     */
    public static TaskList load(Path dataFile) {
        TaskList taskList = new TaskList();
        if (!Files.exists(dataFile)) {
            return taskList;
        }

        try {
            List<Task> loadedTasks = new ArrayList<>();
            for (String line : Files.readAllLines(dataFile)) {
                loadedTasks.add(Task.deserialize(line));
            }
            taskList.tasks.addAll(loadedTasks);
        } catch (IOException | IllegalArgumentException exception) {
            System.out.println("Error: Could not load saved tasks.");
        }
        return taskList;
    }

    /** Saves all tasks to disk.
     *
     * @param dataFile the file in which to save the tasks
     */
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

    /** Adds one or more tasks to the list.
     *
     * @param newTasks the tasks to add
     */
    public void add(Task... newTasks) {
        for (Task task : newTasks) {
            assert task != null : "Task list must not contain null tasks";
            tasks.add(task);
        }
    }

    /** Returns the task selected by a one-based task number.
     *
     * @param taskNumber the one-based task number
     * @return the selected task
     */
    public Task get(String taskNumber) {
        try {
            int index = Integer.parseInt(taskNumber) - 1;
            if (index < 0 || index >= tasks.size()) {
                throw new IllegalArgumentException("That task number does not exist.");
            }
            assert tasks.get(index) != null : "Task list must not contain null tasks";
            return tasks.get(index);
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Please provide a valid task number.");
        }
    }

    /** Removes and returns the task selected by a one-based task number.
     *
     * @param taskNumber the one-based task number
     * @return the removed task
     */
    public Task remove(String taskNumber) {
        Task task = get(taskNumber);
        tasks.remove(task);
        return task;
    }

    /** Prints all tasks with one-based numbering. */
    public void printAll() {
        System.out.print(formatAll());
    }

    /** Prints all tasks whose description or details contain the given keyword.
     *
     * @param keyword the keyword to search for
     */
    public void printMatching(String keyword) {
        System.out.print(formatMatching(keyword));
    }

    /** Formats all tasks with one-based numbering.
     *
     * @return the formatted task list
     */
    public String formatAll() {
        StringBuilder output = new StringBuilder();
        for (int i = 0; i < tasks.size(); i++) {
            output.append(i + 1).append(". ").append(tasks.get(i)).append("\n");
        }
        return output.toString();
    }

    /** Formats tasks whose descriptions or details contain the given keyword.
     *
     * @param keyword the keyword to search for
     * @return the matching tasks
     */
    public String formatMatching(String keyword) {
        StringBuilder output = new StringBuilder();
        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.get(i).matches(keyword)) {
                output.append(i + 1).append(". ").append(tasks.get(i)).append("\n");
            }
        }
        return output.toString();
    }
}
