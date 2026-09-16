package aravii;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;

/** Tests task state, formatting, matching, and persistence behaviour. */
class TaskTest {
    @Test
    void newTask_isIncomplete() {
        Task task = new Task(TaskType.TODO, "read notes", "");

        assertEquals("[T] [ ] read notes", task.toString());
    }

    @Test
    void markAndUnmark_updatesCompletionStatus() {
        Task task = new Task(TaskType.TODO, "read notes", "");

        task.mark();
        assertTrue(task.toString().contains("[X]"));

        task.unmark();
        assertFalse(task.toString().contains("[X]"));
    }

    @Test
    void matches_findsKeywordsWithoutCaseSensitivity() {
        Task task = new Task(TaskType.DEADLINE, "Submit report", "(by: 2026-09-05)");

        assertTrue(task.matches("submit"));
        assertTrue(task.matches("2026-09-05"));
        assertFalse(task.matches("meeting"));
    }

    @Test
    void serializeAndDeserialize_preservesTaskState() {
        Task original = new Task(TaskType.EVENT, "team meeting", "(from: 2026-09-01 14:00 to: 15:00)");
        original.mark();

        Task restored = Task.deserialize(original.serialize());

        assertEquals(original.toString(), restored.toString());
    }

    @Test
    void add_acceptsMultipleTasks() {
        TaskList taskList = new TaskList();

        taskList.add(new Task(TaskType.TODO, "first task", ""),
                new Task(TaskType.TODO, "second task", ""));

        assertEquals("[T] [ ] first task", taskList.get("1").toString());
        assertEquals("[T] [ ] second task", taskList.get("2").toString());
    }

    @Test
    void formattingStreams_preserveOrderAndTaskNumbers() {
        TaskList taskList = new TaskList();
        taskList.add(new Task(TaskType.TODO, "first", ""),
                new Task(TaskType.TODO, "second", ""));

        assertEquals("1. [T] [ ] first\n2. [T] [ ] second\n", taskList.formatAll());
        assertEquals("2. [T] [ ] second\n", taskList.formatMatching("second"));
    }

    @Test
    void sortByDescription_ordersTasksAlphabeticallyIgnoringCase() {
        TaskList taskList = new TaskList();
        taskList.add(new Task(TaskType.TODO, "zebra", ""),
                new Task(TaskType.TODO, "Alpha", ""),
                new Task(TaskType.TODO, "beta", ""));

        taskList.sortByDescription();

        assertEquals("1. [T] [ ] Alpha\n2. [T] [ ] beta\n3. [T] [ ] zebra\n",
                taskList.formatAll());
    }

    @Test
    void sortCommand_sortsTasksAndAppearsInHelp() {
        TaskList taskList = new TaskList();
        taskList.add(new Task(TaskType.TODO, "zebra", ""),
                new Task(TaskType.TODO, "alpha", ""));
        CommandHandler commandHandler = new CommandHandler();

        assertTrue(commandHandler.helpMessage().contains("sort"));
        assertEquals("1. [T] [ ] alpha\n2. [T] [ ] zebra",
                commandHandler.execute(taskList, "sort"));
    }

    @Test
    void load_doesNotKeepPartiallyLoadedTasks() throws IOException {
        Path dataFile = Files.createTempFile("aravii", ".txt");
        Files.writeString(dataFile, "TODO\tfalse\tvalid task\t\ninvalid saved task\n");

        TaskList taskList = TaskList.load(dataFile);

        assertEquals("", taskList.formatAll());
        Files.deleteIfExists(dataFile);
    }

    @Test
    void constructor_assertsRequiredFields() {
        assertThrows(AssertionError.class, () -> new Task(null, "description", ""));
        assertThrows(AssertionError.class, () -> new Task(TaskType.TODO, null, ""));
        assertThrows(AssertionError.class, () -> new Task(TaskType.TODO, "description", null));
    }
}
