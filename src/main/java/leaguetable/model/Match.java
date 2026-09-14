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

    /** Trims {@code value}, rejecting it if null or blank once trimmed. */
    private static String requireNonBlank(String value, String fieldName) {
        Objects.requireNonNull(value, fieldName + " must not be null");
        String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        return trimmed;
    }

    /** The date the match was played. */
    public LocalDate date() {
        return date;
    }

    /** The home team's name, trimmed. */
    public String homeTeam() {
        return homeTeam;
    }

    /** The away team's name, trimmed. */
    public String awayTeam() {
        return awayTeam;
    }

    /** Goals scored by the home team. */
    public int homeGoals() {
        return homeGoals;
    }

    /** Goals scored by the away team. */
    public int awayGoals() {
        return awayGoals;
    }

    /** Whether the home team scored more than the away team. */
    public boolean isHomeWin() {
        return homeGoals > awayGoals;
    }

    /** Whether the away team scored more than the home team. */
    public boolean isAwayWin() {
        return awayGoals > homeGoals;
    }

    /** Whether both teams scored the same number of goals. */
    public boolean isDraw() {
        return homeGoals == awayGoals;
    }

    /** Equal when date, both team names, and the scoreline all match. */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Match)) return false;
        Match match = (Match) o;
        return homeGoals == match.homeGoals
                && awayGoals == match.awayGoals
                && date.equals(match.date)
                && homeTeam.equals(match.homeTeam)
                && awayTeam.equals(match.awayTeam);
    }

    /** Consistent with {@link #equals(Object)}. */
    @Override
    public int hashCode() {
        return Objects.hash(date, homeTeam, awayTeam, homeGoals, awayGoals);
    }

    /** e.g. {@code "1974-08-17: Stoke City 3-0 Leeds United"}. */
    @Override
    public String toString() {
        return String.format("%s: %s %d-%d %s", date, homeTeam, homeGoals, awayGoals, awayTeam);
    }
}