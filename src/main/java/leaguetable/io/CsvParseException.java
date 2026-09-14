package leaguetable.io;

/** Thrown when the input CSV cannot be parsed into valid match results. */
public class CsvParseException extends RuntimeException {

    /** @param lineNumber 1-based line number (0 if the whole file is the problem, e.g. empty) */
    public CsvParseException(int lineNumber, String line, String reason) {
        super(String.format("Line %d: %s%n  >> %s", lineNumber, reason, line));
    }

    /** Same as above, wrapping an underlying cause. */
    public CsvParseException(int lineNumber, String line, String reason, Throwable cause) {
        super(String.format("Line %d: %s%n  >> %s", lineNumber, reason, line), cause);
    }
}