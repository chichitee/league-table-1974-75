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

    private static final String EXPECTED_HEADER = "date,hometeam,awayteam,homegoals,awaygoals";

    public List<Match> read(Reader reader) {
        // Reads match results from the given Reader, returning a list of Match objects. Throws CsvParseException on any parsing error.
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
                        + "Date,HomeTeam,AwayTeam,HomeGoals,AwayGoals");
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to read match results", e);
        }
        return matches;
    }

    private void validateHeader(String line, int lineNumber) {
        // Validates that the header line matches the expected format. Throws CsvParseException if it does not.
        String normalised = line.strip().toLowerCase(Locale.ROOT).replace(" ", "");
        if (!normalised.equals(EXPECTED_HEADER)) {
            throw new CsvParseException(lineNumber, line,
                    "Expected header 'Date,HomeTeam,AwayTeam,HomeGoals,AwayGoals'");
        }
    }

    private Match parseMatchLine(String line, int lineNumber) {
        // Parses a single line of match data into a Match object. Throws CsvParseException on any parsing error.
        List<String> fields = CsvLine.split(line);
        if (fields.size() != 5) {
            throw new CsvParseException(lineNumber, line,
                    "Expected 5 fields (Date,HomeTeam,AwayTeam,HomeGoals,AwayGoals) but found "
                            + fields.size());
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

    private int parseGoals(String raw, int lineNumber, String line, String label) {
        // Parses a goals field into an integer. Throws CsvParseException if the value is not a valid integer.
        try {
            return Integer.parseInt(raw.strip());
        } catch (NumberFormatException e) {
            throw new CsvParseException(lineNumber, line,
                    "Invalid " + label + " goals value '" + raw + "', expected a whole number", e);
        }
    }
}
