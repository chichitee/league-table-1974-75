package leaguetable.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TeamStandingTest {

    @Test
    void unplayedStandingHasNoStats() {
        TeamStanding standing = TeamStanding.unplayed("Arsenal");
        assertEquals("Arsenal", standing.team());
        assertEquals(0, standing.played());
        assertEquals(0, standing.won());
        assertEquals(0, standing.drawn());
        assertEquals(0, standing.lost());
        assertEquals(0, standing.goalsFor());
        assertEquals(0, standing.goalsAgainst());
        assertEquals(0, standing.points());
        assertEquals(0.0, standing.goalAverage());
    }

    @Test
    void accumulatesAWin() {
        TeamStanding standing = TeamStanding.unplayed("Arsenal").withResult(3, 0, 2);
        assertEquals(1, standing.played());
        assertEquals(1, standing.won());
        assertEquals(0, standing.drawn());
        assertEquals(0, standing.lost());
        assertEquals(3, standing.goalsFor());
        assertEquals(0, standing.goalsAgainst());
        assertEquals(2, standing.points());
    }

    @Test
    void accumulatesADraw() {
        TeamStanding standing = TeamStanding.unplayed("Arsenal").withResult(1, 1, 1);
        assertEquals(1, standing.drawn());
        assertEquals(0, standing.won());
        assertEquals(0, standing.lost());
        assertEquals(1, standing.points());
    }

    @Test
    void accumulatesALoss() {
        TeamStanding standing = TeamStanding.unplayed("Arsenal").withResult(0, 2, 0);
        assertEquals(1, standing.lost());
        assertEquals(0, standing.won());
        assertEquals(0, standing.drawn());
        assertEquals(0, standing.points());
    }

    @Test
    void accumulatesMultipleResults() {
        TeamStanding standing = TeamStanding.unplayed("Ipswich Town")
                .withResult(1, 0, 2)   // win
                .withResult(2, 2, 1)   // draw
                .withResult(0, 1, 0);  // loss

        assertEquals(3, standing.played());
        assertEquals(1, standing.won());
        assertEquals(1, standing.drawn());
        assertEquals(1, standing.lost());
        assertEquals(3, standing.goalsFor());
        assertEquals(3, standing.goalsAgainst());
        assertEquals(3, standing.points());
    }

    @Test
    void goalAverageIsGoalsForOverGoalsAgainst() {
        TeamStanding standing = TeamStanding.unplayed("Ipswich Town").withResult(6, 2, 2);
        assertEquals(3.0, standing.goalAverage(), 0.0001);
    }

    @Test
    void goalAverageIsInfiniteWhenNoGoalsConcededButGoalsScored() {
        TeamStanding standing = TeamStanding.unplayed("Carlisle United").withResult(2, 0, 2);
        assertEquals(Double.POSITIVE_INFINITY, standing.goalAverage());
    }

    @Test
    void goalAverageIsZeroWhenNoGoalsAtAll() {
        TeamStanding standing = TeamStanding.unplayed("Everton").withResult(0, 0, 1);
        assertEquals(0.0, standing.goalAverage());
    }

    @Test
    void originalStandingIsUnaffectedByWithResult() {
        TeamStanding original = TeamStanding.unplayed("Arsenal");
        TeamStanding updated = original.withResult(1, 0, 2);

        assertEquals(0, original.played());
        assertEquals(1, updated.played());
    }
}
