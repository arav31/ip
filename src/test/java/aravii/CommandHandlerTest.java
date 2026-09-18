package aravii;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Covers command execution after parsing has validated the command syntax.
 */
class CommandHandlerTest {
    @Test
    void execute_queryCommands_returnUsefulResponses() {
        TaskList tasks = new TaskList();
        CommandHandler handler = new CommandHandler();

        assertTrue(handler.execute(tasks, Parser.parse("help")).contains("deadline"));
        assertEquals("There are no tasks.", handler.execute(tasks, Parser.parse("list")));
        assertEquals("No matching tasks found.", handler.execute(tasks, Parser.parse("find missing")));
        assertEquals("Bye. Hope to see you again soon!", handler.execute(tasks, Parser.parse("bye")));
    }

    @Test
    void execute_mutatingCommands_updateAndFormatTasks() {
        TaskList tasks = new TaskList();
        CommandHandler handler = new CommandHandler();

        assertTrue(handler.execute(tasks, Parser.parse("todo write tests")).contains("write tests"));
        assertTrue(handler.execute(tasks, Parser.parse("mark 1")).contains("[X]"));
        assertTrue(handler.execute(tasks, Parser.parse("unmark 1")).contains("[ ]"));
        assertTrue(handler.execute(tasks, Parser.parse("delete 1")).contains("write tests"));
        assertEquals("There are no tasks.", handler.execute(tasks, Parser.parse("list")));
    }
}
