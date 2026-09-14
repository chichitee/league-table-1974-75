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
        Objects.requireNonNull(value, fieldName + " must not be null");
        String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        return trimmed;
    }

    public LocalDate date() {
        return date;
    }

    public String homeTeam() {
        return homeTeam;
    }

    public String awayTeam() {
        return awayTeam;
    }

    public int homeGoals() {
        return homeGoals;
    }

    public int awayGoals() {
        return awayGoals;
    }

    public boolean isHomeWin() {
        return homeGoals > awayGoals;
    }

    public boolean isAwayWin() {
        return awayGoals > homeGoals;
    }

    public boolean isDraw() {
        return homeGoals == awayGoals;
    }

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

    @Override
    public int hashCode() {
        return Objects.hash(date, homeTeam, awayTeam, homeGoals, awayGoals);
    }

    @Override
    public String toString() {
        return String.format("%s: %s %d-%d %s", date, homeTeam, homeGoals, awayGoals, awayTeam);
    }
}
