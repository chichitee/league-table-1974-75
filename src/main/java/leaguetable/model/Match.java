package leaguetable.model;

import java.time.LocalDate;
import java.util.Objects;

/**
 * A single completed football match between two teams.
 *
 * <p>Immutable value object. Validation is deliberately strict: a
 * "production-ready" reader should fail fast and loudly on bad input rather
 * than silently producing a wrong table.
 */
public final class Match {

    private final LocalDate date;
    private final String homeTeam;
    private final String awayTeam;
    private final int homeGoals;
    private final int awayGoals;


    /**
     * Creates a validated match result.
     *
     * @throws NullPointerException     if date or a team name is null
     * @throws IllegalArgumentException if a team name is blank, the two
     *                                  teams are the same, or a goal count is negative
     */

    public Match(LocalDate date, String homeTeam, String awayTeam, int homeGoals, int awayGoals) {
        this.date = Objects.requireNonNull(date, "date must not be null");
        this.homeTeam = requireNonBlank(homeTeam, "homeTeam");
        this.awayTeam = requireNonBlank(awayTeam, "awayTeam");
        if (homeTeam.trim().equalsIgnoreCase(awayTeam.trim())) {
            throw new IllegalArgumentException(
                    "A team cannot play itself: " + homeTeam);
        }
        if (homeGoals < 0 || awayGoals < 0) {
            throw new IllegalArgumentException(
                    "Goals cannot be negative (home=" + homeGoals + ", away=" + awayGoals + ")");
        }
        this.homeGoals = homeGoals;
        this.awayGoals = awayGoals;
    }


    private static String requireNonBlank(String value, String fieldName) {
        // Trims {@code value}, rejecting it if null or blank once trimmed. //
        Objects.requireNonNull(value, fieldName + " must not be null");
        String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        return trimmed;
    }

    public LocalDate date() {
     //The date the match was played. 
        return date;
    }

    public String homeTeam() {
        // The home team's name, trimmed. 
        return homeTeam;
    }

    public String awayTeam() {
        // The away team's name, trimmed.
        return awayTeam;
    }

    public int homeGoals() {
        // The number of goals scored by the home team.
        return homeGoals;
    }

    public int awayGoals() {
        // The number of goals scored by the away team.
        return awayGoals;
    }

    public boolean isHomeWin() {
        // Returns true if the home team won the match.
        return homeGoals > awayGoals;
    }

    public boolean isAwayWin() {
        // Returns true if the away team won the match.
        return awayGoals > homeGoals;
    }

    public boolean isDraw() {
        // Returns true if the match was a draw.
        return homeGoals == awayGoals;
    }

    @Override
    public boolean equals(Object o) {
        // Checks if this match is equal to another object.
        if (this == o) return true;
        if (!(o instanceof Match)) return false;
        Match match = (Match) o;
        return homeGoals == match.homeGoals
                && awayGoals == match.awayGoals
                && date.equals(match.date)
                && homeTeam.equals(match.homeTeam)
                && awayTeam.equals(match.awayTeam);
    }

    @Override
    public int hashCode() {
        // Returns a hash code for this match.
        return Objects.hash(date, homeTeam, awayTeam, homeGoals, awayGoals);
    }

    @Override
    public String toString() {
        // Returns a string representation of this match in the format "YYYY-MM-DD: HomeTeam X-Y AwayTeam".
        return String.format("%s: %s %d-%d %s", date, homeTeam, homeGoals, awayGoals, awayTeam);
    }
}
