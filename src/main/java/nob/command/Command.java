package nob.command;

import nob.exception.NobException;
import nob.storage.Storage;
import nob.task.TaskList;
import nob.ui.Ui;

/**
 * Represents one action that Nob can perform for a user command.
 */
public abstract class Command {
    /**
     * Performs this command using the application's collaborators.
     *
     * @param tasks the user's task list
     * @param ui the console user interface
     * @param storage persistent task storage
     * @throws NobException if the command cannot be completed
     */
    public abstract void execute(TaskList tasks, Ui ui, Storage storage) throws NobException;

    /**
     * Returns whether this command ends the application.
     *
     * @return {@code true} when the command exits Nob
     */
    public boolean isExit() {
        return false;
    }
}
