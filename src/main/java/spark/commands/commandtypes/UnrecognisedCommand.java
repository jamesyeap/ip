package spark.commands.commandtypes;

import spark.Ui;
import spark.exceptions.formatexceptions.UnrecognisedCommandException;
import spark.storage.Storage;
import spark.tasks.TaskList;

public class UnrecognisedCommand extends Command {
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.printException(new UnrecognisedCommandException());
    }

    @Override
    public boolean isExit() {
        return false;
    }
}
