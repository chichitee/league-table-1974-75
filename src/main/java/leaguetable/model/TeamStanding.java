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

    /**
     * Returns a new standing with one more match folded in.
     *
     * @param goalsFor     goals this team scored in the match
     * @param goalsAgainst goals this team conceded in the match
     * @param pointsEarned points earned for the result (from the scoring rules)
     */
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

    public String team() {
        return team;
    }

    public int played() {
        return played;
    }

    public int won() {
        return won;
    }

    public int drawn() {
        return drawn;
    }

    public int lost() {
        return lost;
    }

    public int goalsFor() {
        return goalsFor;
    }

    public int goalsAgainst() {
        return goalsAgainst;
    }

    public int points() {
        return points;
    }

    /**
     * Goal average (goals scored / goals conceded), the tie-break statistic
     * used by the English Football League before goal difference was
     * introduced in 1976/77.
     *
     * <p>A team that has conceded no goals but scored at least one has an
     * undefined (infinite) ratio; by convention this ranks above every finite
     * average. A team with 0 for and 0 against (no goals at all) is treated
     * as a neutral 0.0 average.
     */
    public double goalAverage() {
        if (goalsAgainst == 0) {
            return goalsFor == 0 ? 0.0 : Double.POSITIVE_INFINITY;
        }
        return (double) goalsFor / goalsAgainst;
    }

    @Override
    public String toString() {
        return String.format("%s [P%d W%d D%d L%d GF%d GA%d Pts%d]",
                team, played, won, drawn, lost, goalsFor, goalsAgainst, points);
    }
}
