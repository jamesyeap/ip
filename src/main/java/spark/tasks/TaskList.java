package spark.tasks;

import spark.exceptions.fileexceptions.FileException;
import spark.exceptions.fileexceptions.TaskDecodingException;
import spark.exceptions.formatexceptions.EmptyDateException;
import spark.exceptions.formatexceptions.EmptyTitleException;
import spark.exceptions.taskmodificationexceptions.InvalidTaskIdException;
import spark.exceptions.taskmodificationexceptions.TaskAlreadyMarked;
import spark.exceptions.taskmodificationexceptions.TaskAlreadyUnMarked;
import spark.exceptions.taskmodificationexceptions.TaskNotFoundException;
import spark.storage.Storage;
import spark.tasks.tasktypes.Deadline;
import spark.tasks.tasktypes.Event;
import spark.tasks.tasktypes.Task;
import spark.tasks.tasktypes.ToDo;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

/**
 * Encapsulates a list of Tasks.
 *
 * @throws FileException if the save-file on the user's hard-disk cannot be accessed or modified.
 * @throws TaskDecodingException if the encoded data on the save-file cannot be read.
 */
public class TaskList {
    protected List<Task> tasks;
    private final Storage taskFile;

    public TaskList() throws FileException, TaskDecodingException {
        // read the save-file from the user's hard-disk
        this.taskFile = new Storage();

        // read the Tasks from the save-file
        this.tasks = taskFile.readTasksFile();
    }

    /**
     * Adds a ToDo.
     *
     * @param tokens
     * @throws EmptyTitleException
     * @throws FileException
     */
    public void addToDo(String[] tokens) throws EmptyTitleException, FileException {
        List<String> words = new ArrayList<>(Arrays.asList(tokens).subList(1, tokens.length));
        String title = String.join(" ", words);
        if (title.equals("")) { // To-Do must have a title
            throw new EmptyTitleException();
        }

        ToDo toDo = new ToDo(title);
        tasks.add(toDo);

        // save changes to save-file in user's hard disk
        taskFile.writeTasksFile(this.tasks);

        System.out.println("Got it, I've added this todo:");
        System.out.format("   %s\n", toDo);

        showNumberOfTasks();
    }

    /**
     * Adds a ToDo with a given deadline.
     *
     * @param tokens
     * @throws EmptyTitleException
     * @throws EmptyDateException
     * @throws FileException
     */
    public void addDeadline(String[] tokens) throws EmptyTitleException, EmptyDateException, FileException {
        List<String> firstHalf = new ArrayList<>();
        List<String> secondHalf = new ArrayList<>();

        boolean inSecondHalf = false;
        for (int i=1; i<tokens.length; i++) {
            if (tokens[i].equals("/by")) {
                inSecondHalf = true;
            } else if (!inSecondHalf) {
                firstHalf.add(tokens[i]);
            } else {
                secondHalf.add(tokens[i]);
            }
        }

        String title = String.join(" ", firstHalf);
        String by = String.join(" ", secondHalf);

        // Deadline must have a title
        if (title.equals("")) {
            throw new EmptyTitleException();
        }

        // Deadline must have a date
        if (by.equals("")) {
            throw new EmptyDateException();
        }

        Deadline deadline = new Deadline(title, by);
        tasks.add(deadline);

        // save changes to save-file in user's hard disk
        taskFile.writeTasksFile(this.tasks);

        System.out.println("Got it, I've added this deadline:");
        System.out.format("   %s\n", deadline);

        showNumberOfTasks();
    }

    /**
     * Adds an Event with a specified date and time at which it occurs.
     *
     * @param tokens
     * @throws EmptyTitleException
     * @throws EmptyDateException
     * @throws FileException
     */
    public void addEvent(String[] tokens) throws EmptyTitleException, EmptyDateException, FileException {
        List<String> firstHalf = new ArrayList<>();
        List<String> secondHalf = new ArrayList<>();

        boolean inSecondHalf = false;
        for (int i=1; i<tokens.length; i++) {
            if (tokens[i].equals("/at")) {
                inSecondHalf = true;
            } else if (!inSecondHalf) {
                firstHalf.add(tokens[i]);
            } else {
                secondHalf.add(tokens[i]);
            }
        }

        String title = String.join(" ", firstHalf);
        String at = String.join(" ", secondHalf);

        // Event must have a title
        if (title.equals("")) {
            throw new EmptyTitleException();
        }

        // Event must have a date
        if (at.equals("")) {
            throw new EmptyDateException();
        }

        Event event = new Event(title, at);
        tasks.add(event);

        // save changes to save-file in user's hard disk
        taskFile.writeTasksFile(this.tasks);

        System.out.println("Got it, I've added this event:");
        System.out.format("   %s\n", event);

        showNumberOfTasks();
    }

    /**
     * Marks an incomplete Task as complete.
     *
     * @param tokens
     * @throws TaskNotFoundException
     * @throws TaskAlreadyMarked
     * @throws InvalidTaskIdException
     * @throws FileException
     */
    public void markTask(String[] tokens) throws TaskNotFoundException, TaskAlreadyMarked, InvalidTaskIdException, FileException {
        int taskId = getTaskId(tokens[1]);

        int index = taskId - 1;
        Task t = getTaskByIndex(index);
        t.mark();

        // save changes to save-file in user's hard disk
        taskFile.writeTasksFile(this.tasks);

        System.out.println("Awesome! I've marked this task as done:");
        System.out.format("   %s\n", t);
    }

    /**
     * Marks a completed Task as incomplete.
     *
     * @param tokens
     * @throws TaskNotFoundException
     * @throws TaskAlreadyUnMarked
     * @throws InvalidTaskIdException
     * @throws FileException
     */
    public void unMarkTask(String[] tokens) throws TaskNotFoundException, TaskAlreadyUnMarked, InvalidTaskIdException, FileException {
        int taskId = getTaskId(tokens[1]);

        int index = taskId - 1;
        Task t = getTaskByIndex(index);
        t.unMark();

        // save changes to save-file in user's hard disk
        taskFile.writeTasksFile(this.tasks);

        System.out.println("Okay, I've marked this task as not done yet:");
        System.out.format("   %s\n", t);
    }

    /**
     * Permanently removes a Task from the user's list by a specified index.
     *
     * @param tokens
     * @throws TaskNotFoundException
     * @throws InvalidTaskIdException
     * @throws FileException
     */
    public void deleteTask(String[] tokens) throws TaskNotFoundException, InvalidTaskIdException, FileException {
        int taskId = getTaskId(tokens[1]);

        int index = taskId - 1;
        Task t = getTaskByIndex(index);

        System.out.println("Noted. I've removed this task:");
        System.out.format("   %s\n", t);

        tasks.remove(t);

        // save changes to save-file in user's hard disk
        taskFile.writeTasksFile(this.tasks);

        showNumberOfTasks();
    }

    /**
     * Displays all Tasks in the user's list.
     */
    public void showTaskList() {
        // if there are no tasks, inform the user
        if (tasks.size() == 0) {
            System.out.println("No tasks found! (trust me, I've looked everywhere)");
        } else {
            for (int i = 0; i < tasks.size(); i++) {
                System.out.format("    %d. %s\n", i + 1, tasks.get(i));
            }
        }
    }

    private void showNumberOfTasks() {
        System.out.format("Now you have %d tasks in the list.\n", this.tasks.size());
    }

    private Task getTaskByIndex(int index) throws TaskNotFoundException {
        try {
            return tasks.get(index);
        } catch (IndexOutOfBoundsException e) {
            throw new TaskNotFoundException();
        }
    }

    private int getTaskId(String input) throws InvalidTaskIdException {
        int taskId;

        try {
            taskId = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            throw new InvalidTaskIdException(input);
        }

        return taskId;
    }
}
