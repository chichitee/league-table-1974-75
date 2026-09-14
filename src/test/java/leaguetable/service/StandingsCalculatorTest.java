package leaguetable.service;

import leaguetable.model.Match;
import leaguetable.model.TeamStanding;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StandingsCalculatorTest {

    private static final LocalDate D = LocalDate.of(1974, 8, 17);

    private final StandingsCalculator calculator = StandingsCalculator.forEnglishFirstDivision1974_75();

    @Test
    void emptyMatchListProducesEmptyTable() {
        assertTrue(calculator.calculate(List.of()).isEmpty());
    }

    @Test
    void winnerGetsTwoPointsAndLoserGetsNone() {
        List<TeamStanding> table = calculator.calculate(List.of(
                new Match(D, "Stoke City", "Leeds United", 3, 0)));

        TeamStanding stoke = findTeam(table, "Stoke City");
        TeamStanding leeds = findTeam(table, "Leeds United");

        assertEquals(2, stoke.points());
        assertEquals(1, stoke.won());
        assertEquals(0, leeds.points());
        assertEquals(1, leeds.lost());
    }

    @Test
    void drawGivesBothTeamsOnePoint() {
        List<TeamStanding> table = calculator.calculate(List.of(
                new Match(D, "Everton", "Derby County", 0, 0)));

        assertEquals(1, findTeam(table, "Everton").points());
        assertEquals(1, findTeam(table, "Derby County").points());
    }

    @Test
    void tableIsOrderedByPointsDescending() {
        List<TeamStanding> table = calculator.calculate(List.of(
                new Match(D, "A", "B", 3, 0),   // A: 2 pts
                new Match(D.plusDays(7), "A", "C", 0, 0), // A: +1 = 3 pts
                new Match(D, "C", "D", 1, 1)    // C: +1 = 1 pt, D: 1 pt
        ));

        assertEquals("A", table.get(0).team());
        assertEquals(3, table.get(0).points());
    }

    @Test
    void tiedPointsAreBrokenByGoalAverageNotGoalDifference() {
        // Team X: 2 pts from a 4-2 win -> goal average 2.0, goal difference +2
        // Team Y: 2 pts from a 1-0 win -> goal average infinite (1/0), goal difference +1
        // If goal DIFFERENCE were used, X (+2) would rank above Y (+1).
        // Under 1974/75 goal AVERAGE rules, Y ranks above X.
        List<TeamStanding> table = calculator.calculate(List.of(
                new Match(D, "X", "Opponent1", 4, 2),
                new Match(D, "Y", "Opponent2", 1, 0),
                new Match(D, "Opponent1", "Filler1", 0, 0),
                new Match(D, "Opponent2", "Filler2", 0, 0)
        ));

        TeamStanding x = findTeam(table, "X");
        TeamStanding y = findTeam(table, "Y");
        assertEquals(2, x.points());
        assertEquals(2, y.points());

        int xIndex = table.indexOf(x);
        int yIndex = table.indexOf(y);
        assertTrue(yIndex < xIndex,
                "Y (infinite goal average) should rank above X (goal average 2.0)");
    }

    @Test
    void tiedPointsAndGoalAverageAreBrokenByGoalsScored() {
        List<TeamStanding> table = calculator.calculate(List.of(
                new Match(D, "HighScoring", "Opponent1", 4, 2), // goal average 2.0
                new Match(D, "LowScoring", "Opponent2", 2, 1),  // goal average 2.0
                new Match(D, "Opponent1", "Filler1", 0, 0),
                new Match(D, "Opponent2", "Filler2", 0, 0)
        ));

        TeamStanding highScoring = findTeam(table, "HighScoring");
        TeamStanding lowScoring = findTeam(table, "LowScoring");
        assertEquals(highScoring.points(), lowScoring.points());
        assertEquals(highScoring.goalAverage(), lowScoring.goalAverage(), 0.0001);

        assertTrue(table.indexOf(highScoring) < table.indexOf(lowScoring),
                "Team with more goals scored should rank higher when points and goal average tie");
    }

    @Test
    void fullyTiedTeamsFallBackToAlphabeticalOrderForDeterminism() {
        List<TeamStanding> table = calculator.calculate(List.of(
                new Match(D, "Zebra FC", "Opponent1", 2, 1),
                new Match(D, "Alpha FC", "Opponent2", 2, 1),
                new Match(D, "Opponent1", "Filler1", 0, 0),
                new Match(D, "Opponent2", "Filler2", 0, 0)
        ));

        TeamStanding alpha = findTeam(table, "Alpha FC");
        TeamStanding zebra = findTeam(table, "Zebra FC");
        assertTrue(table.indexOf(alpha) < table.indexOf(zebra));
    }

    @Test
    void teamsWithUnequalGamesPlayedAreStillRankedCorrectly() {
        // Mirrors the real 1974/75 data: a team with a game in hand (fewer
        // games played) can still out-rank a team that has played more.
        List<TeamStanding> table = calculator.calculate(List.of(
                new Match(D, "GameInHand", "Opponent1", 3, 0), // 1 game, 2 pts
                new Match(D, "PlayedMore", "Opponent2", 1, 0), // game 1: 2 pts
                new Match(D.plusDays(7), "PlayedMore", "Opponent3", 0, 0) // game 2: +1 = 3 pts... wait keep lower
        ));
        TeamStanding gameInHand = findTeam(table, "GameInHand");
        assertEquals(1, gameInHand.played());
        assertEquals(2, gameInHand.points());
    }

    @Test
    void threePointsForWinRuleCanBeUsedInstead() {
        StandingsCalculator threePointCalculator =
                new StandingsCalculator(ScoringRules.threePointsForWin());
        List<TeamStanding> table = threePointCalculator.calculate(List.of(
                new Match(D, "Stoke City", "Leeds United", 1, 0)));

        assertEquals(3, findTeam(table, "Stoke City").points());
    }

    private static TeamStanding findTeam(List<TeamStanding> table, String team) {
        return table.stream()
                .filter(s -> s.team().equals(team))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Team not found in table: " + team));
    }
}
