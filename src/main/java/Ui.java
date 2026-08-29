/**
 * Handles messages shown to the user in the console.
 */
public class Ui {
    /** The divider used around each app response block. */
    private static final String DIVIDER = "____________________________________________________________";

    /** Displays Nob's greeting when the application starts. */
    public void showWelcome() {
        String banner = """
                 _   _       _
                | \\ | | ___ | |__
                |  \\| |/ _ \\| '_ \\
                | |\\  | (_) | |_) |
                |_| \\_|\\___/|_.__/

                  (•_•)
                  ( •_•)>⌐■-■
                  (⌐■_■)

                """;
        String greeting = """
                WASSUP! I'm Nob :)
                How can I help you?
                """;

        showDivider();
        System.out.print(banner);
        System.out.print(greeting);
        showDivider();
    }

    /** Displays the divider between console response blocks. */
    public void showDivider() {
        System.out.println(DIVIDER);
    }

    /** Displays Nob's goodbye message. */
    public void showGoodbye() {
        System.out.println("Goodbye! Hope to see you soon mate!");
    }

    /** Displays the help text listing all available commands. */
    public void showHelp() {
        System.out.println("Here are the commands you can use:");
        System.out.println("  list");
        System.out.println("  todo DESCRIPTION");
        System.out.println("  deadline DESCRIPTION /by DATE_OR_TIME");
        System.out.println("  event DESCRIPTION /from START /to END");
        System.out.println("  mark TASK_NUMBER");
        System.out.println("  unmark TASK_NUMBER");
        System.out.println("  delete TASK_NUMBER");
        System.out.println("  clear");
        System.out.println("  bye");
    }

    /** Displays a hint that a command is missing the space before its description. */
    public void showMissingSpaceHint(String commandName, String command) {
        String description = command.substring(commandName.length()).trim();
        System.out.println("It looks like you are missing a space after '" + commandName + "'.");
        System.out.println("Did you mean: " + commandName + " " + description);
    }

    /** Displays suggestions after an unrecognised command. */
    public void showUnknownCommandHint() {
        System.out.println("That isn't any of the commands I know. Did you mean one of these?");
        System.out.println("  todo DESCRIPTION");
        System.out.println("  deadline DESCRIPTION /by DATE_OR_TIME");
        System.out.println("  event DESCRIPTION /from START /to END");
    }
}
