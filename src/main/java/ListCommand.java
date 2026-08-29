/**
 * Displays the current task list.
 */
public class ListCommand extends Command {
    /** Displays the current task list. */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws NobException {
        ui.showTaskList(tasks);
    }
}
