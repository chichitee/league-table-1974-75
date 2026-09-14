package leaguetable.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MatchTest {

    private static final LocalDate DATE = LocalDate.of(1974, 8, 17);

    @Test
    void identifiesHomeWin() {
        Match match = new Match(DATE, "Stoke City", "Leeds United", 3, 0);
        assertTrue(match.isHomeWin());
        assertFalse(match.isAwayWin());
        assertFalse(match.isDraw());
    }

    @Test
    void identifiesAwayWin() {
        Match match = new Match(DATE, "Birmingham City", "Middlesbrough", 0, 3);
        assertTrue(match.isAwayWin());
        assertFalse(match.isHomeWin());
        assertFalse(match.isDraw());
    }

    @Test
    void identifiesDraw() {
        Match match = new Match(DATE, "Everton", "Derby County", 0, 0);
        assertTrue(match.isDraw());
        assertFalse(match.isHomeWin());
        assertFalse(match.isAwayWin());
    }

    @Test
    void rejectsNegativeGoals() {
        assertThrows(IllegalArgumentException.class,
                () -> new Match(DATE, "Arsenal", "Chelsea", -1, 0));
        assertThrows(IllegalArgumentException.class,
                () -> new Match(DATE, "Arsenal", "Chelsea", 0, -1));
    }

    @Test
    void rejectsBlankTeamNames() {
        assertThrows(IllegalArgumentException.class,
                () -> new Match(DATE, "  ", "Chelsea", 1, 0));
        assertThrows(IllegalArgumentException.class,
                () -> new Match(DATE, "Arsenal", "", 1, 0));
    }

    @Test
    void rejectsNullValues() {
        assertThrows(NullPointerException.class,
                () -> new Match(null, "Arsenal", "Chelsea", 1, 0));
        assertThrows(NullPointerException.class,
                () -> new Match(DATE, null, "Chelsea", 1, 0));
    }

    @Test
    void rejectsATeamPlayingItself() {
        assertThrows(IllegalArgumentException.class,
                () -> new Match(DATE, "Arsenal", "Arsenal", 1, 1));
        assertThrows(IllegalArgumentException.class,
                () -> new Match(DATE, "Arsenal", "arsenal ", 1, 1));
    }

    @Test
    void trimsTeamNames() {
        Match match = new Match(DATE, "  Arsenal  ", " Chelsea ", 1, 0);
        assertEquals("Arsenal", match.homeTeam());
        assertEquals("Chelsea", match.awayTeam());
    }
}
