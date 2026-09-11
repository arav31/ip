package aravii;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
}
