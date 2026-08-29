/**
 * Displays Nob's farewell message and ends the application.
 */
public class ExitCommand extends Command {
    /** Displays Nob's farewell message. */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showGoodbye();
    }

    /** Returns that this command ends the application. */
    @Override
    public boolean isExit() {
        return true;
    }
}
