package leaguetable.io;

/** Thrown when the input CSV cannot be parsed into valid match results. */
public class CsvParseException extends RuntimeException {

    public CsvParseException(int lineNumber, String line, String reason) {
        super(String.format("Line %d: %s%n  >> %s", lineNumber, reason, line));
    }

    public CsvParseException(int lineNumber, String line, String reason, Throwable cause) {
        super(String.format("Line %d: %s%n  >> %s", lineNumber, reason, line), cause);
    }
}
