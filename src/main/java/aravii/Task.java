package aravii;

/** Represents a task and its optional deadline or event details. */
public class Task {
    private final TaskType type;
    private final String description;
    private final String details;
    private boolean completed;

    public Task(TaskType type, String description, String details) {
        this.type = type;
        this.description = description;
        this.details = details;
    }

    /** Marks this task as completed. */
    public void mark() {
        completed = true;
    }

    /** Marks this task as not completed. */
    public void unmark() {
        completed = false;
    }

    /** Serialises this task for storage in the save file. */
    public String serialize() {
        return type.name() + "\t" + completed + "\t" + description + "\t" + details;
    }

    /** Recreates a task from a line in the save file. */
    public static Task deserialize(String line) {
        String[] fields = line.split("\\t", -1);
        if (fields.length != 4) {
            throw new IllegalArgumentException("Invalid saved task.");
        }
        Task task = new Task(TaskType.valueOf(fields[0]), fields[2], fields[3]);
        if (Boolean.parseBoolean(fields[1])) {
            task.mark();
        }
        return task;
    }

    /** Returns the task in the format shown by the list command. */
    @Override
    public String toString() {
        String status = completed ? "[X]" : "[ ]";
        String taskDetails = details.isEmpty() ? "" : " " + details;
        return "[" + type.getSymbol() + "] " + status + " " + description + taskDetails;
    }
}
