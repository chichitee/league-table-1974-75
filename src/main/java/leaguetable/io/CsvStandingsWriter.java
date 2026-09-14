package leaguetable.io;

import leaguetable.model.TeamStanding;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.util.List;
import java.util.Locale;

/**
 * Writes a ranked league table to CSV in the conventional column order used
 * by printed English football tables: position, team, played, won, drawn,
 * lost, goals for, goals against, goal average, points.
 */
public final class CsvStandingsWriter {
    // The CSV header line, with the column names in the conventional order.

    private static final String HEADER =
            "Position,Team,Played,Won,Drawn,Lost,GoalsFor,GoalsAgainst,GoalAverage,Points";

    public void write(List<TeamStanding> table, Writer writer) {
        // Writes the league table to the given Writer in CSV format, including a header line.
        try {
            writer.write(HEADER);
            writer.write(System.lineSeparator());

            int position = 1;
            for (TeamStanding standing : table) {
                writer.write(formatRow(position, standing));
                writer.write(System.lineSeparator());
                position++;
            }
            writer.flush();
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to write standings", e);
        }
    }

    private String formatRow(int position, TeamStanding s) {
        // Formats a single row of the league table as a CSV line, escaping the team name if necessary.
        return String.format("%d,%s,%d,%d,%d,%d,%d,%d,%s,%d",
                position,
                csvEscape(s.team()),
                s.played(),
                s.won(),
                s.drawn(),
                s.lost(),
                s.goalsFor(),
                s.goalsAgainst(),
                formatGoalAverage(s.goalAverage()),
                s.points());
    }

    private String formatGoalAverage(double goalAverage) {
        // Formats the goal average to three decimal places, or returns "Inf" if the value is infinite (e.g., when goals against is zero).
        if (Double.isInfinite(goalAverage)) {
            return "Inf";
        }
        return String.format(Locale.ROOT, "%.3f", goalAverage);
    }

    private String csvEscape(String value) {
        // Escapes a string for CSV output. If the value contains a comma, double quote, or newline, it is enclosed in double quotes and any double quotes are escaped by doubling them.
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}
