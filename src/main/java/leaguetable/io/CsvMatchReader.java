package leaguetable.io;

import leaguetable.model.Match;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Reads match results from a CSV source.
 *
 * <p>Expected format, one match per line, with a mandatory header row:
 * <pre>Date,HomeTeam,AwayTeam,HomeGoals,AwayGoals
 * 1974-08-17,Birmingham City,Middlesbrough,0,3</pre>
 *
 * <p>Dates use ISO-8601 ({@code yyyy-MM-dd}). Blank lines are ignored. Team
 * names may be wrapped in double quotes if they need to contain a comma
 * (with {@code ""} as an escaped quote); this mirrors the minimal subset of
 * RFC 4180 actually needed for football team names.
 */
public final class CsvMatchReader {

    /** The single source of truth for the expected column layout; everything else below is derived from it. */
    private static final String EXPECTED_HEADER_DISPLAY = "Date,HomeTeam,AwayTeam,HomeGoals,AwayGoals";

    private static final String EXPECTED_HEADER =
            EXPECTED_HEADER_DISPLAY.toLowerCase(Locale.ROOT).replace(" ", "");

    private static final int EXPECTED_FIELD_COUNT = EXPECTED_HEADER_DISPLAY.split(",").length;

    /**
     * Reads match results from the given Reader, returning a list of Match objects.
     *
     * @throws CsvParseException    if the header is missing/wrong, or a line can't be parsed
     * @throws UncheckedIOException if an I/O error occurs while reading
     */
    public List<Match> read(Reader reader) {
        List<Match> matches = new ArrayList<>();
        try (BufferedReader bufferedReader = new BufferedReader(reader)) {
            String line;
            int lineNumber = 0;
            boolean headerSeen = false;

            while ((line = bufferedReader.readLine()) != null) {
                lineNumber++;
                if (line.isBlank()) {
                    continue;
                }

                if (!headerSeen) {
                    validateHeader(line, lineNumber);
                    headerSeen = true;
                    continue;
                }

                matches.add(parseMatchLine(line, lineNumber));
            }

            if (!headerSeen) {
                throw new CsvParseException(0, "", "Input is empty; expected a header row: "
                        + EXPECTED_HEADER_DISPLAY);
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to read match results", e);
        }
        return matches;
    }

    /** Validates that the header line matches the expected format. */
    private void validateHeader(String line, int lineNumber) {
        String normalised = line.strip().toLowerCase(Locale.ROOT).replace(" ", "");
        if (!normalised.equals(EXPECTED_HEADER)) {
            throw new CsvParseException(lineNumber, line,
                    "Expected header '" + EXPECTED_HEADER_DISPLAY + "'");
        }
    }

    /** Parses a single line of match data into a {@link Match}. */
    private Match parseMatchLine(String line, int lineNumber) {
        List<String> fields = CsvLine.split(line);
        if (fields.size() != EXPECTED_FIELD_COUNT) {
            throw new CsvParseException(lineNumber, line,
                    "Expected " + EXPECTED_FIELD_COUNT + " fields (" + EXPECTED_HEADER_DISPLAY
                            + ") but found " + fields.size());
        }

        LocalDate date;
        try {
            date = LocalDate.parse(fields.get(0).strip());
        } catch (DateTimeParseException e) {
            throw new CsvParseException(lineNumber, line,
                    "Invalid date '" + fields.get(0) + "', expected yyyy-MM-dd", e);
        }

        String homeTeam = fields.get(1).strip();
        String awayTeam = fields.get(2).strip();

        int homeGoals = parseGoals(fields.get(3), lineNumber, line, "home");
        int awayGoals = parseGoals(fields.get(4), lineNumber, line, "away");

        try {
            return new Match(date, homeTeam, awayTeam, homeGoals, awayGoals);
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new CsvParseException(lineNumber, line, e.getMessage(), e);
        }
    }

    /** Parses a goals field as a whole number; {@code label} names it in the error message. */
    private int parseGoals(String raw, int lineNumber, String line, String label) {
        try {
            return Integer.parseInt(raw.strip());
        } catch (NumberFormatException e) {
            throw new CsvParseException(lineNumber, line,
                    "Invalid " + label + " goals value '" + raw + "', expected a whole number", e);
        }
    }
}