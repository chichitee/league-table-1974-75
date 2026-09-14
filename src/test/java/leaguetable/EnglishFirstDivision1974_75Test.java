package leaguetable;

import leaguetable.io.CsvMatchReader;
import leaguetable.model.Match;
import leaguetable.model.TeamStanding;
import leaguetable.service.StandingsCalculator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The actual deliverable required by the assignment: the English Football
 * League First Division table after 10 rounds of the 1974/75 season,
 * computed from real, researched match results (see {@code /data} and the
 * README for sourcing and verification notes).
 *
 * <p>Six clubs (Arsenal, Leeds United, Leicester City, Middlesbrough,
 * Newcastle United and Tottenham Hotspur) had their round-9 fixture
 * postponed and so have played only 9 matches at this point — a real,
 * verified feature of the historical data, not a bug. The table below was
 * independently computed (in Python, from the same sourced results) before
 * this Java implementation was written, and is asserted against exactly.
 */
class EnglishFirstDivision1974_75Test {

    private static List<Match> matches;

    @BeforeAll
    static void loadRealSeasonData() throws Exception {
        try (InputStream in = EnglishFirstDivision1974_75Test.class
                .getResourceAsStream("/1974-75-first-division-week10.csv")) {
            assertTrue(in != null, "Test resource with real match results not found");
            matches = new CsvMatchReader().read(new InputStreamReader(in, StandardCharsets.UTF_8));
        }
    }

    @Test
    void allOneHundredAndSevenMatchesAreParsed() {
        assertEquals(107, matches.size());
    }

    @Test
    void tableHasAllTwentyTwoClubs() {
        List<TeamStanding> table = StandingsCalculator.forEnglishFirstDivision1974_75().calculate(matches);
        assertEquals(22, table.size());
    }

    @Test
    void sixClubsHaveAGameInHand() {
        List<TeamStanding> table = StandingsCalculator.forEnglishFirstDivision1974_75().calculate(matches);
        long clubsWithNineGamesPlayed = table.stream().filter(s -> s.played() == 9).count();
        long clubsWithTenGamesPlayed = table.stream().filter(s -> s.played() == 10).count();

        assertEquals(6, clubsWithNineGamesPlayed);
        assertEquals(16, clubsWithTenGamesPlayed);
    }

    @Test
    void producesTheExpectedWeekTenTable() {
        List<TeamStanding> table = StandingsCalculator.forEnglishFirstDivision1974_75().calculate(matches);

        String[] expected = {
                // team,                     P, W, D, L, GF, GA, Pts
                "Ipswich Town,10,8,0,2,18,6,16",
                "Manchester City,10,6,2,2,14,11,14",
                "Liverpool,10,6,1,3,17,8,13",
                "Everton,10,4,5,1,14,11,13",
                "Sheffield United,10,5,3,2,14,14,13",
                "Newcastle United,9,5,2,2,16,13,12",
                "Middlesbrough,9,4,3,2,12,7,11",
                "Derby County,10,3,5,2,16,13,11",
                "Stoke City,10,4,3,3,13,11,11",
                "Wolverhampton Wanderers,10,3,5,2,12,11,11",
                "Carlisle United,10,4,2,4,8,8,10",
                "West Ham United,10,4,1,5,20,18,9",
                "Burnley,10,4,1,5,17,18,9",
                "Birmingham City,10,3,2,5,12,17,8",
                "Coventry City,10,2,4,4,11,17,8",
                "Leicester City,9,2,3,4,13,17,7",
                "Luton Town,10,1,5,4,11,16,7",
                "Chelsea,10,2,3,5,10,18,7",
                "Leeds United,9,2,2,5,12,14,6",
                "Arsenal,9,2,2,5,9,12,6",
                "Tottenham Hotspur,9,3,0,6,11,15,6",
                "Queens Park Rangers,10,1,4,5,8,13,6",
        };

        assertEquals(expected.length, table.size());

        for (int i = 0; i < expected.length; i++) {
            String[] parts = expected[i].split(",");
            TeamStanding actual = table.get(i);
            String context = "Position " + (i + 1) + " (" + parts[0] + ")";

            assertEquals(parts[0], actual.team(), context + ": team name");
            assertEquals(Integer.parseInt(parts[1]), actual.played(), context + ": played");
            assertEquals(Integer.parseInt(parts[2]), actual.won(), context + ": won");
            assertEquals(Integer.parseInt(parts[3]), actual.drawn(), context + ": drawn");
            assertEquals(Integer.parseInt(parts[4]), actual.lost(), context + ": lost");
            assertEquals(Integer.parseInt(parts[5]), actual.goalsFor(), context + ": goals for");
            assertEquals(Integer.parseInt(parts[6]), actual.goalsAgainst(), context + ": goals against");
            assertEquals(Integer.parseInt(parts[7]), actual.points(), context + ": points");
        }
    }

    @Test
    void ipswichAreCorrectlyToppingTheTableOnGoalAverage() {
        // A well known, checkable fact about this exact table: Ipswich were
        // top after 10 games in 1974/75.
        List<TeamStanding> table = StandingsCalculator.forEnglishFirstDivision1974_75().calculate(matches);
        assertEquals("Ipswich Town", table.get(0).team());
        assertEquals(3.0, table.get(0).goalAverage(), 0.0001);
    }
}
