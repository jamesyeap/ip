package spark.commands.commandtypes;

import spark.exceptions.SparkException;
import spark.storage.Storage;
import spark.tasks.TaskList;
import spark.Ui;

public class AddDeadlineCommand extends Command {
    private String[] tokens;

    public AddDeadlineCommand(String[] tokens) {
        this.tokens = tokens;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        try {
            tasks.addDeadline(tokens);
        } catch (SparkException e) {
            ui.printException(e);
        }
    }

    @Override
    public boolean isExit() {
        return false;
    }
}
