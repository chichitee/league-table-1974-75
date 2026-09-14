package leaguetable.io;

/** Thrown when the input CSV cannot be parsed into valid match results. */
public class CsvParseException extends RuntimeException {

    public CsvParseException(int lineNumber, String line, String reason) {
        // Constructs a new CsvParseException with a formatted message indicating the line number, the reason for the failure, and the content of the line that caused the error.
        super(String.format("Line %d: %s%n  >> %s", lineNumber, reason, line));
    }

    public CsvParseException(int lineNumber, String line, String reason, Throwable cause) {
        // Constructs a new CsvParseException with a formatted message and a cause, indicating the line number, the reason for the failure, and the content of the line that caused the error.
        super(String.format("Line %d: %s%n  >> %s", lineNumber, reason, line), cause);
    }
}
