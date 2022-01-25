package spark.exceptions.fileexceptions;

import spark.exceptions.SparkException;

public abstract class FileException extends SparkException {
    public FileException(String s) {
        super(String.format("Encountered an issue while handling save-file: %s", s));
    }
}
