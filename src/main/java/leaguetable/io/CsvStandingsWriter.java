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

    /** The CSV header line, with the column names in the conventional order. */
    private static final String HEADER =
            "Position,Team,Played,Won,Drawn,Lost,GoalsFor,GoalsAgainst,GoalAverage,Points";

    /**
     * Writes the league table to the given Writer in CSV format, including a header line.
     *
     * @throws UncheckedIOException if an I/O error occurs while writing
     */
    public void write(List<TeamStanding> table, Writer writer) {
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

    /** Formats a single row of the league table as a CSV line, escaping the team name if necessary. */
    private String formatRow(int position, TeamStanding s) {
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

    /** Formats the goal average to three decimal places, or "Inf" if it's infinite. */
    private String formatGoalAverage(double goalAverage) {
        if (Double.isInfinite(goalAverage)) {
            return "Inf";
        }
        return String.format(Locale.ROOT, "%.3f", goalAverage);
    }

    /** Quotes {@code value} for CSV if it contains a comma, quote, or newline; doubles any embedded quotes. */
    private String csvEscape(String value) {
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}