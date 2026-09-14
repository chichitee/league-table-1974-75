package leaguetable.model;

import java.util.Objects;

/**
 * The accumulated record of a single team at a point in the season:
 * played/won/drawn/lost, goals for/against and points.
 *
 * <p>Immutable. Built up via {@link #withResult(int, int, int)} rather than
 * mutated in place, so a stream of {@code Match} objects can be reduced onto
 * a starting {@link #unplayed(String)} standing.
 */
public final class TeamStanding {

    private final String team;
    private final int played;
    private final int won;
    private final int drawn;
    private final int lost;
    private final int goalsFor;
    private final int goalsAgainst;
    private final int points;

    /** Package-private full-state constructor; use {@link #unplayed(String)} to start a standing. */
    private TeamStanding(String team, int played, int won, int drawn, int lost,
                          int goalsFor, int goalsAgainst, int points) {
        this.team = team;
        this.played = played;
        this.won = won;
        this.drawn = drawn;
        this.lost = lost;
        this.goalsFor = goalsFor;
        this.goalsAgainst = goalsAgainst;
        this.points = points;
    }

    /** A brand-new standing for a team that has not yet played. */
    public static TeamStanding unplayed(String team) {
        return new TeamStanding(Objects.requireNonNull(team), 0, 0, 0, 0, 0, 0, 0);
    }

    /** Returns a new standing with one more match's goals/points folded in. */
    public TeamStanding withResult(int goalsFor, int goalsAgainst, int pointsEarned) {
        int newWon = won;
        int newDrawn = drawn;
        int newLost = lost;
        if (goalsFor > goalsAgainst) {
            newWon++;
        } else if (goalsFor < goalsAgainst) {
            newLost++;
        } else {
            newDrawn++;
        }
        return new TeamStanding(
                team,
                played + 1,
                newWon,
                newDrawn,
                newLost,
                this.goalsFor + goalsFor,
                this.goalsAgainst + goalsAgainst,
                points + pointsEarned);
    }

    /** The team's name, exactly as first seen in the input data. */
    public String team() {
        return team;
    }

    /** Number of matches played so far. */
    public int played() {
        return played;
    }

    /** Number of matches won so far. */
    public int won() {
        return won;
    }

    /** Number of matches drawn so far. */
    public int drawn() {
        return drawn;
    }

    /** Number of matches lost so far. */
    public int lost() {
        return lost;
    }

    /** Total goals scored so far. */
    public int goalsFor() {
        return goalsFor;
    }

    /** Total goals conceded so far. */
    public int goalsAgainst() {
        return goalsAgainst;
    }

    /** Total points earned so far, under whichever {@code ScoringRules} built this standing. */
    public int points() {
        return points;
    }

    /**
     * Goal average (goals scored / conceded) — the pre-1976/77 tie-break
     * statistic. Goals with none conceded is treated as infinite; 0-0 is 0.0.
     */
    public double goalAverage() {
        if (goalsAgainst == 0) {
            return goalsFor == 0 ? 0.0 : Double.POSITIVE_INFINITY;
        }
        return (double) goalsFor / goalsAgainst;
    }

    /** Equal when team, played/won/drawn/lost, goals, and points all match. */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TeamStanding)) return false;
        TeamStanding that = (TeamStanding) o;
        return played == that.played
                && won == that.won
                && drawn == that.drawn
                && lost == that.lost
                && goalsFor == that.goalsFor
                && goalsAgainst == that.goalsAgainst
                && points == that.points
                && team.equals(that.team);
    }


    @Override
    public int hashCode() {
        return Objects.hash(team, played, won, drawn, lost, goalsFor, goalsAgainst, points);
    }

    /** A short human-readable form, e.g. {@code "Leeds United [P10 W9 D1 L0 GF26 GA6 Pts19]"}. */
    @Override
    public String toString() {
        return String.format("%s [P%d W%d D%d L%d GF%d GA%d Pts%d]",
                team, played, won, drawn, lost, goalsFor, goalsAgainst, points);
    }
}