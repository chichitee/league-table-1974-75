package leaguetable.io;

import leaguetable.model.TeamStanding;
import org.junit.jupiter.api.Test;

import java.io.StringWriter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CsvStandingsWriterTest {

    private final CsvStandingsWriter writer = new CsvStandingsWriter();

    @Test
    void writesHeaderAndRowsInConventionalOrder() {
        TeamStanding ipswich = TeamStanding.unplayed("Ipswich Town").withResult(3, 1, 2);
        TeamStanding stoke = TeamStanding.unplayed("Stoke City").withResult(1, 3, 0);

        StringWriter out = new StringWriter();
        writer.write(List.of(ipswich, stoke), out);

        String[] lines = out.toString().split(System.lineSeparator());
        assertEquals("Position,Team,Played,Won,Drawn,Lost,GoalsFor,GoalsAgainst,GoalAverage,Points", lines[0]);
        assertEquals("1,Ipswich Town,1,1,0,0,3,1,3.000,2", lines[1]);
        assertEquals("2,Stoke City,1,0,0,1,1,3,0.333,0", lines[2]);
    }

    @Test
    void escapesTeamNamesContainingCommas() {
        TeamStanding team = TeamStanding.unplayed("Town, The").withResult(1, 0, 2);

        StringWriter out = new StringWriter();
        writer.write(List.of(team), out);

        assertEquals(true, out.toString().contains("\"Town, The\""));
    }

    @Test
    void formatsInfiniteGoalAverageAsInf() {
        TeamStanding team = TeamStanding.unplayed("Carlisle United").withResult(2, 0, 2);

        StringWriter out = new StringWriter();
        writer.write(List.of(team), out);

        assertEquals(true, out.toString().contains(",Inf,"));
    }

    @Test
    void emptyTableProducesJustTheHeader() {
        StringWriter out = new StringWriter();
        writer.write(List.of(), out);

        assertEquals("Position,Team,Played,Won,Drawn,Lost,GoalsFor,GoalsAgainst,GoalAverage,Points"
                + System.lineSeparator(), out.toString());
    }
}
