package leaguetable.service;

import leaguetable.model.Match;
import leaguetable.model.TeamStanding;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Turns a list of match results into a ranked league table.
 *
 * <p>Ranking order (as used by the English Football League First Division in
 * 1974/75, per the competition's own classification rules):
 * <ol>
 *   <li>Points</li>
 *   <li>Goal average (goals scored ÷ goals conceded) — goal <em>difference</em>
 *       was not introduced in England until the 1976/77 season, so it is
 *       deliberately not used here</li>
 *   <li>Goals scored</li>
 * </ol>
 * Anything still tied after those three levels is not specified by the
 * historical rules (in practice this essentially never happened over a full
 * season); this implementation documents that as an explicit assumption and
 * falls back to the teams' names in alphabetical order, purely so the output
 * is deterministic and reproducible.
 */
public final class StandingsCalculator {

    /**
     * Tie-break comparator used once points are equal: goal average (desc),
     * then goals scored (desc), then team name (asc) as the documented,
     * deterministic final fallback.
     */
    static final Comparator<TeamStanding> TIE_BREAK_COMPARATOR =
            Comparator.comparingDouble(TeamStanding::goalAverage).reversed()
                    .thenComparing(Comparator.comparingInt(TeamStanding::goalsFor).reversed())
                    .thenComparing(TeamStanding::team);

    static final Comparator<TeamStanding> TABLE_ORDER =
            Comparator.comparingInt(TeamStanding::points).reversed()
                    .thenComparing(TIE_BREAK_COMPARATOR);

    private final ScoringRules rules;

    public StandingsCalculator(ScoringRules rules) {
        this.rules = rules;
    }

    /** Convenience factory using the 1974/75 English First Division scoring rules. */
    public static StandingsCalculator forEnglishFirstDivision1974_75() {
        return new StandingsCalculator(ScoringRules.englishFirstDivision1974_75());
    }

    /**
     * Computes the league table for the given matches.
     *
     * <p>Every team that appears as either the home or away side in at least
     * one match is included, even if — as happened for six clubs in the real
     * 1974/75 data used by this project — a postponed fixture means it has
     * played fewer matches than its rivals at this point in the season.
     *
     * @param matches match results, in any order; an empty list yields an empty table
     * @return the table, ordered from 1st place to last
     */
    public List<TeamStanding> calculate(List<Match> matches) {
        Map<String, TeamStanding> standings = new LinkedHashMap<>();

        for (Match match : matches) {
            TeamStanding home = standings.computeIfAbsent(match.homeTeam(), TeamStanding::unplayed);
            TeamStanding away = standings.computeIfAbsent(match.awayTeam(), TeamStanding::unplayed);

            int homePoints = pointsFor(match.homeGoals(), match.awayGoals());
            int awayPoints = pointsFor(match.awayGoals(), match.homeGoals());

            standings.put(match.homeTeam(),
                    home.withResult(match.homeGoals(), match.awayGoals(), homePoints));
            standings.put(match.awayTeam(),
                    away.withResult(match.awayGoals(), match.homeGoals(), awayPoints));
        }

        List<TeamStanding> table = new ArrayList<>(standings.values());
        table.sort(TABLE_ORDER);
        return table;
    }

    private int pointsFor(int goalsFor, int goalsAgainst) {
        if (goalsFor > goalsAgainst) {
            return rules.pointsForWin();
        }
        if (goalsFor < goalsAgainst) {
            return rules.pointsForLoss();
        }
        return rules.pointsForDraw();
    }
}
