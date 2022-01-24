package spark.commands.commandtypes;

import spark.storage.Storage;
import spark.tasks.TaskList;
import spark.Ui;

public class ListCommand extends Command {
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        tasks.showTaskList();
    }

    @Override
    public boolean isExit() {
        return false;
    }
}
